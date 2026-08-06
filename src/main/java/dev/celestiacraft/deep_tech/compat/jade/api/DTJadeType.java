package dev.celestiacraft.deep_tech.compat.jade.api;

import dev.celestiacraft.deep_tech.DeepTech;
import net.minecraft.resources.ResourceLocation;

public class DTJadeType {
	public static final ResourceLocation MACHINE = addType("machine");

	private static ResourceLocation addType(String path) {
		return DeepTech.loadResource(path);
	}
}