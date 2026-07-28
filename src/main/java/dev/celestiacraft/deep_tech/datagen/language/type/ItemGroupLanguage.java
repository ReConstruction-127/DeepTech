package dev.celestiacraft.deep_tech.datagen.language.type;

import dev.celestiacraft.deep_tech.datagen.language.LanguageGenerate;

public class ItemGroupLanguage extends LanguageGenerate {
	public static void addLang() {
		addCreativeTabLang(
				"material",
				"DeepTech - Material",
				"深邃科技 - 材料"
		);
		addCreativeTabLang(
				"machine",
				"DeepTech - Machine",
				"深邃科技 - 机器"
		);
	}
}