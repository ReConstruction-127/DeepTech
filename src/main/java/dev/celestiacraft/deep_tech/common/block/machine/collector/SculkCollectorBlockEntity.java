package dev.celestiacraft.deep_tech.common.block.machine.collector;

import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.SlotWidget;
import com.lowdragmc.lowdraglib.gui.widget.TextFieldWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Position;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.api.block.machine.MachineBlockEntity;
import dev.celestiacraft.deep_tech.api.gui.widget.EnergyBarWidget;
import dev.celestiacraft.deep_tech.common.block.machine.collector.capability.SculkCollectorCapability;
import dev.celestiacraft.deep_tech.common.inventory.SimpleMachineInventory;
import dev.celestiacraft.deep_tech.common.recipe.harvest.HarvestRecipe;
import dev.celestiacraft.deep_tech.common.register.DTRecipes;
import dev.celestiacraft.deep_tech.common.register.block.MachineBlocks;
import dev.celestiacraft.deep_tech.config.common.machine.SculkCollectorConfig;
import dev.celestiacraft.libs.api.register.block.BasicBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 幽匿采集器:
 * <ul>
 *   <li>通电(50 FE/t)后以自身为原点按游标扫描周围方块, 每 tick 挖 1 个; 挖掘范围可由 GUI 输入框实时调整</li>
 *   <li>中间两列过滤, 每行一一配对:
 *       左列 = 挖掘过滤(幽灵槽, 放入不消耗、点击即消失, 只标记不存储);
 *       右列 = 回填过滤(幽灵槽, 同样只标记, 指定该行挖掉后用右侧储存区中的何种方块回填;
 *       ：右列为空 = 挖掉留空; 储存区中找不到该回填方块(耗尽) = 跳过该行不再挖掘)</li>
 *   <li>右侧 3x3 储存区: 存放真实回填方块, 机器按右列回填过滤从中抽取自动放置到世界上; 也用能力向管道提供出入</li>
 *   <li>左侧 3x3 输出槽存放挖掘产物</li>
 *   <li>掉落由数据驱动配方(deep_tech:harvest)决定(支持概率), 无配方则直接按战利品表(钻石镐无限, 无精准)产出</li>
 *   <li>不可破坏方块(如基岩, 破坏速度 &lt; 0)不会挖掘</li>
 * </ul>
 */
public class SculkCollectorBlockEntity extends MachineBlockEntity<SculkCollectorBlockEntity> implements IUIHolder.BlockEntityUI {
	public static final int INPUT_SLOTS = 9;
	public static final int OUTPUT_SLOTS = 9;
	public static final int FILTER_SLOTS = 3;

	public static final int INPUT_START = 0;
	public static final int OUTPUT_START = INPUT_SLOTS;
	public static final int TOTAL_SLOTS = OUTPUT_START + OUTPUT_SLOTS;

	public static final int DEFAULT_RADIUS_XZ = 16;
	public static final int DEFAULT_RADIUS_Y = 5;
	public static final int MAX_RADIUS_XZ = 32;
	public static final int MAX_RADIUS_Y = 16;
	public static final int ENERGY_PER_HARVEST = 50;

	private final SculkCollectorCapability caps = new SculkCollectorCapability(this);
	private final ItemStackHandler filterHandler = new ItemStackHandler(FILTER_SLOTS);
	private final ItemStackHandler backfillFilterHandler = new ItemStackHandler(FILTER_SLOTS);
	private int scanIndex = 0;
	private int radiusXZ = DEFAULT_RADIUS_XZ;
	private int radiusY = DEFAULT_RADIUS_Y;

	public SculkCollectorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		initDefaultFilters();
	}

	/** 新机器默认过滤幽匿系方块(加载存档时会用 NBT 覆盖) */
	private void initDefaultFilters() {
		for (int i = 0; i < FILTER_SLOTS; i++) {
			if (!filterHandler.getStackInSlot(i).isEmpty()) {
				return;
			}
		}
		Block[] defaults = {
				Blocks.SCULK,
				Blocks.SCULK_VEIN,
				Blocks.SCULK_CATALYST
		};
		for (int i = 0; i < defaults.length && i < FILTER_SLOTS; i++) {
			filterHandler.setStackInSlot(i, new ItemStack(defaults[i]));
		}
	}

	// ---------------- 挖掘范围 ----------------

	public int getRadiusXZ() {
		return radiusXZ;
	}

	public void setRadiusXZ(int radiusXZ) {
		this.radiusXZ = Math.max(1, Math.min(MAX_RADIUS_XZ, radiusXZ));
		setChanged();
	}

	public int getRadiusY() {
		return radiusY;
	}

	public void setRadiusY(int radiusY) {
		this.radiusY = Math.max(1, Math.min(MAX_RADIUS_Y, radiusY));
		setChanged();
	}

	private static int parseClamped(String text, int fallback) {
		try {
			return Integer.parseInt(text.trim());
		} catch (NumberFormatException e) {
			return fallback;
		}
	}

	// ---------------- 能量配置 ----------------

	@Override
	public int getMachineMaxEnergy() {
		return SculkCollectorConfig.MAX_ENERGY.get();
	}

	@Override
	public int getMaxReceive() {
		return SculkCollectorConfig.MAX_RECEIVE.get();
	}

	// ---------------- 物品槽配置 ----------------

	@Override
	public int getItemInputSlotCount() {
		return INPUT_SLOTS;
	}

	@Override
	public int getItemOutputSlotCount() {
		return OUTPUT_SLOTS;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack) {
		return slot < INPUT_SLOTS && stack.getItem() instanceof BlockItem;
	}

	// ---------------- NBT ----------------

	@Override
	protected void saveAdditional(@NotNull CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("Filters", filterHandler.serializeNBT());
		tag.put("BackfillFilters", backfillFilterHandler.serializeNBT());
		tag.putInt("RadiusXZ", radiusXZ);
		tag.putInt("RadiusY", radiusY);
	}

	@Override
	public void load(@NotNull CompoundTag tag) {
		super.load(tag);
		if (tag.contains("Filters")) {
			filterHandler.deserializeNBT(tag.getCompound("Filters"));
		}
		if (tag.contains("BackfillFilters")) {
			backfillFilterHandler.deserializeNBT(tag.getCompound("BackfillFilters"));
		}
		if (tag.contains("RadiusXZ")) {
			radiusXZ = Math.max(1, Math.min(MAX_RADIUS_XZ, tag.getInt("RadiusXZ")));
		}
		if (tag.contains("RadiusY")) {
			radiusY = Math.max(1, Math.min(MAX_RADIUS_Y, tag.getInt("RadiusY")));
		}
	}

	// ---------------- Capability(单独类) ----------------

	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, Direction side) {
		return caps.get(capability, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		caps.invalidate();
	}

	// ---------------- 工作逻辑 ----------------

	@Override
	public void serverTick(Level level, BlockPos pos, BlockState state, SculkCollectorBlockEntity entity) {
		if (level.isClientSide()) {
			return;
		}
		if (entity.getEnergy() < ENERGY_PER_HARVEST) {
			entity.setLit(level, pos, state, false);
			return;
		}
		ScanResult target = entity.scanNext(level, pos);
		if (target == null) {
			entity.setLit(level, pos, state, false);
			return;
		}
		entity.setLit(level, pos, state, true);

		BlockPos targetPos = target.pos();
		List<ItemStack> drops = entity.computeDrops(level, targetPos);
		if (!entity.canStoreAll(drops)) {
			return;
		}
		entity.storeAll(drops);
		entity.refillOrClear(level, targetPos, target.row());

		entity.setEnergy(entity.getEnergy() - ENERGY_PER_HARVEST);
		entity.setChanged();
		entity.setSyncCounter(entity.getSyncCounter() + 1);
		if (entity.getSyncCounter() % 5 == 0) {
			entity.sync();
		}
	}

	private void setLit(Level level, BlockPos pos, BlockState state, boolean lit) {
		if (state.getValue(BasicBlock.LIT) != lit) {
			level.setBlockAndUpdate(pos, state.setValue(BasicBlock.LIT, lit));
		}
	}

	/**
	 * 从游标开始扫描当前范围, 返回下一个可挖方块及其配对的回填行, 无则返回 null。
	 * 命中行若已耗尽(fillExhausted)则跳过该方块不挖。
	 */
	private ScanResult scanNext(Level level, BlockPos origin) {
		int rangeXZ = radiusXZ * 2 + 1;
		int rangeY = radiusY * 2 + 1;
		int volume = rangeXZ * rangeXZ * rangeY;
		for (int i = 0; i < volume; i++) {
			int idx = scanIndex;
			scanIndex = (scanIndex + 1) % volume;
			int dy = idx / (rangeXZ * rangeXZ);
			int rem = idx % (rangeXZ * rangeXZ);
			int dz = rem / rangeXZ;
			int dx = rem % rangeXZ;
			BlockPos pos = origin.offset(dx - radiusXZ, dy - radiusY, dz - radiusXZ);
			if (pos.equals(origin)) {
				continue;
			}
			BlockState state = level.getBlockState(pos);
			if (state.isAir() || !state.getFluidState().isEmpty()) {
				continue;
			}
			int row = matchedFilterRow(state);
			if (row < 0) {
				continue;
			}
			if (!isHarvestable(level, pos, state)) {
				continue;
			}
			return new ScanResult(pos, row);
		}
		return null;
	}

	/**
	 * 返回该方块命中的第一个有效过滤行(左列), 无效返回 -1。
	 * 行有效 = 右列回填过滤为空(留空), 或右侧储存区中仍有该回填方块; 耗尽的行视为无效。
	 */
	private int matchedFilterRow(BlockState state) {
		for (int i = 0; i < FILTER_SLOTS; i++) {
			ItemStack filter = filterHandler.getStackInSlot(i);
			if (!filter.isEmpty() && filter.getItem() instanceof BlockItem item && state.is(item.getBlock())) {
				if (isBackfillRowAvailable(i)) {
					return i;
				}
				return -1;
			}
		}
		return -1;
	}

	/** 行是否有效: 右列回填过滤为空(留空), 或右侧储存区仍能找到对应的回填方块 */
	private boolean isBackfillRowAvailable(int row) {
		ItemStack fillFilter = backfillFilterHandler.getStackInSlot(row);
		if (fillFilter.isEmpty()) {
			return true;
		}
		ItemStackHandler handler = getItemHandler();
		for (int i = INPUT_START; i < INPUT_START + INPUT_SLOTS; i++) {
			ItemStack stack = handler.getStackInSlot(i);
			if (!stack.isEmpty() && ItemStack.isSameItem(fillFilter, stack)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 是否可挖掘: 过滤命中的方块只要能破坏(破坏速度 >= 0, 如基岩为 -1)即可清除。
	 * 掉落为空也照常清除, 避免"无掉落 = 不挖"的误判。
	 */
	private boolean isHarvestable(Level level, BlockPos pos, BlockState state) {
		return state.getDestroySpeed(level, pos) >= 0;
	}

	private List<ItemStack> computeDrops(Level level, BlockPos target) {
		BlockState state = level.getBlockState(target);
		for (HarvestRecipe r : level.getRecipeManager().getAllRecipesFor(DTRecipes.HARVEST.getRecipeType())) {
			if (r.matches(state, level)) {
				return r.rollOutputs(level.getRandom());
			}
		}
		if (level instanceof ServerLevel serverLevel) {
			return HarvestDropHelper.defaultDrops(serverLevel, target, state);
		}
		return List.of();
	}

	/** 输出槽是否放得下全部掉落 */
	private boolean canStoreAll(List<ItemStack> drops) {
		for (ItemStack drop : drops) {
			if (!findOutputSpace(drop, true)) {
				return false;
			}
		}
		return true;
	}

	private void storeAll(List<ItemStack> drops) {
		for (ItemStack drop : drops) {
			findOutputSpace(drop, false);
		}
	}

	/**
	 * 将物品放入输出槽: 优先堆叠同物品槽, 其次空槽。
	 *
	 * @return 是否成功放入
	 */
	private boolean findOutputSpace(ItemStack stack, boolean simulate) {
		ItemStackHandler handler = getItemHandler();
		ItemStack remaining = stack.copy();
		for (int i = OUTPUT_START; i < TOTAL_SLOTS; i++) {
			ItemStack current = handler.getStackInSlot(i);
			if (current.isEmpty()) {
				if (!simulate) {
					handler.setStackInSlot(i, remaining);
				}
				return true;
			}
			if (ItemStack.isSameItemSameTags(current, remaining)
					&& current.getCount() + remaining.getCount() <= 64) {
				if (!simulate) {
					current.grow(remaining.getCount());
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 清除目标方块: 右列回填过滤指定了方块则从右侧储存区抽取同种物品放置;
	 * 右列为空(留空配置)则放空气。
	 */
	private void refillOrClear(Level level, BlockPos target, int row) {
		ItemStackHandler handler = getItemHandler();
		ItemStack fillFilter = backfillFilterHandler.getStackInSlot(row);
		int picked = -1;
		if (!fillFilter.isEmpty()) {
			for (int i = INPUT_START; i < INPUT_START + INPUT_SLOTS; i++) {
				ItemStack stack = handler.getStackInSlot(i);
				if (!stack.isEmpty() && ItemStack.isSameItem(fillFilter, stack)) {
					picked = i;
					break;
				}
			}
		}
		if (picked >= 0) {
			ItemStack pickedStack = handler.getStackInSlot(picked);
			level.setBlockAndUpdate(target, ((BlockItem) pickedStack.getItem()).getBlock().defaultBlockState());
			pickedStack.shrink(1);
			if (pickedStack.isEmpty()) {
				handler.setStackInSlot(picked, ItemStack.EMPTY);
			}
		} else {
			level.setBlockAndUpdate(target, Blocks.AIR.defaultBlockState());
		}
	}

	// ---------------- GUI ----------------

	@Override
	public ModularUI createUI(Player player) {
		WidgetGroup group = createUIWidget(player);
		return new ModularUI(176, 240, this, player).widget(group);
	}

	private WidgetGroup createUIWidget(Player player) {
		WidgetGroup group = new WidgetGroup(0, 0, 176, 240);
		group.setBackground(new ResourceTexture(DeepTech.loadGui("sculk_collector")));

		LabelWidget title = new LabelWidget(8, 8, MachineBlocks.SCULK_COLLECTOR.get().getName());
		title.setColor(0xFF5D5F60);
		group.addWidget(title);

		group.addWidget(new EnergyBarWidget(7, 25, this::getEnergyStored, getMaxEnergyStored()));

		SimpleMachineInventory container = new SimpleMachineInventory(getItemHandler());

		// 左侧 3x3 输出槽(挖掘产物)
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				group.addWidget(createSlot(container, OUTPUT_START + col + row * 3, 7 + col * 18, 74 + row * 18, false, true));
			}
		}

		// 中间两列过滤, 每行配对: 左列幽灵挖掘过滤 + 右列幽灵回填过滤(从右侧储存区抽取回填)
		for (int row = 0; row < 3; row++) {
			group.addWidget(new FilterSlotWidget(filterHandler, row, 63, 74 + row * 18));
			group.addWidget(new FilterSlotWidget(backfillFilterHandler, row, 97, 74 + row * 18));
		}

		// 右侧 3x3 储存区(回填方块真实存储, 按右列回填过滤自动抽取放置)
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				group.addWidget(createSlot(container, INPUT_START + col + row * 3, 115 + col * 18, 74 + row * 18, true, true));
			}
		}

		// 挖掘范围(两列之间空列的下方)
		LabelWidget radiusXzLabel = new LabelWidget(7, 122, Component.literal("横向"));
		radiusXzLabel.setColor(0xFF5D5F60);
		group.addWidget(radiusXzLabel);
		TextFieldWidget radiusXzField = new TextFieldWidget(31, 121, 30, 12,
				() -> String.valueOf(getRadiusXZ()),
				text -> setRadiusXZ(parseClamped(text, getRadiusXZ())));
		radiusXzField.setValidator(str -> str.replaceAll("[^0-9]", ""));
		radiusXzField.setMaxStringLength(2);
		group.addWidget(radiusXzField);

		LabelWidget radiusYLabel = new LabelWidget(79, 122, Component.literal("纵向"));
		radiusYLabel.setColor(0xFF5D5F60);
		group.addWidget(radiusYLabel);
		TextFieldWidget radiusYField = new TextFieldWidget(103, 121, 30, 12,
				() -> String.valueOf(getRadiusY()),
				text -> setRadiusY(parseClamped(text, getRadiusY())));
		radiusYField.setValidator(str -> str.replaceAll("[^0-9]", ""));
		radiusYField.setMaxStringLength(2);
		group.addWidget(radiusYField);

		addPlayerSlots(group, player);
		return group;
	}

	private void addPlayerSlots(WidgetGroup group, Player player) {
		Container inventory = player.getInventory();
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 9; col++) {
				SlotWidget widget = createSlot(inventory, col + row * 9 + 9, 7 + col * 18, 136 + row * 18, true, true);
				widget.setLocationInfo(true, false);
				group.addWidget(widget);
			}
		}
		for (int col = 0; col < 9; col++) {
			SlotWidget widget = createSlot(inventory, col, 7 + col * 18, 190, true, true);
			widget.setLocationInfo(true, true);
			group.addWidget(widget);
		}
	}

	private static SlotWidget createSlot(Container container, int index, int x, int y, boolean canPut, boolean canTake) {
		SlotWidget widget = new SlotWidget();
		widget.initTemplate();
		widget.setContainerSlot(container, index);
		widget.setSelfPosition(new Position(x, y));
		widget.setBackground((ResourceTexture) null);
		widget.setCanPutItems(canPut);
		widget.setCanTakeItems(canTake);
		return widget;
	}

	/** 扫描命中结果: 目标方块 + 其配对的回填行 */
	private record ScanResult(BlockPos pos, int row) {
	}

	/**
	 * 幽灵过滤槽: 放入物品仅把手中物品复制为标记(不消耗), 点击已标记槽标记立即消失(不产出任何物品)。
	 * 标记数据通过 client action 同步到服务端。
	 * 两列共用: 左列绑定 filterHandler(挖掘过滤), 右列绑定 backfillFilterHandler(回填过滤)。
	 */
	private static class FilterSlotWidget extends SlotWidget {
		private final ItemStackHandler filterHandler;
		private final int index;

		public FilterSlotWidget(ItemStackHandler filterHandler, int index, int x, int y) {
			this.filterHandler = filterHandler;
			this.index = index;
			initTemplate();
			setContainerSlot(new SimpleMachineInventory(filterHandler), index);
			setSelfPosition(new Position(x, y));
			setCanPutItems(true);
			setCanTakeItems(true);
		}

		/**
		 * 槽注册进菜单后立即移除(换成空占位): 过滤槽不参与双击收拢/快速移动/交换等
		 * 任何菜单级物品操作, 杜绝标记被收走造成复制; 渲染仍直接读 filterHandler, 不受影响。
		 */
		@Override
		public void initWidget() {
			super.initWidget();
			if (slotReference != null && getGui() != null) {
				getGui().removeNativeSlot(slotReference);
			}
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (isMouseOverElement(mouseX, mouseY) && gui != null) {
				ItemStack current = filterHandler.getStackInSlot(index);
				if (!current.isEmpty()) {
					filterHandler.setStackInSlot(index, ItemStack.EMPTY);
					writeClientAction(1, buffer -> buffer.writeItemStack(ItemStack.EMPTY, false));
					return true;
				}
				Player player = Minecraft.getInstance().player;
				ItemStack finger = player != null ? player.containerMenu.getCarried() : ItemStack.EMPTY;
				if (!finger.isEmpty()) {
					ItemStack marker = finger.copy();
					marker.setCount(1);
					filterHandler.setStackInSlot(index, marker);
					writeClientAction(1, buffer -> buffer.writeItemStack(marker, false));
					return true;
				}
			}
			return false;
		}

		@Override
		public void handleClientAction(int id, FriendlyByteBuf buffer) {
			super.handleClientAction(id, buffer);
			if (id == 1) {
				filterHandler.setStackInSlot(index, buffer.readItem());
				onSlotChanged();
			}
		}

		/** 打开 GUI 时客户端从服务端拉取当前标记真值, 保证重启/重进后显示与服务器一致 */
		@Override
		public void writeInitialData(FriendlyByteBuf buffer) {
			super.writeInitialData(buffer);
			buffer.writeItemStack(filterHandler.getStackInSlot(index), false);
		}

		@Override
		public void readInitialData(FriendlyByteBuf buffer) {
			super.readInitialData(buffer);
			filterHandler.setStackInSlot(index, buffer.readItem());
		}

		/**
		 * 阻止基类把携带中的物品经 superMouseReleased 放入槽内(会吞掉手上物品)。
		 * 按下(标记/清除)已由 mouseClicked 处理, 松开不再做任何容器交互。
		 */
		@Override
		public boolean mouseReleased(double mouseX, double mouseY, int button) {
			return isMouseOverElement(mouseX, mouseY) && gui != null;
		}

		/** 阻止拖拽分裂等把物品匀入槽内的容器交互 */
		@Override
		public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
			return isMouseOverElement(mouseX, mouseY) && gui != null;
		}
	}
}