package dev.celestiacraft.deep_tech.common.block.machine.furnace;

import com.lowdragmc.lowdraglib.gui.factory.BlockEntityUIFactory;
import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import dev.celestiacraft.libs.api.register.block.BasicEntityBlock;
import dev.celestiacraft.libs.api.register.block.BlockFacing;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.NotNull;

public class SculkFurnaceBlock extends BasicEntityBlock {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty LIT = BlockStateProperties.LIT;

	public SculkFurnaceBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.defaultBlockState()
				.setValue(FACING, Direction.NORTH)
				.setValue(LIT, false));
	}

	// ✅ 删除错误的构造器和工厂方法（它们属于 SculkFurnaceBlockEntity）

	@Override
	protected boolean useLitState() {
		return true;
	}

	@Override
	protected BlockFacing useFacingType() {
		return BlockFacing.HORIZONTAL;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, LIT);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState()
				.setValue(FACING, context.getHorizontalDirection());
	}

	@Override
	public BlockEntityType<SculkFurnaceBlockEntity> getBlockEntityType() {
		return DTBlockEntities.SCULK_FURNACE.get();
	}

	@Override
	public Class<SculkFurnaceBlockEntity> getBlockEntityClass() {
		return SculkFurnaceBlockEntity.class;
	}

	@Override
	public void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			BlockEntity be = level.getBlockEntity(pos);
			if (be instanceof SculkFurnaceBlockEntity sculk_furnace) {
				sculk_furnace.getCapability(ForgeCapabilities.ITEM_HANDLER)
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
			if (blockEntity instanceof SculkFurnaceBlockEntity sculk_furnace && player instanceof ServerPlayer serverPlayer) {
				BlockEntityUIFactory.INSTANCE.openUI(sculk_furnace, serverPlayer);
			}
		}
		return InteractionResult.SUCCESS;
	}
}