package dev.celestiacraft.deep_tech.api.client.texture;

import dev.celestiacraft.deep_tech.DeepTech;
import net.minecraft.resources.ResourceLocation;

public class DTFluidTextures {
	public static final DTFluidTexture
			EXP;

	static {
		EXP = add("liquid_experience/flowing", "liquid_experience/still");
	}

	private static DTFluidTexture add(String flowing, String still) {
		return add(loadFluid(flowing), loadFluid(still));
	}

	private static DTFluidTexture add(ResourceLocation flowing, ResourceLocation still) {
		return DTFluidTexture.of(flowing, still);
	}

	private static ResourceLocation loadFluid(String path) {
		return DeepTech.loadResource("fluid/%s".formatted(path));
	}
}
