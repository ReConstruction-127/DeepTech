package dev.celestiacraft.deep_tech.api.block;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.libs.api.register.block.BasicEntityBlock;
import dev.celestiacraft.libs.api.register.block.BlockFacing;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import org.jetbrains.annotations.NotNull;

/**
 * 端口基类:继承 {@link BasicEntityBlock},BE 由 IEntityBlock 默认实现创建与 tick,
 * FACING 六面朝向属性由 {@link BasicEntityBlock#useFacingType()} 自动注册与管理.
 */
public abstract class SNPortBlock<T extends BlockEntity> extends BasicEntityBlock<T> {
	// 碰撞箱与地毯一致:16x16 底板上 1 格高
	private static final VoxelShape PORT_SHAPE = Shapes.box(0.0, 0.0, 0.0, 1.0, 1.0 / 16.0, 1.0);

	private static final String ROOT = "block/sculk_network/";

	public SNPortBlock(BlockBehaviour.Properties properties) {
		super(properties.sound(SoundType.SCULK_CATALYST)
				.strength(5.0F, 5.0F)
				.noOcclusion()
				.requiresCorrectToolForDrops());
	}

	@Override
	protected BlockFacing useFacingType() {
		return BlockFacing.FACING;
	}

	// 放置模式参考 ResonanceNode:朝向 = 点击面的反方向(接头指向被点击的方块)
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(BlockStateProperties.FACING, context.getClickedFace().getOpposite());
	}

	@Override
	public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return PORT_SHAPE;
	}

	@Override
	public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return PORT_SHAPE;
	}

	@Override
	public @NotNull VoxelShape getOcclusionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
		return Shapes.empty();
	}

	/**
	 * 提取手持物品所含的流体(用于设置端口过滤).
	 * <p>
	 * Forge 1.20.1 的 {@link FluidUtil#getFluidContained} 依赖物品注册
	 * {@code FLUID_HANDLER_ITEM} capability,而原版 {@link BucketItem} 并未注册,
	 * 对水桶/熔岩桶会漏识别(返回空). 因此先走 {@link BucketItem#getFluid()} 直取,
	 * 再兜底 capability 路线(模组容器, 瓶等).
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

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> simple(String model) {
		return (context, provider) -> {
			BlockModelProvider models = provider.models();
			ModelFile file = models.getExistingFile(provider.modLoc(model));
			provider.simpleBlock(context.get(), file);
		};
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> port(String io) {
		return (context, provider) -> {
			ConfiguredModel.Builder<?> builder = ConfiguredModel.builder();
			BlockModelProvider models = provider.models();

			ModelFile side = models.getExistingFile(provider.modLoc(ROOT + "port_" + io));
			ModelFile up = models.getExistingFile(provider.modLoc(ROOT + "port_up_" + io));
			ModelFile down = models.getExistingFile(provider.modLoc(ROOT + "port_down_" + io));

			provider.getVariantBuilder(context.get())
					.forAllStates((state) -> {
						Direction facing = state.getValue(BlockStateProperties.FACING);
						return switch (facing) {
							case UP -> builder.modelFile(up)
									.build();
							case DOWN -> builder.modelFile(down)
									.build();
							case EAST -> builder.modelFile(side)
									.rotationY(90)
									.build();
							case SOUTH -> builder.modelFile(side)
									.rotationY(180)
									.build();
							case WEST -> builder.modelFile(side)
									.rotationY(270)
									.build();
							default -> builder.modelFile(side)
									.build();
						};
					});
		};
	}
}