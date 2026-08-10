package dev.celestiacraft.deep_tech.common.block.machine.sculk_network.port;

import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

// ✅ 正确：接受 Properties 参数并传递给父类
public class SNItemInputPortBlock extends SNPortBlock {
	public SNItemInputPortBlock(Properties properties) {
		super(properties);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SNItemInputPortBlockEntity(DTBlockEntities.SN_ITEM_INPUT_PORT.get(), pos, state);
	}
}
