package dev.celestiacraft.deep_tech.common.block.machine.resonance_node;

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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;

public class ResonanceNodeBlock extends MachineBlock<ResonanceNodeBlockEntity> {
	public ResonanceNodeBlock(Properties properties) {
		super(properties);
		// ✅ 设置默认朝向为 UP（紫水晶朝上，基座朝下）
		registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
	}

	@Override
	protected BlockFacing useFacingType() {
		return BlockFacing.FACING;
	}

	@Override
	protected boolean useLitState() {
		return false;
	}
	// ✅ 新增：放置时根据点击的面设置朝向（紫水晶指向点击的面）
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction clickedFace = context.getClickedFace();
		return defaultBlockState().setValue(FACING, clickedFace);
	}

	@Override
	public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
		return 0;
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
			BlockModelBuilder model = models.getBuilder(context.getName())
					.parent(models.getExistingFile(provider.modLoc("block/machine/resonance_node")));

			provider.getVariantBuilder(context.get())
					.forAllStates((state) -> {
						Direction facing = state.getValue(FACING);

						return ConfiguredModel.builder()
								.modelFile(model)
								// ✅️ 替换：使用自定义旋转方法，而非 BasicBlock 的默认方法
								.rotationX(getXRotForPole(facing))
								.rotationY(getYRotForPole(facing))
								.build();
					});
		};
	}
	// ✅️ 新增：接线柱风格模型的 X 轴旋转（默认模型朝上）
	private static int getXRotForPole(Direction facing) {
		return switch (facing) {
			case UP -> 0;       // 朝上 → 不旋转
			case DOWN -> 180;   // 朝下 → 翻转
			case NORTH, SOUTH, WEST, EAST -> 90; // 水平方向 → 向前倒
		};
	}

	// ✅️ 新增：接线柱风格模型的 Y 轴旋转（仅水平方向需要）
	private static int getYRotForPole(Direction facing) {
		return switch (facing) {
			case NORTH -> 0;
			case EAST -> 90;
			case SOUTH -> 180;
			case WEST -> 270;
			default -> 0;       // UP / DOWN 不需要 Y 旋转
		};
	}
}