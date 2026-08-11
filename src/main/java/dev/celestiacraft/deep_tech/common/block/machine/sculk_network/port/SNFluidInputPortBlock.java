package dev.celestiacraft.deep_tech.common.block.machine.sculk_network.port;

import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

// 正确：接 Properties 参数并传递给父类
public class SNFluidInputPortBlock extends SNPortBlock {
	public SNFluidInputPortBlock(Properties properties) {
		super(properties.noOcclusion());
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SNFluidInputPortBlockEntity(DTBlockEntities.SN_FLUID_INPUT_PORT.get(), pos, state);
	}
}
