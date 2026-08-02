package dev.celestiacraft.deep_tech.api.gui.widget;

import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import dev.celestiacraft.deep_tech.DeepTech;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class EnergyBarWidget extends Widget {
	private final Supplier<Integer> energy;
	private final int maxEnergy;

	private final ResourceTexture BACKGROUND = new ResourceTexture(DeepTech.loadResource("textures/gui/elements/energy_back.png"));
	private final ResourceTexture FOREGROUND = new ResourceTexture(DeepTech.loadResource("textures/gui/elements/energy_front.png"));

	public EnergyBarWidget(int x, int y, Supplier<Integer> energy, int maxEnergy) {
		super(x, y, 14, 42);
		this.energy = energy;
		this.maxEnergy = maxEnergy;
	}

	@Override
	public void drawInBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		int energy = this.energy.get();
		BACKGROUND.draw(
				graphics,
				mouseX,
				mouseY,
				getPosition().x,
				getPosition().y,
				14,
				42
		);

		if (energy <= 0 || maxEnergy <= 0) {
			return;
		}

		float ratio = (float) energy / maxEnergy;
		if (ratio > 1.0f) {
			ratio = 1.0f;
		}

		FOREGROUND.drawSubArea(
				graphics,
				getPosition().x,
				getPosition().y + getSize().height * (1.0F - ratio),
				getSize().width,
				getSize().height * ratio,
				0.0F,
				1.0F - ratio,
				1.0F,
				ratio
		);
	}

	@Override
	public void drawInForeground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		super.drawInForeground(graphics, mouseX, mouseY, partialTicks);

		if (isMouseOverElement(mouseX, mouseY)) {
			String text = String.format("%s FE / %s FE", energy.get(), maxEnergy);
			setHoverTooltips(Component.literal(text));
		}
	}
}