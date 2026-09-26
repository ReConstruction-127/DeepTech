package dev.celestiacraft.deep_tech.common.register.item;

import com.tterrag.registrate.util.entry.ItemEntry;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.api.client.model.ItemModelGen;
import dev.celestiacraft.deep_tech.common.item.TestTubeItem;
import dev.celestiacraft.deep_tech.common.item.tool.WrenchItem;
import dev.celestiacraft.deep_tech.common.register.DTCreativeTabs;
import dev.celestiacraft.deep_tech.tags.DeepTechItemTags;

public class ToolItems {
	public static final ItemEntry<WrenchItem> WRENCH;
	public static final ItemEntry<TestTubeItem> TEST_TUBE;

	static {
		WRENCH = DeepTech.REGISTRATE.item("wrench", WrenchItem::new)
				.model(ItemModelGen.handheld("item/tool/wrench"))
				.tab(DTCreativeTabs.TOOL.getKey())
				.tag(DeepTechItemTags.WRENCH)
				.register();

		TEST_TUBE = DeepTech.REGISTRATE.item("test_tube", TestTubeItem::new)
				.model((context, provider) -> provider.withExistingParent(context.getName(), provider.mcLoc("item/generated"))
						.texture("layer0", provider.modLoc("item/tool/test_tube"))
						.override()
						.predicate(provider.modLoc("filled"), 1.0F)
						.model(provider.getExistingFile(provider.modLoc("item/test_tube_filled")))
						.end())
				.tab(DTCreativeTabs.TOOL.getKey())
				.register();
	}

	public static void register() {
		DeepTech.registerLog("Tool Items");
	}
}
