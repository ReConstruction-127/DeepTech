package dev.celestiacraft.deep_tech.api.client.texture;

public class DTTextures {
	public static final Guis
			PROGRESS_FRONT,
			ICON_CRUSHER;

	static {
		PROGRESS_FRONT = addGuiTexture("elements/jei/elements", 0, 0, 16, 7);
		ICON_CRUSHER = addGuiTexture("elements/jei/elements", 224, 0, 32, 32);
	}

	public static Guis addGuiTexture(String path, int startX, int startY, int width, int height) {
		return new Guis(path, startX, startY, width, height);
	}

	public static Guis addGuiTexture(String path, int width, int height) {
		return new Guis(path, 0, 0, width, height);
	}
}