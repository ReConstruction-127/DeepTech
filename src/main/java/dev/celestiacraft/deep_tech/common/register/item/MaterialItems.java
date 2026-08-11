package dev.celestiacraft.deep_tech.common.register.item;

import com.tterrag.registrate.util.entry.ItemEntry;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.api.client.model.ItemModelGen;
import dev.celestiacraft.deep_tech.common.register.DTCreativeTabs;
import dev.celestiacraft.libs.api.register.item.BasicItem;

public class MaterialItems {
	public static ItemEntry<BasicItem> SCULK_ALLOY;
	public static ItemEntry<BasicItem> SCULK_CHUNK;
	public static ItemEntry<BasicItem> SCULK_BONE;
	public static ItemEntry<BasicItem> SCULK_BONEMEAL;
	public static ItemEntry<BasicItem> SCULK_CIRCUIT;

	static {
		DTCreativeTabs.getTab("material");

		SCULK_ALLOY = DeepTech.REGISTRATE.item("sculk_alloy", BasicItem::new)
				.model(ItemModelGen.generated("item/sculk_alloy"))
				.tab(DTCreativeTabs.getTabKey("material"))
				.register();

		SCULK_CHUNK = DeepTech.REGISTRATE.item("sculk_chunk", BasicItem::new)
				.model(ItemModelGen.generated("item/sculk_chunk"))
				.tab(DTCreativeTabs.getTabKey("material"))
				.register();

		SCULK_BONE = DeepTech.REGISTRATE.item("sculk_bone", BasicItem::new)
				.model(ItemModelGen.generated("item/sculk_bone"))
				.tab(DTCreativeTabs.getTabKey("material"))
				.register();

		SCULK_BONEMEAL = DeepTech.REGISTRATE.item("sculk_bonemeal", BasicItem::new)
				.model(ItemModelGen.generated("item/sculk_bonemeal"))
				.tab(DTCreativeTabs.getTabKey("material"))
				.register();

		SCULK_CIRCUIT = DeepTech.REGISTRATE.item("sculk_circuit", BasicItem::new)
				.model(ItemModelGen.generated("item/sculk_circuit"))
				.tab(DTCreativeTabs.getTabKey("material"))
				.register();
	}

	public static void register() {
		DeepTech.registerLog("Material Items");
	}
}