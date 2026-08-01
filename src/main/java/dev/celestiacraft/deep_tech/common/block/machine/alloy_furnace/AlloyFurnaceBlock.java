package dev.celestiacraft.deep_tech.common.block.machine.alloy_furnace;

import dev.celestiacraft.deep_tech.api.block.machine.MachineBlock;
import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class AlloyFurnaceBlock extends MachineBlock<AlloyFurnaceBlockEntity> {
	public AlloyFurnaceBlock(Properties properties) {
		super(properties);
	}

	@Override
	public BlockEntityType<AlloyFurnaceBlockEntity> getBlockEntityType() {
		return DTBlockEntities.ALLOY_FURNACE.get();
	}

	@Override
	public Class<AlloyFurnaceBlockEntity> getBlockEntityClass() {
		return AlloyFurnaceBlockEntity.class;
	}
}