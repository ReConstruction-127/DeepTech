package dev.celestiacraft.deep_tech.common.block.machine.reactor;

import dev.celestiacraft.deep_tech.api.block.machine.MachineBlock;
import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 反应堆能量接收器: 放在反应堆中心 5 格范围内, 破坏结构内的幽匿块换电, 每块 5000 FE.
 * <p>
 * 朝向用默认的水平四向, 只影响外观; LIT 表示「正在收割」。
 */
public class ReactorEnergyReceiverBlock extends MachineBlock<ReactorEnergyReceiverBlockEntity> {
	public ReactorEnergyReceiverBlock(Properties properties) {
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
	public BlockEntityType<ReactorEnergyReceiverBlockEntity> getBlockEntityType() {
		return DTBlockEntities.REACTOR_ENERGY_RECEIVER.get();
	}

	@Override
	public Class<ReactorEnergyReceiverBlockEntity> getBlockEntityClass() {
		return ReactorEnergyReceiverBlockEntity.class;
	}
}
