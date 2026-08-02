package dev.celestiacraft.deep_tech.api.gui.widget;

import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;


public class ProgressBarWidget extends Widget {
	private final Supplier<Integer> progressGetter;
	private final Supplier<Integer> maxProgressGetter;

	private final ResourceTexture background;
	private final ResourceTexture foreground;

	public ProgressBarWidget(
			int x,
			int y,
			int width,
			int height,
			Supplier<Integer> progressGetter,
			Supplier<Integer> maxProgressGetter,
			ResourceTexture background,
			ResourceTexture foreground
	) {
		super(x, y, width, height);
		this.progressGetter = progressGetter;
		this.maxProgressGetter = maxProgressGetter;
		this.background = background;
		this.foreground = foreground;
	}

	@Override
	public void drawInBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		int progress = progressGetter.get();
		int maxProgress = maxProgressGetter.get();

		background.draw(
				graphics,
				mouseX,
				mouseY,
				getPosition().x,
				getPosition().y,
				getSize().width,
				getSize().height
		);

		if (progress <= 0 || maxProgress <= 0) {
			return;
		}

		float ratio = (float) progress / maxProgress;
		if (ratio > 1.0f) {
			ratio = 1.0f;
		}

		foreground.drawSubArea(
				graphics,
				getPosition().x,
				getPosition().y,
				getSize().width * ratio,
				getSize().height,
				0.0F,
				0.0F,
				ratio,
				1.0F
		);
	}
}