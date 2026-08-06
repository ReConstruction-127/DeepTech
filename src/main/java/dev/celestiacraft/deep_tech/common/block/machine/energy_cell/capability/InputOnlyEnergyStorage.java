package dev.celestiacraft.deep_tech.common.block.machine.energy_cell.capability;

import dev.celestiacraft.deep_tech.common.block.machine.energy_cell.EnergyCellBlockEntity;
import lombok.AllArgsConstructor;
import net.minecraftforge.energy.IEnergyStorage;

@AllArgsConstructor
public class InputOnlyEnergyStorage implements IEnergyStorage {
	private final EnergyCellBlockEntity entity;

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		int received = Math.min(
				maxReceive,
				entity.getMachineMaxEnergy() - entity.getEnergy()
		);

		if (!simulate && received > 0) {
			entity.setEnergy(entity.getEnergy() + received);
			entity.setChanged();
			entity.sync();
		}

		return received;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return 0;
	}

	@Override
	public int getEnergyStored() {
		return entity.getEnergy();
	}

	@Override
	public int getMaxEnergyStored() {
		return entity.getMachineMaxEnergy();
	}

	@Override
	public boolean canExtract() {
		return false;
	}

	@Override
	public boolean canReceive() {
		return true;
	}
}