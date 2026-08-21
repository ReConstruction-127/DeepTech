package dev.celestiacraft.deep_tech.common.block.machine.advanced.sculk_network.port;

import dev.celestiacraft.deep_tech.api.block.SNPortBlock;
import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import dev.celestiacraft.libs.api.interaction.context.UseContext;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fluids.FluidStack;

public class SNFluidInputPortBlock extends SNPortBlock<SNFluidInputPortBlockEntity> {
	public SNFluidInputPortBlock(Properties properties) {
		super(properties);
	}

	@Override
	public BlockEntityType<SNFluidInputPortBlockEntity> getBlockEntityType() {
		return DTBlockEntities.SN_FLUID_INPUT_PORT.get();
	}

	@Override
	public Class<SNFluidInputPortBlockEntity> getBlockEntityClass() {
		return SNFluidInputPortBlockEntity.class;
	}

	@Override
	public InteractionResult useOn(UseContext context) {
		// 桶/流体交互优先(未启用时默认直接 PASS)
		InteractionResult fluid = tryFluidInteraction(context.getPlayer(), context.getHand(), context.getLevel(), context.getPos(), context.getResult());
		if (fluid.consumesAction()) {
			return fluid;
		}

		Level level = context.getLevel();
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		if (!(level.getBlockEntity(context.getPos()) instanceof SNFluidInputPortBlockEntity port)) {
			return InteractionResult.PASS;
		}

		Player player = context.getPlayer();

		if (player.isShiftKeyDown()) {
			return InteractionResult.SUCCESS;
		}

		ItemStack held = context.getStack();
		if (held.isEmpty()) {
			port.clearFilter();
			player.displayClientMessage(Component.translatable("message.deep_tech.fluid_filter_cleared"), true);
		} else {
			// 尝试从物品中提取流体(原版桶/模组容器)
			FluidStack fluidStack = getContainedFluid(held);
			if (fluidStack.isEmpty()) {
				player.displayClientMessage(Component.translatable("message.deep_tech.fluid_filter_empty"), true);
				return InteractionResult.PASS;
			} else {
				port.setFilter(fluidStack);
				player.displayClientMessage(
						Component.translatable("message.deep_tech.fluid_filter_set", fluidStack.getDisplayName()),
						true
				);
			}
		}
		return InteractionResult.SUCCESS;
	}
}