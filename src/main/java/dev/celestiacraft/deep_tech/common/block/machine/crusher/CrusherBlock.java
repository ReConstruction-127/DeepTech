package dev.celestiacraft.deep_tech.common.block.machine.crusher;

import dev.celestiacraft.deep_tech.api.block.MachineBlock;
import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class CrusherBlock extends MachineBlock<CrusherBlockEntity> {
	public CrusherBlock(Properties properties) {
		super(properties);
	}

	@Override
	public BlockEntityType<CrusherBlockEntity> getBlockEntityType() {
		return DTBlockEntities.CRUSHER.get();
	}

	@Override
	public Class<CrusherBlockEntity> getBlockEntityClass() {
		return CrusherBlockEntity.class;
	}
}