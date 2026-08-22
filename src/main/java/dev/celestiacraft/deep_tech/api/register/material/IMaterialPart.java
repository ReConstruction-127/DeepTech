package dev.celestiacraft.deep_tech.api.register.material;

public interface IMaterialPart {
	IMaterialPart INGOT = addPart("ingot");
	IMaterialPart NUGGET = addPart("nugget");
	IMaterialPart PLATE = addPart("plate");
	IMaterialPart DUST = addPart("dust");
	IMaterialPart RAW_MATERIAL = addPart("raw", "raw_materials");

	String getPath();

	String getTagFolder();

	default String getMaterialTag(String material) {
		return String.format("%s/%s", getTagFolder(), material);
	}

	default String getModelPath(String material) {
		return String.format("item/material/%s/%s", getPath(), material);
	}

	static IMaterialPart addPart(String name, String folder) {
		return new SimpleMaterialPart(name, folder);
	}

	static IMaterialPart addPart(String name) {
		return addPart(name, name + "s");
	}
}