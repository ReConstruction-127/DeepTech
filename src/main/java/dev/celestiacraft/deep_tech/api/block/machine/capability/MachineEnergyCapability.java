package dev.celestiacraft.deep_tech.api.block.machine.capability;

import dev.celestiacraft.deep_tech.api.block.machine.MachineBlockEntity;
import net.minecraftforge.energy.IEnergyStorage;

public class MachineEnergyCapability implements IEnergyStorage {
	private final MachineBlockEntity<?> machine;

	public MachineEnergyCapability(MachineBlockEntity<?> machine) {
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