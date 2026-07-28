package dev.celestiacraft.deep_tech.api.client.texture;

public class DTTextures {
	public static final Guis
			PROGRESS_FRONT;

	static {
		PROGRESS_FRONT = addGuiTexture("elements/elements", 16, 6);
	}

	public static Guis addGuiTexture(String path, int startX, int startY, int width, int height) {
		return new Guis(path, startX, startY, width, height);
	}

	public static Guis addGuiTexture(String path, int width, int height) {
		return new Guis(path, 0, 0, width, height);
	}
}