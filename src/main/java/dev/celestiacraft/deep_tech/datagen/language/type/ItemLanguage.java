package dev.celestiacraft.deep_tech.datagen.language.type;

import dev.celestiacraft.deep_tech.datagen.language.LanguageGenerate;

public class ItemLanguage extends LanguageGenerate {
	public static void addLang() {
		addItemLanguage(
				"wrench",
				"Wrench",
				"扳手"
		);
		addItemLanguage(
				"sculk_chunk",
				"Sculk Chunk",
				"幽匿碎块"
		);
		addItemLanguage(
				"sculk_alloy",
				"Sculk Alloy",
				"幽匿合金"
		);
	}
}