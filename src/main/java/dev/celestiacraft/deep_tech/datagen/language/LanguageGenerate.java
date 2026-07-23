package dev.celestiacraft.deep_tech.datagen.language;

import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.datagen.language.type.BlockLanguage;
import dev.celestiacraft.deep_tech.datagen.language.type.GuiLanguage;
import dev.celestiacraft.deep_tech.datagen.language.type.ItemLanguage;

import java.util.ArrayList;
import java.util.List;

public class LanguageGenerate {
	public static final List<List<String>> TRANSLATION_LIST = new ArrayList<>();

	public static void register() {
		ItemLanguage.addLang();
		BlockLanguage.addLang();
		GuiLanguage.addLang();
	}

	protected static void addLanguage(String type, String key, String english, String chinese) {
		String fullKey;

		if (type == null || type.isEmpty()) {
			fullKey = String.format("%s.%s", DeepTech.MODID, key);
		} else {
			fullKey = String.format("%s.%s.%s", type, DeepTech.MODID, key);
		}

		addCustomLang(fullKey, english, chinese);
	}

	/**
	 * 添加自定义翻译
	 *
	 * @param key     翻译键
	 * @param english 英文
	 * @param chinese 中文
	 */
	protected static void addCustomLang(String key, String english, String chinese) {
		List<String> newList = new ArrayList<>();
		newList.add(key);
		newList.add(english);
		newList.add(chinese);
		TRANSLATION_LIST.add(newList);
	}

	protected static void addBlockLanguage(String key, String english, String chinese) {
		addLanguage("block", key, english, chinese);
	}

	protected static void addItemLanguage(String key, String english, String chinese) {
		addLanguage("item", key, english, chinese);
	}

	protected static void addBiomeLanguage(String key, String english, String chinese) {
		addLanguage("biome", key, english, chinese);
	}

	protected static void addCreativeTabLang(String key, String english, String chinese) {
		addLanguage("itemGroup", key, english, chinese);
	}

	protected static void addTooltipLang(String key, String english, String chinese) {
		addCustomLang(String.format("cmi.tooltip.%s", key), english, chinese);
	}

	protected static void addJeiCategoryLang(String key, String english, String chinese) {
		addLanguage("jei.category", key, english, chinese);
	}

	protected static void addRecipeLang(String key, String english, String chinese) {
		addCustomLang(String.format("cmi.recipe.%s", key), english, chinese);
	}

	protected static void addEntityLang(String key, String english, String chinese) {
		addLanguage("entity", key, english, chinese);
	}

	protected static void addKeyLang(String key, String english, String chinese) {
		addLanguage("key", key, english, chinese);
	}

	protected static void addGuiLang(String key, String english, String chinese) {
		addLanguage("gui", key, english, chinese);
	}
}