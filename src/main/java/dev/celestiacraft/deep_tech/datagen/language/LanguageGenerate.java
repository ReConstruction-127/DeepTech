package dev.celestiacraft.deep_tech.datagen.language;

import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.datagen.language.type.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LanguageGenerate {
	public static final List<List<String>> TRANSLATION_LIST = new ArrayList<>();
	public static final Map<String, String[]> CONFIG_LANG = new LinkedHashMap<>();
	public static boolean configLangRegistered = false;

	public static void register() {
		ItemLanguage.addLang();
		BlockLanguage.addLang();
		FluidLanguage.addLang();
		GuiLanguage.addLang();
		OtherLanguage.addLang();
		MaterialLanguage.addLang();
		JeiLanguage.addLang();
		registerConfigLang();
	}

	public static void registerConfigLang() {
		if (configLangRegistered) {
			return;
		}
		configLangRegistered = true;
		ConfigLanguage.addLang();
	}

	protected static void addConfigLang(String key, String english, String chinese) {
		CONFIG_LANG.put(key, new String[]{english, chinese});
		addCustomLang(String.format("config.%s.%s", DeepTech.MODID, key), english, chinese);
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

	protected static void addItemLanguage(String key, String english, String chinese) {
		addLanguage("item", key, english, chinese);
	}

	protected static void addBlockLanguage(String key, String english, String chinese) {
		addLanguage("block", key, english, chinese);
	}

	protected static void addFluidLanguage(String key, String english, String chinese) {
		addLanguage("fluid", key, english, chinese);
		addLanguage("block", key, english, chinese);
		addItemLanguage(key + "_bucket", english + " Bucket", chinese + "桶");
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

	protected static void addMaterialLang(String key, String english, String chinese) {
		// 物品: 与 Nebula Libs 材料系统生成的 ID 一一对应
		addItemLanguage(key + "_ingot", english + " Ingot", chinese + "锭");
		addItemLanguage(key + "_plate", english + " Plate", chinese + "板");
		addItemLanguage(key + "_nugget", english + " Nugget", chinese + "粒");
		addItemLanguage(key + "_dust", english + " Dust", chinese + "粉");
		addItemLanguage(key + "_rod", english + " Rod", chinese + "杆");
		addItemLanguage(key + "_gear", english + " Gear", chinese + "齿轮");
		addItemLanguage(key + "_wire", english + " Wire", chinese + "线材");
		addItemLanguage(key + "_prism", english + " Prism", chinese + "棱镜");

		// 粗矿
		addItemLanguage("raw_" + key, "Raw " + english, "粗" + chinese);

		// Mekanism 中间产物
		addItemLanguage("dirty_" + key + "_dust", "Dirty " + english + " Dust", "脏" + chinese + "粉");
		addItemLanguage(key + "_clump", english + " Clump", chinese + "团块");
		addItemLanguage(key + "_shard", english + " Shard", chinese + "碎片");
		addItemLanguage(key + "_crystal", english + " Crystal", chinese + "晶体");

		// 方块(方块物品的翻译键也是 block.*)
		addBlockLanguage(key + "_block", english + " Block", chinese + "块");
		addBlockLanguage("raw_" + key + "_block", "Raw " + english + " Block", "粗" + chinese + "块");

		// 熔融流体 + 桶
		addFluidLanguage("molten_" + key, "Molten " + english, "熔融" + chinese);
	}
}
