package dev.celestiacraft.deep_tech.common.block.machine.sculk_network.port;

import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

// 正确：接 Properties 参数并传递给父类
public class SNFluidOutputPortBlock extends SNPortBlock {
	public SNFluidOutputPortBlock(Properties properties) {
		super(properties.noOcclusion());
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SNFluidOutputPortBlockEntity(DTBlockEntities.SN_FLUID_OUTPUT_PORT.get(), pos, state);
	}
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos,
	                             Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.isClientSide) return InteractionResult.SUCCESS;

		BlockEntity be = level.getBlockEntity(pos);
		if (!(be instanceof SNFluidOutputPortBlockEntity port)) return InteractionResult.PASS;

		if (player.isShiftKeyDown()) return InteractionResult.SUCCESS;

		ItemStack held = player.getItemInHand(hand);
		if (held.isEmpty()) {
			port.clearFilter();
			player.displayClientMessage(Component.literal("Fluid filter cleared"), true);
		} else {
			// 尝试从物品中提取流体（桶、瓶等）
			FluidStack fluid = FluidUtil.getFluidContained(held).orElse(FluidStack.EMPTY);
			if (!fluid.isEmpty()) {
				port.setFilter(fluid);
				player.displayClientMessage(Component.literal("Filter set: " + fluid.getDisplayName()), true);
			} else {
				player.displayClientMessage(Component.literal("Item contains no fluid!"), true);
				return InteractionResult.PASS;
			}
		}
		return InteractionResult.SUCCESS;
	}
}
