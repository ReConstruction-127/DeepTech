package dev.celestiacraft.deep_tech.common.block.machine.sculk_network.reservoir;

import com.lowdragmc.lowdraglib.gui.factory.BlockEntityUIFactory;
import dev.celestiacraft.deep_tech.common.inventory.SimpleMachineInventory;
import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class SNItemReservoirBlock extends Block {
	public SNItemReservoirBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Nullable
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SNItemReservoirBlockEntity(DTBlockEntities.SN_ITEM_RESERVOIR.get(), pos, state);
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			BlockEntity blockEntity = level.getBlockEntity(pos);
			if (blockEntity instanceof SNItemReservoirBlockEntity reservoirBe) {
				Containers.dropContents(level, pos, new SimpleMachineInventory(reservoirBe.getInventory()));
			}
			super.onRemove(state, level, pos, newState, isMoving);
		}
	}

	// 右键打开 GUI
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos,
	                             Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.isClientSide) return InteractionResult.SUCCESS;

		BlockEntity be = level.getBlockEntity(pos);
		if (be instanceof SNItemReservoirBlockEntity reservoirBe && player instanceof ServerPlayer serverPlayer) {
			BlockEntityUIFactory.INSTANCE.openUI(reservoirBe, serverPlayer);
			return InteractionResult.CONSUME;
		}
		return InteractionResult.PASS;
	}
}