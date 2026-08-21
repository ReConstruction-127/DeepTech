package dev.celestiacraft.deep_tech.common.block.machine.advanced.sculk_network.center.capability;

import dev.celestiacraft.deep_tech.common.block.machine.advanced.sculk_network.center.SNCenterBlockEntity;
import net.minecraftforge.energy.IEnergyStorage;

/** 中枢能量存储:仅允许注入,不允许抽取(扫描消耗由 performScan 直接扣减) */
public class SNCenterEnergyStorage implements IEnergyStorage {
	private final SNCenterBlockEntity entity;

	public SNCenterEnergyStorage(SNCenterBlockEntity entity) {
		this.entity = entity;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		int received = Math.min(maxReceive, entity.getMaxEnergy() - entity.getEnergy());
		if (!simulate) {
			entity.setEnergy(entity.getEnergy() + received);
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
		return entity.getMaxEnergy();
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