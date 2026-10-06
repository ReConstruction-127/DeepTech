package dev.celestiacraft.deep_tech.common.block.machine.reactor;

import dev.celestiacraft.deep_tech.api.block.machine.MachineBlock;
import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 反应堆控制器: 多方块的锚点方块(结构 pattern 里的 {@code 0}), 也是玩家查看/启动结构的地方.
 * <p>
 * 朝向用默认的水平四向(继承 {@link MachineBlock}), 结构与多方块可视化都跟着朝向旋转;
 * LIT 用来表示「结构成立且在生长」。
 */
public class ReactorControllerBlock extends MachineBlock<ReactorControllerBlockEntity> {
	public ReactorControllerBlock(Properties properties) {
		super(advancedProperties(properties));
		registerDefaultState(stateDefinition.any().setValue(HORIZONTAL_FACING, Direction.NORTH));
	}

	@Override
	public @Nullable BlockState getStateForPlacement(@NotNull net.minecraft.world.item.context.BlockPlaceContext context) {
		return defaultBlockState()
				.setValue(HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite())
				.setValue(LIT, false);
	}

	@Override
	public BlockEntityType<ReactorControllerBlockEntity> getBlockEntityType() {
		return DTBlockEntities.REACTOR_CONTROLLER.get();
	}

	@Override
	public Class<ReactorControllerBlockEntity> getBlockEntityClass() {
		return ReactorControllerBlockEntity.class;
	}

	/**
	 * 控制器被挖掉时先做引爆检查:
	 * 机器"热"(正在失控, 或正在运行且腔里还有自己长出来的幽匿块)的时候拆掉控制器会直接引爆,
	 * 而不是让失控现象就这么停掉.
	 */
	@Override
	public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock()) && !level.isClientSide()
				&& level.getBlockEntity(pos) instanceof ReactorControllerBlockEntity controller) {
			controller.detonateOnBreak();
		}

		super.onRemove(state, level, pos, newState, isMoving);
	}
}
