package dev.celestiacraft.deep_tech.datagen.language.type;

import dev.celestiacraft.deep_tech.datagen.language.LanguageGenerate;

public class MaterialLanguage extends LanguageGenerate {
	public static void addLang() {
		addMaterialLang("copper", "Copper", "铜");
		addMaterialLang("iron", "Iron", "铁");
		addMaterialLang("gold", "Gold", "金");
		addMaterialLang("sculk_alloy", "Sculk Alloy", "幽匿合金");
		addMaterialLang("sculk_steel", "Sculk Steel", "幽钢");
	}
}