package dev.celestiacraft.deep_tech.common.block.machine.sculk_network.accessor;

import dev.celestiacraft.deep_tech.common.block.machine.sculk_network.port.SNPortBlock;
import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SNAccessorBlock extends SNPortBlock {  // 或者 extends HorizontalDirectionalBlock
	public SNAccessorBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SNAccessorBlockEntity(DTBlockEntities.SN_ACCESSOR.get(), pos, state);
	}

	// accessor 保持完整方块碰撞箱(不继承端口的"地毯"形状)
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return Shapes.block();
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return Shapes.block();
	}

	@Override
	public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
		return Shapes.block();
	}
}