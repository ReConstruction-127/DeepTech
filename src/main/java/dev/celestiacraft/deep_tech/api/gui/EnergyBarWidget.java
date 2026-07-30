package dev.celestiacraft.deep_tech.api.gui;

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

		int height = (int) (42.0F * energy / maxEnergy);

		if (height <= 0) {
			return;
		}

		graphics.enableScissor(
				getPosition().x,
				getPosition().y + 42 - height,
				getPosition().x + 14,
				getPosition().y + 42
		);

		FOREGROUND.draw(
				graphics,
				mouseX,
				mouseY,
				getPosition().x,
				getPosition().y,
				14,
				42
		);

		graphics.disableScissor();
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