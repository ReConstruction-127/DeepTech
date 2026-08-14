package dev.celestiacraft.deep_tech.common.block.machine.sculk_network.center;

import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import dev.celestiacraft.libs.api.register.block.BasicEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class SNCenterBlock extends BasicEntityBlock<SNCenterBlockEntity> {
	public SNCenterBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	public BlockEntityType<SNCenterBlockEntity> getBlockEntityType() {
		return DTBlockEntities.SN_CENTER.get();
	}

	@Override
	public Class<SNCenterBlockEntity> getBlockEntityClass() {
		return SNCenterBlockEntity.class;
	}
}