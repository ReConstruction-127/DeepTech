package dev.celestiacraft.deep_tech.common.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

import java.util.function.Supplier;

/**
 * 培养液液块:独立的方块 tick 通道, 确保源块与流动液体都持续触发感染。
 * 流体自身的 tick 只在流动时被调度, 静止的源块只 tick 一次, 因此必须由方块 tick 驱动。
 */
public class SculkCultureLiquidBlock extends LiquidBlock {

	public SculkCultureLiquidBlock(Supplier<? extends FlowingFluid> fluid, Properties properties) {
		super(fluid, properties);
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
		super.onPlace(state, level, pos, oldState, isMoving);
		level.scheduleTick(pos, this, 5);
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (!state.getFluidState().isEmpty()) {
			SculkCultureFluid.tickFluid(level, pos);
			level.scheduleTick(pos, this, 5);
		}
	}
}