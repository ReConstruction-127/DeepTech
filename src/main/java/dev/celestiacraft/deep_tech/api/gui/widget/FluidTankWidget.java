package dev.celestiacraft.deep_tech.api.gui.widget;

import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.side.fluid.forge.FluidHelperImpl;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class FluidTankWidget extends Widget {
	private final Supplier<Integer> fluidGetter;
	private final @Nullable Supplier<FluidStack> fluidStackGetter;
	private final int maxFluid;
	private final ResourceTexture background;
	private final @Nullable ResourceTexture foreground;

	public FluidTankWidget(
			int x,
			int y,
			int width,
			int height,
			Supplier<Integer> fluidGetter,
			int maxFluid,
			ResourceTexture background,
			ResourceTexture foreground
	) {
		this(x, y, width, height, fluidGetter, maxFluid, null, background, foreground);
	}

	public FluidTankWidget(
			int x,
			int y,
			int width,
			int height,
			Supplier<Integer> fluidGetter,
			int maxFluid,
			Supplier<FluidStack> fluidStackGetter,
			ResourceTexture background
	) {
		this(x, y, width, height, fluidGetter, maxFluid, fluidStackGetter, background, null);
	}

	private FluidTankWidget(
			int x,
			int y,
			int width,
			int height,
			Supplier<Integer> fluidGetter,
			int maxFluid,
			@Nullable Supplier<FluidStack> fluidStackGetter,
			ResourceTexture background,
			@Nullable ResourceTexture foreground
	) {
		super(x, y, width, height);
		this.fluidGetter = fluidGetter;
		this.fluidStackGetter = fluidStackGetter;
		this.maxFluid = maxFluid;
		this.background = background;
		this.foreground = foreground;
	}

	@Override
	public void drawInBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		int fluid = fluidGetter.get();
		background.draw(
				graphics,
				mouseX,
				mouseY,
				getPosition().x,
				getPosition().y,
				getSize().width,
				getSize().height
		);

		if (fluid <= 0 || maxFluid <= 0) {
			return;
		}
		float ratio = (float) fluid / maxFluid;
		if (ratio > 1.0f) {
			ratio = 1.0f;
		}
		float fillHeight = getSize().height * ratio;
		float fillY = getPosition().y + getSize().height - fillHeight;

		FluidStack fluidStack = fluidStackGetter == null ? FluidStack.EMPTY : fluidStackGetter.get();
		if (!fluidStack.isEmpty()) {
			DrawerHelper.drawFluidForGui(
					graphics,
					FluidHelperImpl.toFluidStack(fluidStack),
					getPosition().x,
					fillY,
					getSize().width,
					fillHeight
			);
		} else if (foreground != null) {
			foreground.drawSubArea(
					graphics,
					getPosition().x,
					fillY,
					getSize().width,
					fillHeight,
					0.0F,
					1.0F - ratio,
					1.0F,
					ratio
			);
		}
	}

	@Override
	public void drawInForeground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		super.drawInForeground(graphics, mouseX, mouseY, partialTicks);

		if (isMouseOverElement(mouseX, mouseY)) {
			setHoverTooltips(Component.translatable("gui.deep_tech.fluid_stored", fluidGetter.get(), maxFluid));
		}
	}
}