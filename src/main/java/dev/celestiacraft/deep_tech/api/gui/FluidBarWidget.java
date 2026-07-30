package dev.celestiacraft.deep_tech.api.gui;

import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class FluidBarWidget extends Widget {
	private final Supplier<Integer> fluidGetter;
	private final int maxFluid;
	private final ResourceTexture background;
	private final ResourceTexture foreground;

	public FluidBarWidget(
			int x,
			int y,
			int width,
			int height,
			Supplier<Integer> fluidGetter,
			int maxFluid,
			ResourceTexture background,
			ResourceTexture foreground
	) {
		super(x, y, width, height);
		this.fluidGetter = fluidGetter;
		this.maxFluid = maxFluid;
		this.background = background;
		this.foreground = foreground;
	}

	@Override
	public void drawInBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		int fluid = fluidGetter.get();
		if (fluid <= 0 || maxFluid <= 0) {
			return;
		}
		float ratio = (float) fluid / maxFluid;
		if (ratio > 1f) {
			ratio = 1f;
		}
		int displayHeight = (int) (getSize().height * ratio);

		background.draw(
				graphics,
				mouseX,
				mouseY,
				getPosition().x,
				getPosition().y,
				getSize().width,
				getSize().height
		);

		graphics.enableScissor(
				getPosition().x,
				getPosition().y + getSize().height - displayHeight,
				getPosition().x + getSize().width,
				getPosition().y + getSize().height
		);
		foreground.draw(
				graphics,
				mouseX,
				mouseY,
				getPosition().x,
				getPosition().y,
				getSize().width,
				getSize().height
		);
		graphics.disableScissor();
	}
}