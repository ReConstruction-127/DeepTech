package dev.celestiacraft.deep_tech.common.block.machine.sculk_network.reservoir;

import dev.celestiacraft.deep_tech.api.block.machine.MachineBlock;
import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class SNFluidReservoirBlock extends MachineBlock<SNFluidReservoirBlockEntity> {
	public SNFluidReservoirBlock(Properties properties) {
		super(properties.noOcclusion());
	}

	@Override
	public BlockEntityType<SNFluidReservoirBlockEntity> getBlockEntityType() {
		return DTBlockEntities.SN_FLUID_RESERVOIR.get();
	}

	@Override
	public Class<SNFluidReservoirBlockEntity> getBlockEntityClass() {
		return SNFluidReservoirBlockEntity.class;
	}
}