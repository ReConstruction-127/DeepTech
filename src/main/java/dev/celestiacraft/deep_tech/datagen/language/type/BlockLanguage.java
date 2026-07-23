package dev.celestiacraft.deep_tech.datagen.language.type;

import dev.celestiacraft.deep_tech.datagen.language.LanguageGenerate;

public class BlockLanguage extends LanguageGenerate {
	public static void addLang() {
		addBlockLanguage(
				"machine_crusher",
				"Crusher",
				"粉碎机"
		);
		addBlockLanguage(
				"machine_frame",
				"Machine Frame",
				"机器框架"
		);
	}
}