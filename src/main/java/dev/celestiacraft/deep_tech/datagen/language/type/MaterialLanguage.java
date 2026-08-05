package dev.celestiacraft.deep_tech.datagen.language.type;

import dev.celestiacraft.deep_tech.datagen.language.LanguageGenerate;

public class MaterialLanguage extends LanguageGenerate {
	public static void addLang() {
		addMaterialLang("copper", "Copper", "铜");
		addMaterialLang("iron", "Iron", "铁");
		addMaterialLang("gold", "Gold", "金");
	}
}