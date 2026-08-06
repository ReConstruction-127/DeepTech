package dev.celestiacraft.deep_tech.common.block.machine.energy_cell.capability;

import dev.celestiacraft.deep_tech.common.block.machine.energy_cell.AbstractEnergyCellBlockEntity;
import lombok.AllArgsConstructor;
import net.minecraftforge.energy.IEnergyStorage;

@AllArgsConstructor
public class OutputOnlyEnergyStorage implements IEnergyStorage {
	private final AbstractEnergyCellBlockEntity entity;

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return 0;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		int extracted = Math.min(maxExtract, entity.getEnergy());

		if (!simulate && extracted > 0) {
			entity.setEnergy(entity.getEnergy() - extracted);
			entity.setChanged();
			entity.sync();
		}

		return extracted;
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
		return true;
	}

	@Override
	public boolean canReceive() {
		return false;
	}
}