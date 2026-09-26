package dev.celestiacraft.deep_tech.datagen.language.type;

import dev.celestiacraft.deep_tech.datagen.language.LanguageGenerate;

public class ItemLanguage extends LanguageGenerate {
	public static void addLang() {
		addItemLanguage(
				"test_tube",
				"Test Tube",
				"试管"
		);
		addCustomLang(
				"tooltip.deep_tech.test_tube.empty",
				"Empty (Capacity: %s mB)",
				"空（容量：%s mB）"
		);
		addCustomLang(
				"tooltip.deep_tech.test_tube.contents",
				"%s %s mB/%s mB",
				"%s %smb/%smb"
		);
		addItemLanguage(
				"wrench",
				"Wrench",
				"扳手"
		);
		addItemLanguage(
				"sculk_chunk",
				"Sculk Chunk",
				"幽匿碎块"
		);
		addItemLanguage(
				"sculk_bone",
				"Sculk Bone",
				"幽匿之骨"
		);
		addItemLanguage(
				"sculk_bonemeal",
				"Sculk Bonemeal",
				"幽匿骨粉"
		);
		addItemLanguage(
				"sculk_circuit",
				"Sculk Circuit",
				"幽匿电路"
		);
		addItemLanguage(
				"dense_sculk_chunk",
				"Dense Sculk Chunk",
				"致密幽匿碎块"
		);
		addItemLanguage(
				"advanced_sculk_control_circuit",
				"Advanced Sculk Control Circuit",
				"高级幽匿控制电路"
		);
		addItemLanguage(
				"alkaloid_powder",
				"Alkaloid Powder",
				"植物碱粉末"
		);
	}
}
