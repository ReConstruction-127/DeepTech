package dev.celestiacraft.deep_tech.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A small, single-use-at-a-time fluid container intended for precise machine transfers.
 */
public class TestTubeItem extends Item {
	public static final int CAPACITY = 8_000;

	public TestTubeItem(Properties properties) {
		super(properties.stacksTo(1));
	}

	@Override
	public @Nullable ICapabilityProvider initCapabilities(@NotNull ItemStack stack, @Nullable CompoundTag nbt) {
		return new FluidHandlerItemStack(stack, CAPACITY);
	}

	@Override
	public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
		Player player = context.getPlayer();
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		InteractionHand hand = context.getHand();

		if (player != null && FluidUtil.interactWithFluidHandler(player, hand, level, pos, context.getClickedFace())) {
			return InteractionResult.sidedSuccess(level.isClientSide());
		}

		return InteractionResult.PASS;
	}

	@Override
	public void appendHoverText(
			@NotNull ItemStack stack,
			@Nullable Level level,
			@NotNull List<Component> tooltip,
			@NotNull TooltipFlag flag
	) {
		stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(handler -> addFluidTooltip(handler, tooltip));
	}

	public static FluidStack getFluid(@NotNull ItemStack stack) {
		return stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM)
				.map(handler -> handler.getFluidInTank(0))
				.orElse(FluidStack.EMPTY);
	}

	private static void addFluidTooltip(IFluidHandlerItem handler, List<Component> tooltip) {
		FluidStack fluid = handler.getFluidInTank(0);
		if (fluid.isEmpty()) {
			tooltip.add(Component.translatable("tooltip.deep_tech.test_tube.empty", CAPACITY));
			return;
		}

		tooltip.add(Component.translatable(
				"tooltip.deep_tech.test_tube.contents",
				fluid.getDisplayName(),
				fluid.getAmount(),
				CAPACITY
		));
	}
}
