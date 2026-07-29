package dev.celestiacraft.deep_tech.common.register;

import dev.celestiacraft.deep_tech.api.register.material.DTMaterial;

import java.util.ArrayList;
import java.util.List;

public class DTMaterials {
	private static final List<DTMaterial> MATERIALS = new ArrayList<>();

	public static final DTMaterial
			IRON,
			COPPER;

	static {
		DTCreativeTabs.getTab("material");

		/*
		 * 平时调用只需要
		 * DTMaterials.IRON.getDust()
		 * DTMaterials.COPPER.getPlate()
		 * 就可以了
		 */

		IRON = addMaterial("iron")
				.plate()
				.dust();

		COPPER = addMaterial("copper")
				.plate()
				.dust();
	}

	private static DTMaterial addMaterial(String id) {
		DTMaterial material = new DTMaterial(id);
		MATERIALS.add(material);
		return material;
	}

//	public static void register() {
//		MATERIALS.forEach(DTMaterial::registerMaterial);
//	}
}