package dev.celestiacraft.deep_tech.api.register.material;

import lombok.Getter;

@Getter
public enum MaterialPart {
	INGOT("ingot", "ingots"),
	PLATE("plate", "plates"),
	DUST("dust", "dusts"),
	RAW_MATERIAL("raw_material", "raw_materials");

	private final String path;
	private final String tagFolder;

	MaterialPart(String path, String tagFolder) {
		this.path = path;
		this.tagFolder = tagFolder;
	}

	public String getMaterialTag(String material) {
		return String.format("%s/%s", tagFolder, material);
	}
}