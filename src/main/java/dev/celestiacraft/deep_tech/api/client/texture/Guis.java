package dev.celestiacraft.deep_tech.api.client.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.celestiacraft.deep_tech.DeepTech;
import net.createmod.catnip.gui.element.ScreenElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class Guis implements ScreenElement {
	public final ResourceLocation location;
	public final int width;
	public final int height;
	public final int startX;
	public final int startY;

	public Guis(String path, int width, int height) {
		this(path, 0, 0, width, height);
	}

	public Guis(String path, int startX, int startY, int width, int height) {
		location = DeepTech.loadResource(String.format("textures/gui/%s.png", path));
		this.startX = startX;
		this.startY = startY;
		this.width = width;
		this.height = height;
	}

	public void bind() {
		RenderSystem.setShaderTexture(0, location);
	}

	@Override
	public void render(GuiGraphics graphics, int x, int y) {
		graphics.blit(location, x, y, startX, startY, width, height);
	}
}