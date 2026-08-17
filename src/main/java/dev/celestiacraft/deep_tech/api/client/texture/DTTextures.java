package dev.celestiacraft.deep_tech.api.client.texture;

public class DTTextures {
	public static final Guis
			PROGRESS_CRUSHER,
			PROGRESS_ALLOYER,
			PROGRESS_COLLECTOR,
			ICON_CRUSHER,
			ICON_ALLOYER,
			ICON_COLLECTOR,
			RIGHT_CLICK,
			LEFT_CLICK,
			RECYCLE,
			INTERACTION_ICON,
			INTERACTION_BACKGROUND;

	static {
		PROGRESS_CRUSHER = addGuiTexture("elements/jei/elements", 0, 0, 16, 7);
		PROGRESS_ALLOYER = addGuiTexture("elements/jei/elements", 0, 7, 10, 15);
		PROGRESS_COLLECTOR = addGuiTexture("elements/jei/elements", 0, 22, 12, 9);
		ICON_CRUSHER = addGuiTexture("elements/jei/elements", 224, 0, 32, 32);
		ICON_ALLOYER = addGuiTexture("elements/jei/elements", 224, 32, 32, 32);
		ICON_COLLECTOR = addGuiTexture("elements/jei/elements", 224, 64, 32, 32);
		RIGHT_CLICK = addGuiTexture("elements/jei/elements", 17, 1, 18, 18);
		LEFT_CLICK = addGuiTexture("elements/jei/elements", 17, 21, 18, 18);
		RECYCLE = addGuiTexture("elements/jei/elements", 17, 41, 18, 18);
		INTERACTION_ICON = addGuiTexture("elements/jei/elements", 17, 61, 18, 18);
		INTERACTION_BACKGROUND = addGuiTexture("elements/jei/elements", 37, 1, 96, 71);
	}

	public static Guis addGuiTexture(String path, int startX, int startY, int width, int height) {
		return new Guis(path, startX, startY, width, height);
	}

	public static Guis addGuiTexture(String path, int width, int height) {
		return new Guis(path, 0, 0, width, height);
	}
}