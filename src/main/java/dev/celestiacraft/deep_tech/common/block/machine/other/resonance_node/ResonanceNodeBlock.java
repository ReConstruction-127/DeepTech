package dev.celestiacraft.deep_tech.common.block.machine.other.resonance_node;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.deep_tech.api.block.machine.MachineBlock;
import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import dev.celestiacraft.libs.api.register.block.BlockFacing;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import org.jetbrains.annotations.NotNull;

public class ResonanceNodeBlock extends MachineBlock<ResonanceNodeBlockEntity> {
	public ResonanceNodeBlock(Properties properties) {
		super(properties.sound(SoundType.AMETHYST)
				.noOcclusion());
		registerDefaultState(stateDefinition.any()
				.setValue(FACING, Direction.UP));
	}

	@Override
	protected BlockFacing useFacingType() {
		return BlockFacing.FACING;
	}

	@Override
	protected boolean useLitState() {
		return false;
	}

	private static boolean isFullBlock(BlockState state, BlockGetter level, BlockPos pos) {
		return state.isCollisionShapeFullBlock(level, pos);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction facing = context.getClickedFace();
		BlockPos attachedPos = context.getClickedPos().relative(facing.getOpposite());
		BlockState attachedState = context.getLevel().getBlockState(attachedPos);

		if (!isFullBlock(attachedState, context.getLevel(), attachedPos)) {
			return null;
		}

		return defaultBlockState().setValue(FACING, facing);
	}

	@Override
	public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter getter, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		final VoxelShape BASE_SHAPE = Shapes.or(
				Block.box(5, 0, 5, 11, 2, 11),
				Block.box(6, 2, 6, 10, 3, 10),
				Block.box(7, 3, 7, 9, 8, 9)
		);

		return switch (state.getValue(FACING)) {
			case UP -> BASE_SHAPE;
			case DOWN -> rotateShape(BASE_SHAPE, Direction.DOWN);
			case NORTH -> rotateShape(BASE_SHAPE, Direction.NORTH);
			case SOUTH -> rotateShape(BASE_SHAPE, Direction.SOUTH);
			case EAST -> rotateShape(BASE_SHAPE, Direction.EAST);
			case WEST -> rotateShape(BASE_SHAPE, Direction.WEST);
		};
	}

	@Override
	public BlockEntityType<ResonanceNodeBlockEntity> getBlockEntityType() {
		return DTBlockEntities.RESONANCE_NODE.get();
	}

	@Override
	public Class<ResonanceNodeBlockEntity> getBlockEntityClass() {
		return ResonanceNodeBlockEntity.class;
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> genBlockState() {
		return (context, provider) -> {
			BlockModelProvider models = provider.models();
			ModelFile model = models.getExistingFile(provider.modLoc("block/machine/resonance_node"));

			provider.getVariantBuilder(context.get())
					.forAllStates((state) -> {
						Direction facing = state.getValue(FACING);

						return ConfiguredModel.builder()
								.modelFile(model)
								.rotationX(getXRotForPole(facing))
								.rotationY(getYRotForPole(facing))
								.build();
					});
		};
	}

	private static int getXRotForPole(Direction facing) {
		return switch (facing) {
			case UP -> 0;
			case DOWN -> 180;
			case NORTH, SOUTH, WEST, EAST -> 90;
		};
	}

	private static int getYRotForPole(Direction facing) {
		return switch (facing) {
			case EAST -> 90;
			case SOUTH -> 180;
			case WEST -> 270;
			default -> 0;
		};
	}

	private static VoxelShape rotateShape(VoxelShape shape, Direction direction) {
		VoxelShape result = Shapes.empty();

		for (AABB box : shape.toAabbs()) {
			double minX = box.minX;
			double minY = box.minY;
			double minZ = box.minZ;
			double maxX = box.maxX;
			double maxY = box.maxY;
			double maxZ = box.maxZ;

			switch (direction) {
				case DOWN -> result = Shapes.or(result, Block.box(
						minX * 16,
						(16 - maxY * 16),
						minZ * 16,
						maxX * 16,
						(16 - minY * 16),
						maxZ * 16
				));

				case NORTH -> result = Shapes.or(result, Block.box(
						minX * 16,
						minZ * 16,
						(16 - maxY * 16),
						maxX * 16,
						maxZ * 16,
						(16 - minY * 16)
				));

				case SOUTH -> result = Shapes.or(result, Block.box(
						minX * 16,
						minZ * 16,
						minY * 16,
						maxX * 16,
						maxZ * 16,
						maxY * 16
				));

				case EAST -> result = Shapes.or(result, Block.box(
						minY * 16,
						minX * 16,
						minZ * 16,
						maxY * 16,
						maxX * 16,
						maxZ * 16
				));

				case WEST -> result = Shapes.or(result, Block.box(
						(16 - maxY * 16),
						minX * 16,
						minZ * 16,
						(16 - minY * 16),
						maxX * 16,
						maxZ * 16
				));
			}
		}

		return result;
	}
}