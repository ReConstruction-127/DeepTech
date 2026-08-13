package dev.celestiacraft.deep_tech.common.block.machine.sculk_network.port;

import dev.celestiacraft.libs.api.register.block.BasicEntityBlock;
import dev.celestiacraft.libs.api.register.block.BlockFacing;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

/**
 * 端口基类:继承 {@link BasicEntityBlock},BE 由 IEntityBlock 默认实现创建与 tick,
 * FACING 六面朝向属性由 {@link BasicEntityBlock#useFacingType()} 自动注册与管理。
 */
public abstract class SNPortBlock<T extends BlockEntity> extends BasicEntityBlock<T> {
	// 碰撞箱与地毯一致:16x16 底板上 1 格高
	private static final VoxelShape PORT_SHAPE = Shapes.box(0.0, 0.0, 0.0, 1.0, 1.0 / 16.0, 1.0);

	public SNPortBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	protected BlockFacing useFacingType() {
		return BlockFacing.FACING;
	}

	// 放置模式参考 ResonanceNode:朝向 = 点击面的反方向(接头指向被点击的方块)
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(BlockStateProperties.FACING, context.getClickedFace().getOpposite());
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return PORT_SHAPE;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return PORT_SHAPE;
	}

	@Override
	public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
		return Shapes.empty();
	}

	/**
	 * 提取手持物品所含的流体(用于设置端口过滤)。
	 * <p>
	 * Forge 1.20.1 的 {@link FluidUtil#getFluidContained} 依赖物品注册
	 * {@code FLUID_HANDLER_ITEM} capability,而原版 {@link BucketItem} 并未注册,
	 * 对水桶/熔岩桶会漏识别(返回空)。因此先走 {@link BucketItem#getFluid()} 直取,
	 * 再兜底 capability 路线(模组容器、瓶等)。
	 */
	protected static FluidStack getContainedFluid(ItemStack stack) {
		if (stack.getItem() instanceof BucketItem bucket) {
			Fluid fluid = bucket.getFluid();
			if (fluid != Fluids.EMPTY) {
				return new FluidStack(fluid, 1000);
			}
		}
		return FluidUtil.getFluidContained(stack).orElse(FluidStack.EMPTY);
	}
}