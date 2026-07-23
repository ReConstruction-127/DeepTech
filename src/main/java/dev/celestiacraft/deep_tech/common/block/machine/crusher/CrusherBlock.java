package dev.celestiacraft.deep_tech.common.block.machine.crusher;

import com.lowdragmc.lowdraglib.gui.factory.BlockEntityUIFactory;
import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import dev.celestiacraft.libs.api.register.block.BasicEntityBlock;
import dev.celestiacraft.libs.api.register.block.BlockFacing;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.NotNull;

public class CrusherBlock extends BasicEntityBlock {
	public CrusherBlock(Properties properties) {
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

	@Override
	public BlockEntityType<CrusherBlockEntity> getBlockEntityType() {
		return DTBlockEntities.CRUSHER.get();
	}

	@Override
	public Class<CrusherBlockEntity> getBlockEntityClass() {
		return CrusherBlockEntity.class;
	}

	@Override
	public void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			BlockEntity be = level.getBlockEntity(pos);
			if (be instanceof CrusherBlockEntity crusher) {
				crusher.getCapability(ForgeCapabilities.ITEM_HANDLER)
						.ifPresent((handler) -> {
							for (int i = 0; i < handler.getSlots(); i++) {
								Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), handler.getStackInSlot(i));
							}
						});
			}
			super.onRemove(state, level, pos, newState, isMoving);
		}
	}

	@Override
	public @NotNull InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
		if (!level.isClientSide) {
			BlockEntity blockEntity = level.getBlockEntity(pos);
			if (blockEntity instanceof CrusherBlockEntity crusher && player instanceof ServerPlayer serverPlayer) {
				BlockEntityUIFactory.INSTANCE.openUI(crusher, serverPlayer);
			}
		}
		return InteractionResult.SUCCESS;
	}
}