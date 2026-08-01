package dev.celestiacraft.deep_tech.common.register;

import dev.celestiacraft.deep_tech.api.register.material.DTMaterial;

import java.util.ArrayList;
import java.util.List;

public class DTMaterials {
	private static final List<DTMaterial> MATERIALS = new ArrayList<>();

	public static final DTMaterial
			COPPER;

	static {
		DTCreativeTabs.getTab("material");

		COPPER = addMaterial("copper")
				.nugget();
	}

	private static DTMaterial addMaterial(String id) {
		DTMaterial material = new DTMaterial(id);
		MATERIALS.add(material);
		return material;
	}

	public static void register() {
		MATERIALS.forEach(DTMaterial::registerMaterial);
	}
}