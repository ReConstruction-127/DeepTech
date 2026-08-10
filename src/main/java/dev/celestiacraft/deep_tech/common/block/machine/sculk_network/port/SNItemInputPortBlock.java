package dev.celestiacraft.deep_tech.common.block.machine.sculk_network.port;

import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
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

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
			Level level, BlockState state, BlockEntityType<T> type) {
		if (level.isClientSide) return null;
		return (l, p, s, be) -> {
			if (be instanceof SNItemInputPortBlockEntity portBe) {
				SNItemInputPortBlockEntity.tick(l, p, s, portBe);
			}
		};
	}
}
