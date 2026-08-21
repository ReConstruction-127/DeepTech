package dev.celestiacraft.deep_tech.common.block.machine.advanced.sculk_network.reservoir;

import dev.celestiacraft.deep_tech.api.block.machine.MachineBlock; // 确保是您自己的基类
import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class SNItemReservoirBlock extends MachineBlock<SNItemReservoirBlockEntity> {
	public SNItemReservoirBlock(Properties properties) {
		super(advancedProperties(properties));
	}

	@Override
	public BlockEntityType<SNItemReservoirBlockEntity> getBlockEntityType() {
		return DTBlockEntities.SN_ITEM_RESERVOIR.get();
	}

	@Override
	public Class<SNItemReservoirBlockEntity> getBlockEntityClass() {
		return SNItemReservoirBlockEntity.class;
	}
}