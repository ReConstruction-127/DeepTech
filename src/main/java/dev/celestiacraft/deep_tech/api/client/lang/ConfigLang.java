package dev.celestiacraft.deep_tech.api.client.lang;

import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.datagen.language.LanguageGenerate;

public class ConfigLang {
	/**
	 * 读取配置注释的英文文本
	 *
	 * @param key
	 * @return
	 */
	public static String addConfigKey(String key) {
		LanguageGenerate.registerConfigLang();
		String[] entry = LanguageGenerate.CONFIG_LANG.get(key);
		return entry == null ? key : entry[0];
	}

	/**
	 * 返回配置项的翻译键(config.deep_tech.&lt;key&gt;),供 ForgeConfigSpec#translation 使用,
	 * 使 Configured 等配置界面能按当前游戏语言显示(zh_cn 显示中文)
	 *
	 * @param key
	 * @return
	 */
	public static String addConfigTranslationKey(String key) {
		LanguageGenerate.registerConfigLang();
		return "config." + DeepTech.MODID + "." + key;
	}
}