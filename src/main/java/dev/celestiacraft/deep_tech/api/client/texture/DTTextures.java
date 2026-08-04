package dev.celestiacraft.deep_tech.api.client.texture;

public class DTTextures {
	public static final Guis
			PROGRESS_CRUSHER,
			PROGRESS_ALLOYER,
			ICON_CRUSHER,
			ICON_ALLOYER;

	static {
		PROGRESS_CRUSHER = addGuiTexture("elements/jei/elements", 0, 0, 16, 7);
		PROGRESS_ALLOYER = addGuiTexture("elements/jei/elements", 0, 7, 10, 15);
		ICON_CRUSHER = addGuiTexture("elements/jei/elements", 224, 0, 32, 32);
		ICON_ALLOYER = addGuiTexture("elements/jei/elements", 224, 32, 32, 32);
	}

	public static Guis addGuiTexture(String path, int startX, int startY, int width, int height) {
		return new Guis(path, startX, startY, width, height);
	}

	public static Guis addGuiTexture(String path, int width, int height) {
		return new Guis(path, 0, 0, width, height);
	}
}