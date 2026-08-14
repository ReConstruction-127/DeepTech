package dev.celestiacraft.deep_tech.common.block.machine.sculk_network.accessor;

import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.TextFieldWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Position;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.common.block.machine.sculk_network.port.SNHelper;
import dev.celestiacraft.deep_tech.common.block.machine.sculk_network.reservoir.SNFluidReservoirBlockEntity;
import dev.celestiacraft.deep_tech.common.block.machine.sculk_network.reservoir.SNItemReservoirBlockEntity;
import dev.celestiacraft.deep_tech.common.register.block.MachineBlocks;
import dev.celestiacraft.libs.api.register.block.BasicBlockEntity;
import dev.celestiacraft.libs.api.register.block.ITickableBlockEntity;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

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
 * 供 UI 端的列表控件读取并同步给客户端展示.
 * <p>
 * 网络发现方式与中枢一致:BFS 沿网络组件扩展(半径 16 格),
 * 不过度依赖中枢的扫描结果,访问器自身即可定位储存器.
 */
public class SNAccessorBlockEntity extends BasicBlockEntity implements IUIHolder.BlockEntityUI, ITickableBlockEntity<SNAccessorBlockEntity> {
	public record ItemEntry(ItemStack stack, long count) {
	}

	public record FluidEntry(FluidStack stack, long amount) {
	}

	/** 汇总刷新间隔(tick) */
	private static final int REFRESH_INTERVAL = 20;
	@Getter
	private final List<ItemEntry> itemEntries = new ArrayList<>();
	@Getter
	private final List<FluidEntry> fluidEntries = new ArrayList<>();
	private final List<SNItemReservoirBlockEntity> itemReservoirs = new ArrayList<>();
	private final List<SNFluidReservoirBlockEntity> fluidReservoirs = new ArrayList<>();
	private long nextRefreshTime = Long.MIN_VALUE;

	public SNAccessorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void serverTick(Level level, BlockPos pos, BlockState state, SNAccessorBlockEntity entity) {
		refreshIfNeeded();
	}

	/**
	 * 间隔刷新网络汇总数据. UI 列表控件也会主动调用,保证打开界面时数据新鲜.
	 */
	public void refreshIfNeeded() {
		if (level == null || level.isClientSide()) {
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
		itemReservoirs.clear();
		fluidReservoirs.clear();

		// BFS 收集网络中的储存器
		Queue<BlockPos> queue = new ArrayDeque<>();
		Set<BlockPos> visited = new HashSet<>();
		queue.add(worldPosition);
		visited.add(worldPosition);

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

			if (pos.distSqr(worldPosition) >= 16 << 4) {
				continue;
			}
			for (Direction direction : Direction.values()) {
				BlockPos neighbor = pos.relative(direction);
				if (!visited.contains(neighbor) && SNHelper.isNetworkComponent(level, neighbor)) {
					visited.add(neighbor);
					queue.add(neighbor);
				}
			}
		}

		// 聚合物品:按物品 ID 合并数量,图标取首个遇到的堆
		Map<Item, long[]> itemCounts = new HashMap<>();
		Map<Item, ItemStack> itemIcons = new HashMap<>();
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
				itemCounts.computeIfAbsent(stack.getItem(), k -> new long[] {0})[0] += stack.getCount();
				itemIcons.putIfAbsent(stack.getItem(), stack.copy());
			}
		}
		for (Map.Entry<Item, long[]> entry : itemCounts.entrySet()) {
			ItemStack icon = itemIcons.get(entry.getKey());
			if (icon == null) {
				continue;
			}
			itemEntries.add(new ItemEntry(icon, entry.getValue()[0]));
		}

		// 聚合流体:按流体 ID 合并数量
		Map<Fluid, long[]> fluidCounts = new HashMap<>();
		Map<Fluid, FluidStack> fluidIcons = new HashMap<>();

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
				fluidCounts.computeIfAbsent(stack.getFluid(), (fluid) -> {
					return new long[] {0};
				})[0] += stack.getAmount();
				fluidIcons.putIfAbsent(stack.getFluid(), stack.copy());
			}
		}
		for (Map.Entry<Fluid, long[]> entry : fluidCounts.entrySet()) {
			FluidStack icon = fluidIcons.get(entry.getKey());
			if (icon == null) {
				continue;
			}
			fluidEntries.add(new FluidEntry(icon, entry.getValue()[0]));
		}
	}

	/** 让汇总缓存下次 tick 立即刷新(存取操作后调用) */
	private void forceRefreshSoon() {
		if (level == null || level.isClientSide) {
			return;
		}
		nextRefreshTime = level.getGameTime();
		setChanged();
	}

	/**
	 * 把物品存入网络(依次塞入各物品储库). 返回实际存入数量.
	 */
	public int insertItem(ItemStack stack, boolean simulate) {
		if (stack.isEmpty() || level == null || level.isClientSide) {
			return 0;
		}
		refreshIfNeeded();
		int inserted = 0;
		ItemStack remaining = stack.copy();
		for (SNItemReservoirBlockEntity reservoir : itemReservoirs) {
			if (remaining.isEmpty()) {
				break;
			}
			IItemHandler handler = reservoir.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
			if (handler == null) {
				continue;
			}
			int before = remaining.getCount();
			remaining = ItemHandlerHelper.insertItem(handler, remaining, simulate);
			inserted += before - remaining.getCount();
		}
		if (inserted > 0 && !simulate) {
			forceRefreshSoon();
		}
		return inserted;
	}

	/**
	 * 从网络取出指定物品(按物品 ID+NBT 匹配,遍历各储库). 返回实际抽出的堆.
	 */
	public ItemStack extractItem(ItemStack filter, int amount, boolean simulate) {
		if (filter.isEmpty() || amount <= 0 || level == null || level.isClientSide) {
			return ItemStack.EMPTY;
		}
		refreshIfNeeded();
		int remaining = amount;
		ItemStack result = ItemStack.EMPTY;
		for (SNItemReservoirBlockEntity reservoir : itemReservoirs) {
			if (remaining <= 0) {
				break;
			}
			IItemHandler handler = reservoir.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
			if (handler == null) {
				continue;
			}
			for (int slot = 0; slot < handler.getSlots(); slot++) {
				ItemStack stack = handler.getStackInSlot(slot);
				if (stack.isEmpty() || !ItemStack.isSameItemSameTags(stack, filter)) {
					continue;
				}
				ItemStack extracted = handler.extractItem(slot, Math.min(remaining, stack.getCount()), simulate);
				if (extracted.isEmpty()) {
					continue;
				}
				remaining -= extracted.getCount();
				if (result.isEmpty()) {
					result = extracted.copy();
				} else {
					result.grow(extracted.getCount());
				}
				if (remaining <= 0) {
					break;
				}
			}
		}
		if (!result.isEmpty() && !simulate) {
			forceRefreshSoon();
		}
		return result;
	}

	/**
	 * 把流体存入网络(依次填入各流体储库). 返回实际存入量(mB).
	 */
	public int fill(FluidStack stack, IFluidHandler.FluidAction action) {
		if (stack.isEmpty() || level == null || level.isClientSide) {
			return 0;
		}
		refreshIfNeeded();
		int filled = 0;
		FluidStack remaining = stack.copy();
		for (SNFluidReservoirBlockEntity reservoir : fluidReservoirs) {
			if (remaining.isEmpty()) {
				break;
			}
			IFluidHandler handler = reservoir.getTank();
			int amount = handler.fill(remaining, action);
			filled += amount;
			if (amount > 0) {
				remaining.shrink(amount);
			}
		}
		if (filled > 0 && !action.simulate()) {
			forceRefreshSoon();
		}
		return filled;
	}

	/**
	 * 从网络抽取流体(按流体类型匹配). 返回实际抽出的流体堆.
	 */
	public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
		if (resource.isEmpty() || level == null || level.isClientSide) {
			return FluidStack.EMPTY;
		}
		refreshIfNeeded();
		int drained = 0;
		FluidStack result = FluidStack.EMPTY;
		for (SNFluidReservoirBlockEntity reservoir : fluidReservoirs) {
			if (drained >= resource.getAmount()) {
				break;
			}
			IFluidHandler handler = reservoir.getTank();
			// 必须用带类型过滤的重载,否则会从"最后一个罐"抽出无关流体(指哪取哪)
			FluidStack request = resource.copy();
			request.setAmount(resource.getAmount() - drained);
			FluidStack amount = handler.drain(request, action);
			if (amount.isEmpty()) {
				continue;
			}
			drained += amount.getAmount();
			if (result.isEmpty()) {
				result = amount.copy();
			} else {
				result.grow(amount.getAmount());
			}
		}
		if (!result.isEmpty() && !action.simulate()) {
			forceRefreshSoon();
		}
		return result;
	}

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

		// 物品列表(9×3,可滚动 + 搜索过滤)
		SNAccessorListWidget itemList = new SNAccessorListWidget(this, SNAccessorListWidget.Kind.ITEMS, 9, 7, 34, 162, 54);
		group.addWidget(new TextFieldWidget(7, 20, 162, 12, itemList::getFilter, itemList::setFilter).setBordered(true).setClientSideWidget());
		group.addWidget(itemList);

		// 流体列表(9×2,紧邻物品区下方,无搜索栏)
		SNAccessorListWidget fluidList = new SNAccessorListWidget(this, SNAccessorListWidget.Kind.FLUIDS, 9, 7, 92, 162, 36);
		group.addWidget(fluidList);

		// 玩家背包(3 行)+ 快捷栏(1 行)
		Container inventory = player.getInventory();
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 9; col++) {
				addPlayerSlot(group, inventory, row * 9 + col + 9, 7 + col * 18, 136 + row * 18);
			}
		}
		for (int col = 0; col < 9; col++) {
			addPlayerSlot(group, inventory, col, 7 + col * 18, 196);
		}

		return group;
	}

	private void addPlayerSlot(WidgetGroup group, Container container, int slotIndex, int x, int y) {
		SNPlayerSlot slot = new SNPlayerSlot(this, slotIndex);
		slot.initTemplate();
		slot.setContainerSlot(container, slotIndex);
		slot.isPlayerContainer = true;
		slot.setSelfPosition(new Position(x, y));
		slot.setBackground((ResourceTexture) null);
		group.addWidget(slot);
	}

	@Override
	public boolean isInvalid() {
		return isRemoved();
	}

	@Override
	public boolean isRemote() {
		return level != null && level.isClientSide;
	}

	@Override
	public void markAsDirty() {
		setChanged();
	}
}