package dev.celestiacraft.deep_tech.api.client.lang;

import dev.celestiacraft.deep_tech.datagen.language.LanguageGenerate;

import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigLang {
	private static final Map<String, String[]> CONFIG_LANG = new LinkedHashMap<>();
	private static boolean configLangRegistered = false;

	/**
	 * 读取配置注释的英文文本
	 *
	 * @param key
	 * @return
	 */
	public static String addConfigKey(String key) {
		LanguageGenerate.registerConfigLang();
		String[] entry = CONFIG_LANG.get(key);
		return entry == null ? key : entry[0];
	}
}