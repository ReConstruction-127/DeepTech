package dev.celestiacraft.deep_tech.common.block.machine.advanced.collector.capability;

import dev.celestiacraft.deep_tech.common.block.machine.advanced.collector.SculkCollectorBlockEntity;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 幽匿采集器的能力(capability)统一入口, 单独类实现:
 * <ul>
 *   <li>能量: {@link ForgeCapabilities#ENERGY}, 存储上限 10000FE, 每 tick 挖矿耗 50FE</li>
 *   <li>物品: {@link ForgeCapabilities#ITEM_HANDLER}, 18 槽(9 输入回填储存 + 9 输出)</li>
 *   <li>过滤标记槽不在此列(只标记不存储, 仅玩家 GUI 可见)</li>
 * </ul>
 */
public class SculkCollectorCapability {
	private final SculkCollectorBlockEntity machine;
	private final LazyOptional<IEnergyStorage> energyCap;
	private final LazyOptional<IItemHandler> itemCap;

	public SculkCollectorCapability(SculkCollectorBlockEntity machine) {
		this.machine = machine;
		this.energyCap = LazyOptional.of(() -> new CollectorEnergyStorage(machine));
		this.itemCap = LazyOptional.of(machine::getItemHandler);
	}

	public <T> @NotNull LazyOptional<T> get(@NotNull Capability<T> capability, @Nullable Direction side) {
		if (capability == ForgeCapabilities.ENERGY) {
			return energyCap.cast();
		}
		if (capability == ForgeCapabilities.ITEM_HANDLER) {
			return itemCap.cast();
		}
		return LazyOptional.empty();
	}

	public void invalidate() {
		energyCap.invalidate();
		itemCap.invalidate();
	}

	/** 采集器的能量存储实现 */
	public static class CollectorEnergyStorage implements IEnergyStorage {
		private final SculkCollectorBlockEntity machine;

		public CollectorEnergyStorage(SculkCollectorBlockEntity machine) {
			this.machine = machine;
		}

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate) {
			if (!canReceive()) {
				return 0;
			}
			int received = Math.min(maxReceive, machine.getMaxReceive());
			received = Math.min(received, machine.getMachineMaxEnergy() - machine.getEnergy());
			received = Math.max(received, 0);
			if (!simulate && received > 0) {
				machine.setEnergy(machine.getEnergy() + received);
				machine.setChanged();
				machine.sync();
			}
			return received;
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate) {
			if (!canExtract()) {
				return 0;
			}
			int extracted = Math.min(maxExtract, machine.getMaxExtract());
			extracted = Math.min(extracted, machine.getEnergy());
			extracted = Math.max(extracted, 0);
			if (!simulate && extracted > 0) {
				machine.setEnergy(machine.getEnergy() - extracted);
				machine.setChanged();
				machine.sync();
			}
			return extracted;
		}

		@Override
		public int getEnergyStored() {
			return machine.getEnergy();
		}

		@Override
		public int getMaxEnergyStored() {
			return machine.getMachineMaxEnergy();
		}

		@Override
		public boolean canExtract() {
			return machine.getMaxExtract() > 0;
		}

		@Override
		public boolean canReceive() {
			return machine.getMaxReceive() > 0;
		}
	}
}