package dev.celestiacraft.deep_tech.datagen.language.locale;

import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.datagen.language.LanguageGenerate;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

import java.util.List;

public class Chinese extends LanguageProvider {
	public Chinese(PackOutput output) {
		super(output, DeepTech.MODID, "zh_cn");
	}

	@Override
	protected void addTranslations() {
		// Common
		for (List<String> item : LanguageGenerate.TRANSLATION_LIST) {
			add(item.get(0), item.get(2));
		}
	}
}