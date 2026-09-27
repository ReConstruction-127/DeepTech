package dev.celestiacraft.deep_tech.common.block.machine.bionic.docking_station;

import dev.celestiacraft.deep_tech.api.block.machine.MachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * 扩展坞: 把自身各个面上的邻居和正对着的方块接通, 相当于把它们"贴"到一起.
 * <p>
 * 只走 Forge 能力(capability), 两个方向都通:
 * <ul>
 *     <li>从其它面接进来的东西(管道/线缆/机器), 看到的是正对着的那台方块的能力
 *     —— 等于把那台机器的接口延伸了出来;</li>
 *     <li>正对着的方块从朝向面来查时, 看到的是扩展坞其余面上的邻居的能力
 *     —— 等于让那台方块以为那些邻居就贴在它边上.</li>
 * </ul>
 * 能量/物品/流体以及任何自定义能力走的都是这一份逻辑, 没有任何针对某个模组的代码.
 * <p>
 * 故意不实现原版 {@code Container}/{@code WorldlyContainer}: 原版漏斗在 Forge 下走的是
 * {@code VanillaInventoryCodeHooks}, 用的就是物品能力; 而 Container 的读写方法没有方向参数,
 * 只能固定桥接一个目标, 反而会让"拆方块时倒空容器"这类通用逻辑把邻居的东西倒出来.
 */
public class DockingStationBlockEntity extends MachineBlockEntity<DockingStationBlockEntity> {
	public DockingStationBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// ------------------------------------------------------------------
	// Forge 能力
	// ------------------------------------------------------------------

	/**
	 * 三种标准能力返回的是<b>带重入保护的转发代理</b>, 而不是邻居的原始 storage, 这一点很关键:
	 * <p>
	 * 像共振节点/纠缠方块这类"网络存储", 拿到 storage 之后会立刻回头问"我贴着的方块"要 storage.
	 * 如果把邻居的原始 storage 直接交出去, 这个"回头问"发生在 {@code getCapability} 返回<b>之后</b>,
	 * 重入保护早就释放了, 于是 N2 -> 扩展坞 -> N2 自己 -> receiveEnergy -> ... 无限递归(实测崩溃).
	 * 改成代理之后, 每次方法调用都会重新进一次保护, 环就断在调用期间。
	 * <p>
	 * 自定义能力没法做代理(拿不到接口类型), 只能直接转, 那种情况下只有 {@code getCapability} 这一层的保护。
	 */
	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
		boolean fromFacing = side == getFacing();

		if (capability == ForgeCapabilities.ENERGY) {
			return LazyOptional.of(() -> (T) new EnergyBridge(fromFacing));
		}
		if (capability == ForgeCapabilities.ITEM_HANDLER) {
			return LazyOptional.of(() -> (T) new ItemBridge(fromFacing));
		}
		if (capability == ForgeCapabilities.FLUID_HANDLER) {
			return LazyOptional.of(() -> (T) new FluidBridge(fromFacing));
		}

		if (!DockingStationBlock.enterBridge(worldPosition)) {
			return LazyOptional.empty();
		}

		try {
			LazyOptional<T> bridged = fromFacing
					? getNeighborCapability(capability)
					: getTargetCapability(capability);

			return bridged != null ? bridged : super.getCapability(capability, side);
		} finally {
			DockingStationBlock.exitBridge(worldPosition);
		}
	}

	/**
	 * 转发一次调用, 期间一直持有重入保护 —— 环就是断在这里。
	 */
	private <T, R> R bridge(@NotNull Capability<T> capability, boolean fromFacing, @NotNull Function<T, R> action, R fallback) {
		if (!DockingStationBlock.enterBridge(worldPosition)) {
			return fallback;
		}

		try {
			T provider = resolveProvider(capability, fromFacing);
			return provider == null ? fallback : action.apply(provider);
		} finally {
			DockingStationBlock.exitBridge(worldPosition);
		}
	}

	@Nullable
	private <T> T resolveProvider(@NotNull Capability<T> capability, boolean fromFacing) {
		LazyOptional<T> optional = fromFacing
				? getNeighborCapability(capability)
				: getTargetCapability(capability);

		return optional != null ? optional.orElse(null) : null;
	}

	/**
	 * 正对着的方块的能力; 按它朝向扩展坞的那一面去问, 这样它自己的分面规则照常生效.
	 */
	@Nullable
	private <T> LazyOptional<T> getTargetCapability(@NotNull Capability<T> capability) {
		BlockEntity target = getTarget(getFacing());
		if (target == null) {
			return null;
		}

		return target.getCapability(capability, getFacing().getOpposite());
	}

	/**
	 * 扩展坞其余各个面上的邻居里, 第一个拥有该能力的.
	 * <p>
	 * 一个扩展坞最多能接五个邻居, 但一个能力只能往外给一份, 所以按
	 * {@link Direction#values()} 的顺序取第一个有的(DOWN, UP, NORTH, SOUTH, WEST, EAST).
	 */
	@Nullable
	private <T> LazyOptional<T> getNeighborCapability(@NotNull Capability<T> capability) {
		if (getLevel() == null) {
			return null;
		}

		Direction facing = getFacing();

		for (Direction direction : Direction.values()) {
			if (direction == facing) {
				continue;
			}

			BlockEntity neighbor = getLevel().getBlockEntity(worldPosition.relative(direction));
			if (neighbor == null) {
				continue;
			}

			LazyOptional<T> optional = neighbor.getCapability(capability, direction.getOpposite());
			if (optional.isPresent()) {
				return optional;
			}
		}

		return null;
	}

	private final class EnergyBridge implements IEnergyStorage {
		private final boolean fromFacing;

		private EnergyBridge(boolean fromFacing) {
			this.fromFacing = fromFacing;
		}

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate) {
			return bridge(ForgeCapabilities.ENERGY, fromFacing, storage -> storage.receiveEnergy(maxReceive, simulate), 0);
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate) {
			return bridge(ForgeCapabilities.ENERGY, fromFacing, storage -> storage.extractEnergy(maxExtract, simulate), 0);
		}

		@Override
		public int getEnergyStored() {
			return bridge(ForgeCapabilities.ENERGY, fromFacing, IEnergyStorage::getEnergyStored, 0);
		}

		@Override
		public int getMaxEnergyStored() {
			return bridge(ForgeCapabilities.ENERGY, fromFacing, IEnergyStorage::getMaxEnergyStored, 0);
		}

		@Override
		public boolean canExtract() {
			return bridge(ForgeCapabilities.ENERGY, fromFacing, IEnergyStorage::canExtract, false);
		}

		@Override
		public boolean canReceive() {
			return bridge(ForgeCapabilities.ENERGY, fromFacing, IEnergyStorage::canReceive, false);
		}
	}

	private final class ItemBridge implements IItemHandler {
		private final boolean fromFacing;

		private ItemBridge(boolean fromFacing) {
			this.fromFacing = fromFacing;
		}

		@Override
		public int getSlots() {
			return bridge(ForgeCapabilities.ITEM_HANDLER, fromFacing, IItemHandler::getSlots, 0);
		}

		@Override
		public @NotNull ItemStack getStackInSlot(int slot) {
			return bridge(ForgeCapabilities.ITEM_HANDLER, fromFacing, handler -> handler.getStackInSlot(slot), ItemStack.EMPTY);
		}

		@Override
		public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
			return bridge(ForgeCapabilities.ITEM_HANDLER, fromFacing, handler -> handler.insertItem(slot, stack, simulate), stack);
		}

		@Override
		public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
			return bridge(ForgeCapabilities.ITEM_HANDLER, fromFacing, handler -> handler.extractItem(slot, amount, simulate), ItemStack.EMPTY);
		}

		@Override
		public int getSlotLimit(int slot) {
			return bridge(ForgeCapabilities.ITEM_HANDLER, fromFacing, handler -> handler.getSlotLimit(slot), 0);
		}

		@Override
		public boolean isItemValid(int slot, @NotNull ItemStack stack) {
			return bridge(ForgeCapabilities.ITEM_HANDLER, fromFacing, handler -> handler.isItemValid(slot, stack), false);
		}
	}

	private final class FluidBridge implements IFluidHandler {
		private final boolean fromFacing;

		private FluidBridge(boolean fromFacing) {
			this.fromFacing = fromFacing;
		}

		@Override
		public int getTanks() {
			return bridge(ForgeCapabilities.FLUID_HANDLER, fromFacing, IFluidHandler::getTanks, 0);
		}

		@Override
		public @NotNull FluidStack getFluidInTank(int tank) {
			return bridge(ForgeCapabilities.FLUID_HANDLER, fromFacing, handler -> handler.getFluidInTank(tank), FluidStack.EMPTY);
		}

		@Override
		public int getTankCapacity(int tank) {
			return bridge(ForgeCapabilities.FLUID_HANDLER, fromFacing, handler -> handler.getTankCapacity(tank), 0);
		}

		@Override
		public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
			return bridge(ForgeCapabilities.FLUID_HANDLER, fromFacing, handler -> handler.isFluidValid(tank, stack), false);
		}

		@Override
		public int fill(@NotNull FluidStack resource, @NotNull FluidAction action) {
			return bridge(ForgeCapabilities.FLUID_HANDLER, fromFacing, handler -> handler.fill(resource, action), 0);
		}

		@Override
		public @NotNull FluidStack drain(@NotNull FluidStack resource, @NotNull FluidAction action) {
			return bridge(ForgeCapabilities.FLUID_HANDLER, fromFacing, handler -> handler.drain(resource, action), FluidStack.EMPTY);
		}

		@Override
		public @NotNull FluidStack drain(int maxDrain, @NotNull FluidAction action) {
			return bridge(ForgeCapabilities.FLUID_HANDLER, fromFacing, handler -> handler.drain(maxDrain, action), FluidStack.EMPTY);
		}
	}

	@Nullable
	private BlockEntity getTarget(@NotNull Direction facing) {
		if (getLevel() == null) {
			return null;
		}

		return getLevel().getBlockEntity(worldPosition.relative(facing));
	}

	@NotNull
	private Direction getFacing() {
		return getBlockState().getValue(DockingStationBlock.FACING);
	}
}
