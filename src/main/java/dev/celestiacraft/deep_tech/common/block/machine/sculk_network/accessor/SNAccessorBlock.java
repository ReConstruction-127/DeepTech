package dev.celestiacraft.deep_tech.common.block.machine.sculk_network.accessor;

import dev.celestiacraft.deep_tech.common.block.machine.sculk_network.port.SNPortBlock;
import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SNAccessorBlock extends SNPortBlock {  // 或者 extends HorizontalDirectionalBlock
	public SNAccessorBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SNAccessorBlockEntity(DTBlockEntities.SN_ACCESSOR.get(), pos, state);
	}
}