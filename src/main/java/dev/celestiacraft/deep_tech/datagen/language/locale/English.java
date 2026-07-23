package dev.celestiacraft.deep_tech.datagen.language.locale;

import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.datagen.language.LanguageGenerate;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

import java.util.List;

public class English extends LanguageProvider {
	public English(PackOutput output) {
		super(output, DeepTech.MODID, "en_us");
	}

	@Override
	protected void addTranslations() {
		for (List<String> item : LanguageGenerate.TRANSLATION_LIST) {
			add(item.get(0), item.get(1));
		}
	}
}