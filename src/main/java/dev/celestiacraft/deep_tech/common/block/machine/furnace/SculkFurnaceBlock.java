package dev.celestiacraft.deep_tech.common.block.machine.furnace;

import dev.celestiacraft.deep_tech.api.block.MachineBlock;
import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class SculkFurnaceBlock extends MachineBlock<SculkFurnaceBlockEntity> {
	public SculkFurnaceBlock(Properties properties) {
		super(properties);
	}

	@Override
	public BlockEntityType<SculkFurnaceBlockEntity> getBlockEntityType() {
		return DTBlockEntities.SCULK_FURNACE.get();
	}

	@Override
	public Class<SculkFurnaceBlockEntity> getBlockEntityClass() {
		return SculkFurnaceBlockEntity.class;
	}
}