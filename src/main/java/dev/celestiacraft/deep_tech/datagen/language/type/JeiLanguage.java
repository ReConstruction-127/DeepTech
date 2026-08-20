package dev.celestiacraft.deep_tech.datagen.language.type;

import dev.celestiacraft.deep_tech.datagen.language.LanguageGenerate;

public class JeiLanguage extends LanguageGenerate {
	public static void addLang() {
		addCustomLang(
				"jei.deep_tech.energy_cost",
				"⚡ %s FE / tick",
				"⚡ %s FE / tick"
		);
		addCustomLang(
				"jei.deep_tech.time_cost",
				"⏱ %s tick",
				"⏱ %s tick"
		);
		addCustomLang(
				"jei.deep_tech.output_chance",
				"Chance: %s%%",
				"产出概率: %s%%"
		);
		addCustomLang(
				"jei.deep_tech.catalyst_no_consume",
				"(does not consume)",
				"(不消耗)"
		);
	}
}