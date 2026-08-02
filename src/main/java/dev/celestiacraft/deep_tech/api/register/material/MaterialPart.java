package dev.celestiacraft.deep_tech.api.register.material;

import lombok.Getter;

@Getter
public enum MaterialPart {
	INGOT("ingot", "ingots"),
	NUGGET("nugget", "nuggets"),
	PLATE("plate", "plates"),
	DUST("dust", "dusts"),
	RAW_MATERIAL("raw", "raw_materials");

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