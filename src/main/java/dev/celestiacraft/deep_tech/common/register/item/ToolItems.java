package dev.celestiacraft.deep_tech.common.register.item;

import com.tterrag.registrate.util.entry.ItemEntry;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.api.client.model.ItemModelGen;
import dev.celestiacraft.deep_tech.common.item.tool.WrenchItem;
import dev.celestiacraft.deep_tech.common.register.DTCreativeTabs;
import dev.celestiacraft.deep_tech.tags.DeepTechItemTags;

public class ToolItems {
	public static final ItemEntry<WrenchItem> WRENCH;

	static {
		DTCreativeTabs.getTab("tool");

		WRENCH = DeepTech.REGISTRATE.item("wrench", WrenchItem::new)
				.model(ItemModelGen.handheld("item/tool/wrench"))
				.tag(DeepTechItemTags.WRENCH)
				.register();
	}

	public static void register() {
		DeepTech.registerLog("Tool Items");
	}
}