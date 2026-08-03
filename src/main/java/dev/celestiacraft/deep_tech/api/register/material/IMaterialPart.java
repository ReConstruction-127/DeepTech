package dev.celestiacraft.deep_tech.api.register.material;

public interface IMaterialPart {
	IMaterialPart INGOT = registerPart("ingot", "ingots");
	IMaterialPart NUGGET = registerPart("nugget", "nuggets");
	IMaterialPart PLATE = registerPart("plate", "plates");
	IMaterialPart DUST = registerPart("dust", "dusts");
	IMaterialPart RAW_MATERIAL = registerPart("raw", "raw_materials");

	String getPath();

	String getTagFolder();

	default String getMaterialTag(String material) {
		return String.format("%s/%s", getTagFolder(), material);
	}

	default String getModelPath(String material) {
		return String.format("item/material/%s/%s", getPath(), material);
	}

	static IMaterialPart registerPart(String name, String folder) {
		return new SimpleMaterialPart(name, folder);
	}
}