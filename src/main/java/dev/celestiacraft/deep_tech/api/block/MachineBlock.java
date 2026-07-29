package dev.celestiacraft.deep_tech.api.block;

import com.lowdragmc.lowdraglib.gui.factory.BlockEntityUIFactory;
import dev.celestiacraft.libs.api.register.block.BasicEntityBlock;
import dev.celestiacraft.libs.api.register.block.BlockFacing;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.NotNull;

public abstract class MachineBlock<T extends BlockEntity> extends BasicEntityBlock<T> {
	public MachineBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected boolean useLitState() {
		return true;
	}

	@Override
	protected BlockFacing useFacingType() {
		return BlockFacing.HORIZONTAL;
	}

	protected boolean getMachineUI(T entity, ServerPlayer player) {
		return BlockEntityUIFactory.INSTANCE.openUI(entity, player);
	}

	@Override
	public void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			BlockEntity blockEntity = level.getBlockEntity(pos);
			if (getBlockEntityClass().isInstance(blockEntity)) {
				dropInventory(getBlockEntityClass().cast(blockEntity), level, pos);
			}
			super.onRemove(state, level, pos, newState, isMoving);
		}
	}

	@Override
	public @NotNull InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (blockEntity != null && getBlockEntityClass().isInstance(blockEntity) && player instanceof ServerPlayer serverPlayer) {
			return getMachineUI(getBlockEntityClass().cast(blockEntity), serverPlayer)
					? InteractionResult.CONSUME
					: InteractionResult.PASS;
		}
		return InteractionResult.PASS;
	}

	protected void dropInventory(T entity, Level level, BlockPos pos) {
		entity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent((handler) -> {
			for (int i = 0; i < handler.getSlots(); i++) {
				ItemStack stack = handler.getStackInSlot(i);
				if (!stack.isEmpty()) {
					Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack.copy());
				}
			}
		});
	}
}