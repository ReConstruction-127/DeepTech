package dev.celestiacraft.deep_tech.common.block.machine.advanced.collector;

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
import dev.celestiacraft.deep_tech.common.block.machine.advanced.collector.capability.SculkCollectorCapability;
import dev.celestiacraft.deep_tech.common.inventory.SimpleMachineInventory;
import dev.celestiacraft.deep_tech.common.recipe.harvest.HarvestRecipe;
import dev.celestiacraft.deep_tech.common.register.DTRecipes;
import dev.celestiacraft.deep_tech.common.register.block.MachineBlocks;
import dev.celestiacraft.deep_tech.config.common.machine.advanced.SculkCollectorConfig;
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

import java.util.List;

/**
 * 幽匿采集器:
 * <ul>
 *   <li>通入红石信号才工作; 每个 tick 按 config 速度(默认 1 块/tick)执行, 每块耗 config 电量(默认 200 FE);
 *       挖掘范围由 GUI 输入框实时调整(横向 XZ 半径 / 纵向向下深度); 挖除使用 level.destroyBlock
 *       (不产生世界掉落, 掉落一律入输出槽)</li>
 *   <li>不挖掘机器所在层, 深度从机器下一格开始计算</li>
 *   <li>中间两列过滤, 每行一一配对:
 *       左列 = 挖掘过滤(幽灵槽, 放入不消耗、点击即消失, 只标记不存储);
 *       右列 = 回填过滤(幽灵槽, 同样只标记, 指定该行挖掉后用右侧储存区中的何种方块回填;
 *       右列为空 = 挖掉留空; 储存区中找不到该回填方块(耗尽) = 跳过该行不再挖掘)</li>
 *   <li>三种工作模式:
 *       HARVEST(左列有过滤) = 挑选挖掘+按行回填;
 *       FILL(左列全空、右列标记全部相同方块) = 填充机: 把工作区域内的空气填充成对应方块,
 *       从最底层开始一层一层向上, 不挖掘;
 *       CLEAR(左右全部留空) = 清空机: 挖掉工作区域内全部方块留空, 从最上层开始一层一层向下;
 *       右列标记了不同方块(混用)则机器不工作</li>
 *   <li>右侧 3x3 储存区: 存放真实回填方块, 机器按回填过滤从中抽取自动放置到世界上; 也用能力向管道提供出入</li>
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
	public static final int DEFAULT_DEPTH = 5;
	public static final int MAX_RADIUS_XZ = 32;
	public static final int MAX_DEPTH = 16;

	private final SculkCollectorCapability caps = new SculkCollectorCapability(this);
	private final ItemStackHandler filterHandler = new ItemStackHandler(FILTER_SLOTS);
	private final ItemStackHandler backfillFilterHandler = new ItemStackHandler(FILTER_SLOTS);
	private int scanLayer = 0;
	private int scanXZ = 0;
	private int fillLayer = 0;
	private int fillXZ = 0;
	/** 空转冷却: 完整扫完一轮零命中后休眠的 tick 数, 避免每 tick 全量扫描空区域 */
	private static final int IDLE_RESCAN_TICKS = 20;
	private int rescanCooldown = 0;
	private int radiusXZ = DEFAULT_RADIUS_XZ;
	private int depth = DEFAULT_DEPTH;

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
				Blocks.SCULK_SHRIEKER
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

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = Math.max(1, Math.min(MAX_DEPTH, depth));
		setChanged();
	}

	public int getEnergyPerHarvest() {
		return SculkCollectorConfig.ENERGY_PER_HARVEST.get();
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
		tag.putInt("Depth", depth);
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
		if (tag.contains("Depth")) {
			depth = Math.max(1, Math.min(MAX_DEPTH, tag.getInt("Depth")));
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

	/** 工作模式: 左列有过滤 = 挑选挖掘+按行回填; 双空 = 全清空; 左列空右列同方块 = 填充空气; 右列方块混用 = 不工作 */
	private enum WorkMode {
		HARVEST, CLEAR, FILL, INVALID
	}

	@Override
	public void serverTick(Level level, BlockPos pos, BlockState state, SculkCollectorBlockEntity entity) {
		if (level.isClientSide()) {
			return;
		}
		if (level.getBestNeighborSignal(pos) <= 0) {
			entity.setLit(level, pos, state, false);
			return;
		}
		WorkMode mode = entity.computeWorkMode();
		if (mode == WorkMode.INVALID) {
			entity.setLit(level, pos, state, false);
			return;
		}
		if (entity.rescanCooldown > 0) {
			entity.rescanCooldown--;
			entity.setLit(level, pos, state, false);
			return;
		}
		int speed = SculkCollectorConfig.HARVEST_SPEED.get();
		int worked = 0;
		for (int t = 0; t < speed; t++) {
			if (entity.getEnergy() < entity.getEnergyPerHarvest()) {
				break;
			}
			if (mode == WorkMode.FILL) {
				BlockPos fillPos = entity.scanFill(level, pos);
				if (fillPos == null) {
					entity.rescanCooldown = IDLE_RESCAN_TICKS;
					break;
				}
				if (!entity.placeFill(level, fillPos)) {
					continue;
				}
			} else {
				ScanResult target = entity.scanNext(level, pos);
				if (target == null) {
					entity.rescanCooldown = IDLE_RESCAN_TICKS;
					break;
				}
				BlockPos targetPos = target.pos();
				List<ItemStack> drops = entity.computeDrops(level, targetPos);
				if (!entity.canStoreAll(drops)) {
					break;
				}
				entity.storeAll(drops);
				level.destroyBlock(targetPos, false);
				if (mode == WorkMode.HARVEST) {
					entity.refillOrClear(level, targetPos, target.row());
				}
			}
			entity.setEnergy(entity.getEnergy() - entity.getEnergyPerHarvest());
			worked++;
		}
		if (worked == 0) {
			entity.setLit(level, pos, state, false);
			return;
		}
		entity.setLit(level, pos, state, true);
		entity.setChanged();
		entity.setSyncCounter(entity.getSyncCounter() + 1);
		if (entity.getSyncCounter() % 5 == 0) {
			entity.sync();
		}
	}

	/** 根据左右过滤槽内容判定当前工作模式 */
	private WorkMode computeWorkMode() {
		boolean anyFilter = false;
		for (int i = 0; i < FILTER_SLOTS; i++) {
			if (!filterHandler.getStackInSlot(i).isEmpty()) {
				anyFilter = true;
				break;
			}
		}
		if (anyFilter) {
			return WorkMode.HARVEST;
		}
		ItemStack first = ItemStack.EMPTY;
		for (int i = 0; i < FILTER_SLOTS; i++) {
			ItemStack stack = backfillFilterHandler.getStackInSlot(i);
			if (stack.isEmpty()) {
				continue;
			}
			if (first.isEmpty()) {
				first = stack;
			} else if (!ItemStack.isSameItem(first, stack)) {
				return WorkMode.INVALID;
			}
		}
		return first.isEmpty() ? WorkMode.CLEAR : WorkMode.FILL;
	}

	private void setLit(Level level, BlockPos pos, BlockState state, boolean lit) {
		if (state.getValue(BasicBlock.LIT) != lit) {
			level.setBlockAndUpdate(pos, state.setValue(BasicBlock.LIT, lit));
		}
	}

	/**
	 * 扫描下一个可挖方块(HARVEST/CLEAR 模式): 不挖机器所在层, 深度从机器下一格开始。
	 * 逐层从上到下: 当前层(scanLayer)内的 XZ 位置(scanXZ)全部扫完才推进到下一层。
	 * 命中行若已耗尽(储存区无其回填方块且右列有标记)则跳过该方块不挖。
	 */
	private ScanResult scanNext(Level level, BlockPos origin) {
		int rangeXZ = radiusXZ * 2 + 1;
		int cells = rangeXZ * rangeXZ;
		for (int attempt = 0; attempt < depth; attempt++) {
			int y = origin.getY() - scanLayer - 1;
			for (; scanXZ < cells; scanXZ++) {
				int idx = scanXZ;
				int dz = idx / rangeXZ;
				int dx = idx % rangeXZ;
				BlockPos pos = new BlockPos(origin.getX() + dx - radiusXZ, y, origin.getZ() + dz - radiusXZ);
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
				scanXZ++;
				return new ScanResult(pos, row);
			}
			scanXZ = 0;
			scanLayer = (scanLayer + 1) % depth;
		}
		return null;
	}

	/**
	 * 扫描下一处需填充的空气(FILL 模式): 独立游标(fillLayer/fillXZ, 不与挖掘共享),
	 * 始终从最底层(fillLayer=0)开始, 当前层内全部扫完才推进到上一层。
	 */
	private BlockPos scanFill(Level level, BlockPos origin) {
		int rangeXZ = radiusXZ * 2 + 1;
		int cells = rangeXZ * rangeXZ;
		for (int attempt = 0; attempt < depth; attempt++) {
			int y = origin.getY() - (depth - fillLayer);
			for (; fillXZ < cells; fillXZ++) {
				int idx = fillXZ;
				int dz = idx / rangeXZ;
				int dx = idx % rangeXZ;
				BlockPos pos = new BlockPos(origin.getX() + dx - radiusXZ, y, origin.getZ() + dz - radiusXZ);
				if (level.isOutsideBuildHeight(pos) || !level.getWorldBorder().isWithinBounds(pos)) {
					continue;
				}
				if (level.getBlockState(pos).isAir()) {
					fillXZ++;
					return pos;
				}
			}
			fillXZ = 0;
			fillLayer = (fillLayer + 1) % depth;
		}
		return null;
	}

	/** 填充机要放置的方块(右列全部相同的过滤标记) */
	private ItemStack getFillBlock() {
		for (int i = 0; i < FILTER_SLOTS; i++) {
			ItemStack stack = backfillFilterHandler.getStackInSlot(i);
			if (!stack.isEmpty()) {
				return stack;
			}
		}
		return ItemStack.EMPTY;
	}

	/** 从右侧储存区抽取回填方块放置到目标空气位置, 无货则不放置 */
	private boolean placeFill(Level level, BlockPos pos) {
		ItemStack fillFilter = getFillBlock();
		if (fillFilter.isEmpty()) {
			return false;
		}
		ItemStackHandler handler = getItemHandler();
		for (int i = INPUT_START; i < INPUT_START + INPUT_SLOTS; i++) {
			ItemStack stack = handler.getStackInSlot(i);
			if (!stack.isEmpty() && ItemStack.isSameItem(fillFilter, stack)) {
				level.setBlockAndUpdate(pos, ((BlockItem) stack.getItem()).getBlock().defaultBlockState());
				stack.shrink(1);
				if (stack.isEmpty()) {
					handler.setStackInSlot(i, ItemStack.EMPTY);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 返回该方块命中的第一个有效过滤行(左列), 无效返回 -1。
	 * 行有效 = 右列回填过滤为空(留空), 或右侧储存区中仍有该回填方块; 耗尽的行视为无效。
	 * 左列全部留空(清空模式) = 全部方块都可挖, 无回填行。
	 */
	private int matchedFilterRow(BlockState state) {
		boolean anyFilter = false;
		for (int i = 0; i < FILTER_SLOTS; i++) {
			if (!filterHandler.getStackInSlot(i).isEmpty()) {
				anyFilter = true;
				break;
			}
		}
		if (!anyFilter) {
			return FILTER_SLOTS - 1;
		}
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
		return new ModularUI(194, 195, this, player).widget(group);
	}

	private WidgetGroup createUIWidget(Player player) {
		WidgetGroup group = new WidgetGroup(0, 0, 194, 195);
		group.setBackground(new ResourceTexture(DeepTech.loadGui("sculk_collector")));

		LabelWidget title = new LabelWidget(28, 8, MachineBlocks.SCULK_COLLECTOR.get().getName());
		title.setColor(0xFF97CCD6);
		group.addWidget(title);

		group.addWidget(new EnergyBarWidget(7, 25, this::getEnergyStored, getMaxEnergyStored()));

		SimpleMachineInventory container = new SimpleMachineInventory(getItemHandler());

		// 左侧 3x3 输出槽(挖掘产物)
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				group.addWidget(createSlot(container, OUTPUT_START + col + row * 3, 27 + col * 18, 25 + row * 18, false, true));
			}
		}

		// 中间两列过滤, 每行配对: 左列幽灵挖掘过滤 + 右列幽灵回填过滤(从右侧储存区抽取回填)
		for (int row = 0; row < 3; row++) {
			group.addWidget(new FilterSlotWidget(filterHandler, row, 83, 25 + row * 18));
			group.addWidget(new FilterSlotWidget(backfillFilterHandler, row, 115, 25 + row * 18));
		}

		// 右侧 3x3 储存区(回填方块真实存储, 按右列回填过滤自动抽取放置)
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				group.addWidget(createSlot(container, INPUT_START + col + row * 3, 135 + col * 18, 25 + row * 18, true, true));
			}
		}

		// 挖掘范围(两列之间空列的下方)
		LabelWidget radiusXzLabel = new LabelWidget(28, 84, Component.literal("↔"));
		radiusXzLabel.setColor(0xFF97CCD6);
		group.addWidget(radiusXzLabel);
		TextFieldWidget radiusXzField = new TextFieldWidget(37, 83, 30, 12,
				() -> String.valueOf(getRadiusXZ()),
				text -> setRadiusXZ(parseClamped(text, getRadiusXZ())));
		radiusXzField.setValidator(str -> str.replaceAll("[^0-9]", ""));
		radiusXzField.setMaxStringLength(2);
		group.addWidget(radiusXzField);

		LabelWidget depthLabel = new LabelWidget(28, 97, Component.literal("▼"));
		depthLabel.setColor(0xFF97CCD6);
		group.addWidget(depthLabel);
		TextFieldWidget depthField = new TextFieldWidget(37, 96, 30, 12,
				() -> String.valueOf(getDepth()),
				text -> setDepth(parseClamped(text, getDepth())));
		depthField.setValidator(str -> str.replaceAll("[^0-9]", ""));
		depthField.setMaxStringLength(2);
		group.addWidget(depthField);

		addPlayerSlots(group, player);
		return group;
	}

	private void addPlayerSlots(WidgetGroup group, Player player) {
		Container inventory = player.getInventory();
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 9; col++) {
				SlotWidget widget = createSlot(inventory, col + row * 9 + 9, 27 + col * 18, 112 + row * 18, true, true);
				widget.setLocationInfo(true, false);
				group.addWidget(widget);
			}
		}
		for (int col = 0; col < 9; col++) {
			SlotWidget widget = createSlot(inventory, col, 27 + col * 18, 170, true, true);
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
			// 背景格子已画进 GUI 大图, 不再绘制槽位背景
			setBackgroundTexture(null);
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