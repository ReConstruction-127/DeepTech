package dev.celestiacraft.deep_tech.common.block.machine.sculk_network.accessor;

import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.TextFieldWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.common.block.machine.sculk_network.port.SNHelper;
import dev.celestiacraft.deep_tech.common.block.machine.sculk_network.reservoir.SNFluidReservoirBlockEntity;
import dev.celestiacraft.deep_tech.common.block.machine.sculk_network.reservoir.SNItemReservoirBlockEntity;
import dev.celestiacraft.deep_tech.common.register.block.MachineBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * 幽匿网络访问器:汇总所在网络中所有储存器(物品/流体)的内容,
 * 供 UI 端的列表控件读取并同步给客户端展示。
 * <p>
 * 网络发现方式与中枢一致:BFS 沿网络组件扩展(半径 16 格),
 * 不过度依赖中枢的扫描结果,访问器自身即可定位储存器。
 */
public class SNAccessorBlockEntity extends BlockEntity implements IUIHolder.BlockEntityUI {

	public record ItemEntry(ItemStack stack, long count) {
	}

	public record FluidEntry(FluidStack stack, long amount) {
	}

	/** 汇总刷新间隔(tick) */
	private static final int REFRESH_INTERVAL = 20;

	// ========== 汇总结果(服务端维护) ==========
	private final List<ItemEntry> itemEntries = new ArrayList<>();
	private final List<FluidEntry> fluidEntries = new ArrayList<>();
	private long nextRefreshTime = Long.MIN_VALUE;

	public SNAccessorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// ============================================================
	//  Tick(驱动汇总缓存刷新)
	// ============================================================

	public static void tick(Level level, BlockPos pos, BlockState state, SNAccessorBlockEntity be) {
		if (level.isClientSide) {
			return;
		}
		be.refreshIfNeeded();
	}

	/**
	 * 间隔刷新网络汇总数据。UI 列表控件也会主动调用,保证打开界面时数据新鲜。
	 */
	public void refreshIfNeeded() {
		if (level == null || level.isClientSide) {
			return;
		}
		if (level.getGameTime() >= nextRefreshTime) {
			refresh();
		}
	}

	private void refresh() {
		nextRefreshTime = level.getGameTime() + REFRESH_INTERVAL;
		itemEntries.clear();
		fluidEntries.clear();

		// BFS 收集网络中的储存器
		Queue<BlockPos> queue = new ArrayDeque<>();
		Set<BlockPos> visited = new HashSet<>();
		queue.add(worldPosition);
		visited.add(worldPosition);

		List<SNItemReservoirBlockEntity> itemReservoirs = new ArrayList<>();
		List<SNFluidReservoirBlockEntity> fluidReservoirs = new ArrayList<>();

		while (!queue.isEmpty()) {
			BlockPos pos = queue.poll();
			if (!level.isLoaded(pos)) {
				continue;
			}
			if (level.getBlockState(pos).getBlock() == MachineBlocks.SN_ITEM_RESERVOIR.get()) {
				if (level.getBlockEntity(pos) instanceof SNItemReservoirBlockEntity reservoir) {
					itemReservoirs.add(reservoir);
				}
			}
			if (level.getBlockState(pos).getBlock() == MachineBlocks.SN_FLUID_RESERVOIR.get()) {
				if (level.getBlockEntity(pos) instanceof SNFluidReservoirBlockEntity reservoir) {
					fluidReservoirs.add(reservoir);
				}
			}

			if (pos.distSqr(worldPosition) >= 16 * 16) {
				continue;
			}
			for (var dir : net.minecraft.core.Direction.values()) {
				BlockPos neighbor = pos.relative(dir);
				if (!visited.contains(neighbor) && SNHelper.isNetworkComponent(level, neighbor)) {
					visited.add(neighbor);
					queue.add(neighbor);
				}
			}
		}

		// 聚合物品:按物品 ID 合并数量,图标取首个遇到的堆
		Map<net.minecraft.world.item.Item, long[]> itemCounts = new HashMap<>();
		Map<net.minecraft.world.item.Item, ItemStack> itemIcons = new HashMap<>();
		for (SNItemReservoirBlockEntity reservoir : itemReservoirs) {
			IItemHandler handler = reservoir.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
			if (handler == null) {
				continue;
			}
			for (int slot = 0; slot < handler.getSlots(); slot++) {
				ItemStack stack = handler.getStackInSlot(slot);
				if (stack.isEmpty()) {
					continue;
				}
				itemCounts.computeIfAbsent(stack.getItem(), k -> new long[]{0})[0] += stack.getCount();
				itemIcons.putIfAbsent(stack.getItem(), stack.copy());
			}
		}
		for (var entry : itemCounts.entrySet()) {
			ItemStack icon = itemIcons.get(entry.getKey());
			if (icon == null) {
				continue;
			}
			itemEntries.add(new ItemEntry(icon, entry.getValue()[0]));
		}

		// 聚合流体:按流体 ID 合并数量
		Map<net.minecraft.world.level.material.Fluid, long[]> fluidCounts = new HashMap<>();
		Map<net.minecraft.world.level.material.Fluid, FluidStack> fluidIcons = new HashMap<>();
		for (SNFluidReservoirBlockEntity reservoir : fluidReservoirs) {
			IFluidHandler handler = reservoir.getTank();
			if (handler == null) {
				continue;
			}
			for (int tank = 0; tank < handler.getTanks(); tank++) {
				FluidStack stack = handler.getFluidInTank(tank);
				if (stack.isEmpty()) {
					continue;
				}
				fluidCounts.computeIfAbsent(stack.getFluid(), k -> new long[]{0})[0] += stack.getAmount();
				fluidIcons.putIfAbsent(stack.getFluid(), stack.copy());
			}
		}
		for (var entry : fluidCounts.entrySet()) {
			FluidStack icon = fluidIcons.get(entry.getKey());
			if (icon == null) {
				continue;
			}
			fluidEntries.add(new FluidEntry(icon, entry.getValue()[0]));
		}
	}

	public List<ItemEntry> getItemEntries() {
		return itemEntries;
	}

	public List<FluidEntry> getFluidEntries() {
		return fluidEntries;
	}

	// ============================================================
	//  LDLib GUI
	// ============================================================

	@Override
	public ModularUI createUI(Player player) {
		ModularUI ui = new ModularUI(176, 222, this, player);
		ui.widget(createUIWidget(player));
		return ui;
	}

	private WidgetGroup createUIWidget(Player player) {
		WidgetGroup group = new WidgetGroup(0, 0, 176, 222);
		group.setBackground(new ResourceTexture(DeepTech.loadGui("accessor")));

		LabelWidget title = new LabelWidget(8, 8, MachineBlocks.SN_ACCESSOR.get().getName());
		title.setColor(0xFF5D5F60);
		group.addWidget(title);

		// 左侧:物品列表 + 搜索栏
		SNAccessorListWidget itemList = new SNAccessorListWidget(this, SNAccessorListWidget.Kind.ITEMS, 8, 36, 76, 84);
		group.addWidget(new TextFieldWidget(8, 20, 76, 12, () -> itemList.getFilter(), itemList::setFilter).setBordered(true));
		group.addWidget(itemList);

		// 右侧:流体列表 + 搜索栏
		SNAccessorListWidget fluidList = new SNAccessorListWidget(this, SNAccessorListWidget.Kind.FLUIDS, 92, 36, 76, 84);
		group.addWidget(new TextFieldWidget(92, 20, 76, 12, () -> fluidList.getFilter(), fluidList::setFilter).setBordered(true));
		group.addWidget(fluidList);

		// 玩家背包(3 行)+ 快捷栏(1 行)
		net.minecraft.world.Container inventory = player.getInventory();
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 9; col++) {
				addPlayerSlot(group, inventory, row * 9 + col + 9, 7 + col * 18, 128 + row * 18);
			}
		}
		for (int col = 0; col < 9; col++) {
			addPlayerSlot(group, inventory, col, 7 + col * 18, 198);
		}

		return group;
	}

	private void addPlayerSlot(WidgetGroup group, net.minecraft.world.Container container, int slotIndex, int x, int y) {
		com.lowdragmc.lowdraglib.gui.widget.SlotWidget slot = new com.lowdragmc.lowdraglib.gui.widget.SlotWidget();
		slot.initTemplate();
		slot.setContainerSlot(container, slotIndex);
		slot.isPlayerContainer = true;
		slot.setSelfPosition(new com.lowdragmc.lowdraglib.utils.Position(x, y));
		slot.setBackground((com.lowdragmc.lowdraglib.gui.texture.ResourceTexture) null);
		group.addWidget(slot);
	}

	@Override
	public boolean isInvalid() {
		return this.isRemoved();
	}

	@Override
	public boolean isRemote() {
		return this.level != null && this.level.isClientSide;
	}

	@Override
	public void markAsDirty() {
		this.setChanged();
	}
}