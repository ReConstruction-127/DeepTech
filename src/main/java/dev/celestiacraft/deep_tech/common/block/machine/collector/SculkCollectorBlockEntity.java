package dev.celestiacraft.deep_tech.common.block.machine.collector;

import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.widget.DraggableScrollableWidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.SlotWidget;
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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
 *   <li>通电(50 FE/t)后以自身为原点, 半径 16 格、上下 5 格高度范围内挖方块, 每秒 20 个(每 tick 1 个)</li>
 *   <li>6 个过滤槽(只标记不存储, 默认幽匿系): 空的过滤 = 挖一切, 有标记 = 只挖标记方块</li>
 *   <li>掉落由数据驱动配方(deep_tech:harvest)决定(支持概率), 无配方则按最高等级不带精准的方式挖掘</li>
 *   <li>81 格输入槽中的任意可放置物品用于回填被清除的方块, 留空则直接清空</li>
 *   <li>81 格输出槽存放挖掘产物</li>
 * </ul>
 */
public class SculkCollectorBlockEntity extends MachineBlockEntity<SculkCollectorBlockEntity> implements IUIHolder.BlockEntityUI {
	public static final int INPUT_SLOTS = 81;
	public static final int OUTPUT_SLOTS = 81;
	public static final int FILTER_SLOTS = 6;

	public static final int INPUT_START = 0;
	public static final int OUTPUT_START = INPUT_SLOTS;
	public static final int FILTER_START = INPUT_SLOTS + OUTPUT_SLOTS;
	public static final int TOTAL_SLOTS = FILTER_START + FILTER_SLOTS;

	public static final int RADIUS = 16;
	public static final int HEIGHT = 5;
	public static final int ENERGY_PER_HARVEST = 50;

	private static final int RANGE_XZ = RADIUS * 2 + 1;
	private static final int RANGE_Y = HEIGHT * 2 + 1;
	private static final int SCAN_VOLUME = RANGE_XZ * RANGE_XZ * RANGE_Y;

	private final SculkCollectorCapability caps = new SculkCollectorCapability(this);
	private int scanIndex = 0;

	public SculkCollectorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		initDefaultFilters();
	}

	/** 新机器默认过滤幽匿系方块(加载存档时会用 NBT 覆盖) */
	private void initDefaultFilters() {
		ItemStackHandler handler = getItemHandler();
		for (int i = 0; i < FILTER_SLOTS; i++) {
			if (!handler.getStackInSlot(FILTER_START + i).isEmpty()) {
				return;
			}
		}
		Block[] defaults = {
				Blocks.SCULK,
				Blocks.SCULK_VEIN,
				Blocks.SCULK_CATALYST,
				Blocks.SCULK_SENSOR,
				Blocks.SCULK_SHRIEKER
		};
		for (int i = 0; i < defaults.length && i < FILTER_SLOTS; i++) {
			handler.setStackInSlot(FILTER_START + i, new ItemStack(defaults[i]));
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
	public int getMaxMachineSlot() {
		return TOTAL_SLOTS;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack) {
		return slot >= INPUT_START && slot < OUTPUT_START && stack.getItem() instanceof BlockItem;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack) {
		return slot >= OUTPUT_START && slot < FILTER_START;
	}

	@Override
	public int getMachineSlotLimit(int slot) {
		return slot >= FILTER_START ? 1 : 64;
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
		BlockPos target = entity.scanNext(level, pos);
		if (target == null) {
			entity.setLit(level, pos, state, false);
			return;
		}
		entity.setLit(level, pos, state, true);

		List<ItemStack> drops = entity.computeDrops(level, target);
		if (!entity.canStoreAll(drops)) {
			return;
		}
		entity.storeAll(drops);
		entity.refillOrClear(level, target);

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

	/** 从游标开始扫描区域, 返回下一个可挖方块, 无则返回 null */
	private BlockPos scanNext(Level level, BlockPos origin) {
		for (int i = 0; i < SCAN_VOLUME; i++) {
			int idx = scanIndex;
			scanIndex = (scanIndex + 1) % SCAN_VOLUME;
			int dy = idx / (RANGE_XZ * RANGE_XZ);
			int rem = idx % (RANGE_XZ * RANGE_XZ);
			int dz = rem / RANGE_XZ;
			int dx = rem % RANGE_XZ;
			BlockPos pos = origin.offset(dx - RADIUS, dy - HEIGHT, dz - RADIUS);
			if (pos.equals(origin)) {
				continue;
			}
			BlockState state = level.getBlockState(pos);
			if (state.isAir() || !state.getFluidState().isEmpty()) {
				continue;
			}
			if (!matchesFilter(state)) {
				continue;
			}
			if (!isHarvestable(level, pos, state)) {
				continue;
			}
			return pos;
		}
		return null;
	}

	/** 过滤: 所有过滤槽为空 = 不过滤; 否则必须命中某个标记方块的物品 */
	private boolean matchesFilter(BlockState state) {
		List<Block> filters = new ArrayList<>();
		for (int i = 0; i < FILTER_SLOTS; i++) {
			ItemStack stack = getItemHandler().getStackInSlot(FILTER_START + i);
			if (!stack.isEmpty() && stack.getItem() instanceof BlockItem item) {
				filters.add(item.getBlock());
			}
		}
		if (filters.isEmpty()) {
			return true;
		}
		for (Block block : filters) {
			if (state.is(block)) {
				return true;
			}
		}
		return false;
	}

	private boolean isHarvestable(Level level, BlockPos pos, BlockState state) {
		for (HarvestRecipe recipe : level.getRecipeManager().getAllRecipesFor(DTRecipes.HARVEST.getRecipeType())) {
			if (recipe.matches(state, level)) {
				return true;
			}
		}
		if (level instanceof ServerLevel serverLevel) {
			return !HarvestDropHelper.defaultDrops(serverLevel, pos, state).isEmpty();
		}
		return false;
	}

	private List<ItemStack> computeDrops(Level level, BlockPos target) {
		BlockState state = level.getBlockState(target);
		HarvestRecipe recipe = null;
		for (HarvestRecipe r : level.getRecipeManager().getAllRecipesFor(DTRecipes.HARVEST.getRecipeType())) {
			if (r.matches(state, level)) {
				recipe = r;
				break;
			}
		}
		if (recipe != null) {
			return recipe.rollOutputs(level.getRandom());
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
	 * 将物品放入输出槽(81 格): 优先堆叠同物品槽, 其次空槽。
	 *
	 * @return 是否成功放入
	 */
	private boolean findOutputSpace(ItemStack stack, boolean simulate) {
		ItemStackHandler handler = getItemHandler();
		ItemStack remaining = stack.copy();
		for (int i = OUTPUT_START; i < FILTER_START; i++) {
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

	/** 清除目标方块; 输入槽中有可放置物品则回填, 否则留空 */
	private void refillOrClear(Level level, BlockPos target) {
		ItemStackHandler handler = getItemHandler();
		int pickedSlot = -1;
		for (int i = INPUT_START; i < OUTPUT_START; i++) {
			ItemStack stack = handler.getStackInSlot(i);
			if (!stack.isEmpty() && stack.getItem() instanceof BlockItem) {
				pickedSlot = i;
				break;
			}
		}
		if (pickedSlot >= 0) {
			ItemStack picked = handler.getStackInSlot(pickedSlot);
			level.setBlockAndUpdate(target, ((BlockItem) picked.getItem()).getBlock().defaultBlockState());
			picked.shrink(1);
			if (picked.isEmpty()) {
				handler.setStackInSlot(pickedSlot, ItemStack.EMPTY);
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

		// 6 个过滤标记槽(只标记不存储)
		for (int i = 0; i < FILTER_SLOTS; i++) {
			group.addWidget(createSlot(container, FILTER_START + i, 30 + i * 18, 25, true, true));
		}

		// 输入 9x9 / 输出 9x9, 可滚动
		DraggableScrollableWidgetGroup scroll = new DraggableScrollableWidgetGroup(7, 40, 162, 124);
		scroll.setYScrollBarWidth(3);
		scroll.addWidget(new LabelWidget(1, 1, Component.translatable("gui.deep_tech.sculk_collector.input")));
		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
				scroll.addWidget(createSlot(container, INPUT_START + col + row * 9, col * 18, row * 18 + 12, true, true));
			}
		}
		scroll.addWidget(new LabelWidget(1, 176, Component.translatable("gui.deep_tech.sculk_collector.output")));
		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
				scroll.addWidget(createSlot(container, OUTPUT_START + col + row * 9, col * 18, row * 18 + 188, false, true));
			}
		}
		group.addWidget(scroll);

		addPlayerSlots(group, player);
		return group;
	}

	private void addPlayerSlots(WidgetGroup group, Player player) {
		Container inventory = player.getInventory();
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 9; col++) {
				group.addWidget(createSlot(inventory, col + row * 9 + 9, 7 + col * 18, 166 + row * 18, true, true));
			}
		}
		for (int col = 0; col < 9; col++) {
			group.addWidget(createSlot(inventory, col, 7 + col * 18, 220, true, true));
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
}