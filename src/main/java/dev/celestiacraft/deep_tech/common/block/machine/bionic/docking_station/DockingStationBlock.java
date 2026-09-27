package dev.celestiacraft.deep_tech.common.block.machine.bionic.docking_station;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.deep_tech.api.block.machine.MachineBlock;
import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import dev.celestiacraft.libs.api.register.block.BasicBlock;
import dev.celestiacraft.libs.api.register.block.BlockFacing;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelBuilder.FaceRotation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * 扩展坞: 六面朝向, 朝向的那一面就是它要"扩展"的方块(通常是机器).
 * <p>
 * 两个作用:
 * <ul>
 *     <li>红石: 从其余面收到的信号会转发给正对着的方块, 相当于给它接了一根红石线;</li>
 *     <li>能力: 正对着的方块的物品/流体/能量能力会暴露在扩展坞其余面上,
 *     见 {@link DockingStationBlockEntity#getCapability}.</li>
 * </ul>
 * 没有激活状态, 也没有 GUI. 参考实现: 共振节点。
 */
public class DockingStationBlock extends MachineBlock<DockingStationBlockEntity> {
	public DockingStationBlock(Properties properties) {
		super(advancedProperties(properties));
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	protected BlockFacing useFacingType() {
		return BlockFacing.FACING;
	}

	@Override
	protected boolean useLitState() {
		return false;
	}

	/**
	 * 放置时朝向点击的那一面: FACING 指向被点击的方块, 也就是要扩展的那台机器.
	 */
	@Override
	public @Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
		return defaultBlockState().setValue(FACING, context.getClickedFace().getOpposite());
	}

	// ------------------------------------------------------------------
	// 红石转发
	// ------------------------------------------------------------------

	@Override
	public boolean isSignalSource(@NotNull BlockState state) {
		return true;
	}

	/**
	 * {@code side} 是"从接收方指向本方块"的方向(和原版 {@code getBestNeighborSignal} 同一套约定),
	 * 所以正对着的机器来读时 side == FACING.getOpposite()。
	 */
	@Override
	public int getSignal(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull Direction side) {
		if (side != state.getValue(FACING).getOpposite()) {
			return 0;
		}

		return getInputSignal(level, pos, state.getValue(FACING));
	}

	/**
	 * 本方块除 FACING 之外各面收到的最高红石强度.
	 * <p>
	 * 方向约定和原版 {@code getBestNeighborSignal} 一致: 问邻居 {@code pos.relative(direction)}
	 * 朝 {@code direction} 方向发出的强度.
	 * <p>
	 * 不读 FACING 那一面, 免得和正对着的机器互相充能。
	 */
	private static int getInputSignal(@NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull Direction facing) {
		int signal = 0;

		for (Direction direction : Direction.values()) {
			if (direction == facing) {
				continue;
			}

			BlockPos neighbor = pos.relative(direction);
			signal = Math.max(signal, level.getBlockState(neighbor).getSignal(level, neighbor, direction));
		}

		return signal;
	}

	@Override
	public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block block, @NotNull BlockPos fromPos, boolean isMoving) {
		super.neighborChanged(state, level, pos, block, fromPos, isMoving);

		if (level.isClientSide()) {
			return;
		}

		/*
		 * 自己的输入变了, 让正对着的机器重新读一次红石强度.
		 * 这里只通知那一个方块, 不用 updateNeighborsAt: 否则两个相邻的扩展坞会互相刷个没完.
		 */
		level.neighborChanged(pos.relative(state.getValue(FACING)), this, pos);
	}

	@Override
	public BlockEntityType<DockingStationBlockEntity> getBlockEntityType() {
		return DTBlockEntities.DOCKING_STATION.get();
	}

	@Override
	public Class<DockingStationBlockEntity> getBlockEntityClass() {
		return DockingStationBlockEntity.class;
	}

	// ------------------------------------------------------------------
	// 桥接的重入保护
	// ------------------------------------------------------------------

	/**
	 * 本次调用链里已经访问过的扩展坞位置.
	 * <p>
	 * 有些设备(共振节点就是)拿到能力/效果后会立刻回头问"我贴着的方块",
	 * 而扩展坞又会把问题转回去; 两个扩展坞挨着放时也会互相问.
	 * 同一个位置在一次调用链里只允许进一次, 就能把所有这类环断掉,
	 * 代价只是环里那一层的桥接暂时返回空.
	 */
	private static final ThreadLocal<Set<BlockPos>> VISITING = ThreadLocal.withInitial(HashSet::new);

	static boolean enterBridge(@NotNull BlockPos pos) {
		return VISITING.get().add(pos.immutable());
	}

	static void exitBridge(@NotNull BlockPos pos) {
		VISITING.get().remove(pos.immutable());
	}

	/**
	 * 方块的正面(north)是 docking_station/face, 与之相对的背面(south)是 bottom, 其余四面都是 side.
	 * <p>
	 * 这里手写立方体而不用 {@code block/cube}, 是为了给每个面单独转贴图:
	 * {@code side} 上的箭头在贴图里是朝上的, 而原版立方体各面的默认朝向是
	 * east/west 的"上"为世界 +Y、down 的"上"为南, 都会让箭头指错方向.
	 * 按下面这些角度转过之后, 四个侧面的箭头都指向 north(也就是 face 那一侧),
	 * 方块状态再整体把 north 转到 FACING.
	 * <p>
	 * 方块状态的方向用 {@link BasicBlock#getXRotFromFacing}/{@link BasicBlock#getYRotFromFacing},
	 * 也就是原版 observer 那套"模型 north 指向 FACING"的映射; 注意上/下朝向会得到负角度,
	 * 需要归一化到 0~360, 否则 ConfiguredModel 会当成非法旋转直接报错。
	 */
	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> genBlockState() {
		return (context, provider) -> {
			BlockModelBuilder model = provider.models()
					.withExistingParent("block/machine/docking_station", "minecraft:block/block")
					.texture("particle", provider.modLoc("block/machine/docking_station/face"))
					.texture("face", provider.modLoc("block/machine/docking_station/face"))
					.texture("bottom", provider.modLoc("block/machine/docking_station/bottom"))
					.texture("side", provider.modLoc("block/machine/docking_station/side"));

			model.element()
					.from(0, 0, 0)
					.to(16, 16, 16)
					.face(Direction.NORTH).uvs(0, 0, 16, 16).texture("#face").cullface(Direction.NORTH).end()
					.face(Direction.SOUTH).uvs(0, 0, 16, 16).texture("#bottom").cullface(Direction.SOUTH).end()
					.face(Direction.UP).uvs(0, 0, 16, 16).texture("#side").cullface(Direction.UP).end()
					.face(Direction.DOWN).uvs(0, 0, 16, 16).texture("#side").rotation(FaceRotation.UPSIDE_DOWN).cullface(Direction.DOWN).end()
					.face(Direction.EAST).uvs(0, 0, 16, 16).texture("#side").rotation(FaceRotation.CLOCKWISE_90).cullface(Direction.EAST).end()
					.face(Direction.WEST).uvs(0, 0, 16, 16).texture("#side").rotation(FaceRotation.COUNTERCLOCKWISE_90).cullface(Direction.WEST).end()
					.end();

			provider.getVariantBuilder(context.get())
					.forAllStates((state) -> {
						Direction facing = state.getValue(FACING);

						return ConfiguredModel.builder()
								.modelFile(model)
								.rotationX(Math.floorMod(BasicBlock.getXRotFromFacing(facing), 360))
								.rotationY(Math.floorMod(BasicBlock.getYRotFromFacing(facing), 360))
								.build();
					});
		};
	}
}
