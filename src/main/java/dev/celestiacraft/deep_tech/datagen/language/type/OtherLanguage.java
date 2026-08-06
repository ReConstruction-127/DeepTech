package dev.celestiacraft.deep_tech.datagen.language.type;

import dev.celestiacraft.deep_tech.datagen.language.LanguageGenerate;

public class OtherLanguage extends LanguageGenerate {
	public static void addLang() {
		addJade();

		addCreativeTabLang(
				"material",
				"Deep Tech: Material",
				"深邃科技: 材料"
		);
		addCreativeTabLang(
				"machine",
				"Deep Tech: Machine",
				"深邃科技: 机器"
		);
		addCreativeTabLang(
				"tool",
				"Deep Tech: Tool",
				"深邃科技: 工具"
		);
	}

	private static void addJade() {
		addCustomLang(
				"config.jade.plugin_deep_tech.machine",
				"Deep Tech: Machine",
				"深邃科技: 机器"
		);
		addCustomLang(
				"tooltip.jade.deep_tech.info.max_receive",
				"Max Energy Receive: %s",
				"最大能量接收: %s FE / Tick"
		);
		addCustomLang(
				"tooltip.jade.deep_tech.info.max_extract",
				"Max Energy Extract: %s",
				"最大能量输出: %s FE / Tick"
		);
	}
}