package dev.celestiacraft.deep_tech.common.block.machine.sculk_network.center;

import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.Nullable;

public class SNCenterBlock extends BaseEntityBlock {

	public SNCenterBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SNCenterBlockEntity(DTBlockEntities.SN_CENTER.get(), pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
			Level level, BlockState state, BlockEntityType<T> type) {
		if (level.isClientSide) return null;
		return (l, p, s, be) -> {
			if (be instanceof SNCenterBlockEntity snBe) {
				SNCenterBlockEntity.tick(l, p, s, snBe);
			}
		};
	}
}