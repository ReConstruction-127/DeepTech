package dev.celestiacraft.deep_tech.api.block;

import com.lowdragmc.lowdraglib.gui.factory.BlockEntityUIFactory;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.libs.api.register.block.BasicEntityBlock;
import dev.celestiacraft.libs.api.register.block.BasicBlock;
import dev.celestiacraft.libs.api.register.block.BlockFacing;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
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

	protected static <B extends BasicBlock> NonNullBiConsumer<DataGenContext<Block, B>, RegistrateBlockstateProvider> genBlockState(String textureFolder) {
		return (context, provider) -> {
			BlockModelBuilder modelOff = machineModel(provider, context.getName() + "_off", textureFolder, false);
			BlockModelBuilder modelOn = machineModel(provider, context.getName() + "_on", textureFolder, true);

			provider.getVariantBuilder(context.get())
					.forAllStates((state) -> {
						return ConfiguredModel.builder()
								.modelFile(state.getValue(BasicBlock.LIT) ? modelOn : modelOff)
								.rotationY(BasicBlock.getYRotFromFacing(state.getValue(BasicBlock.FACING)))
								.build();
					});
		};
	}

	protected static BlockModelBuilder machineModel(RegistrateBlockstateProvider provider, String name, String textureFolder, boolean lit) {
		String suffix = lit ? "on" : "off";
		String textureRoot = "block/machine/" + textureFolder + "/";

		BlockModelProvider models = provider.models();

		return models.orientableWithBottom(
				name,
				provider.modLoc(textureRoot + "side_" + suffix),
				provider.modLoc(textureRoot + "face_" + suffix),
				provider.modLoc(textureRoot + "bottom"),
				provider.modLoc(textureRoot + "top_" + suffix)
		);
	}

	protected static <T extends Block> void horizontalLitBlock(RegistrateBlockstateProvider provider, T block, BlockModelBuilder modelOff, BlockModelBuilder modelOn) {
		provider.getVariantBuilder(block)
				.forAllStates((state) -> {
					Direction direction = state.getValue(BasicBlock.HORIZONTAL_FACING);

					return ConfiguredModel.builder()
							.modelFile(state.getValue(BasicBlock.LIT) ? modelOn : modelOff)
							.rotationY(BasicBlock.getYRotFromFacing(direction))
							.build();
				});
	}
}