package dev.celestiacraft.deep_tech.common.block.machine.sculk_network.port;

import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import dev.celestiacraft.libs.api.interaction.context.UseContext;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

// 正确：接 Properties 参数并传递给父类
public class SNFluidInputPortBlock extends SNPortBlock<SNFluidInputPortBlockEntity> {
	public SNFluidInputPortBlock(Properties properties) {
		super(properties.noOcclusion());
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
		if (fluid.consumesAction()) return fluid;

		Level level = context.getLevel();
		if (level.isClientSide) return InteractionResult.SUCCESS;

		if (!(level.getBlockEntity(context.getPos()) instanceof SNFluidInputPortBlockEntity port)) return InteractionResult.PASS;

		Player player = context.getPlayer();

		if (player.isShiftKeyDown()) return InteractionResult.SUCCESS;

		ItemStack held = context.getStack();
		if (held.isEmpty()) {
			port.clearFilter();
			player.displayClientMessage(Component.literal("Fluid filter cleared"), true);
		} else {
			// 尝试从物品中提取流体（桶、瓶等）
			FluidStack fluidStack = FluidUtil.getFluidContained(held).orElse(FluidStack.EMPTY);
			if (!fluidStack.isEmpty()) {
				port.setFilter(fluidStack);
				player.displayClientMessage(Component.literal("Filter set: " + fluidStack.getDisplayName()), true);
			} else {
				player.displayClientMessage(Component.literal("Item contains no fluid!"), true);
				return InteractionResult.PASS;
			}
		}
		return InteractionResult.SUCCESS;
	}
}