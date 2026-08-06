package dev.celestiacraft.deep_tech.common.block.machine.energy_cell;

import dev.celestiacraft.deep_tech.config.common.machine.EnergyCellConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class EnergyCellBlockEntity extends AbstractEnergyCellBlockEntity {
	public EnergyCellBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public int getMachineMaxEnergy() {
		return EnergyCellConfig.MAX_ENERGY.get();
	}

	@Override
	public int getMaxReceive() {
		return EnergyCellConfig.MAX_RECEIVE.get();
	}
}