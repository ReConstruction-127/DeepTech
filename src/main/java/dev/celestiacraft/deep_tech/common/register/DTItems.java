package dev.celestiacraft.deep_tech.common.register;

import com.tterrag.registrate.util.entry.ItemEntry;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.api.client.ItemModelGen;
import dev.celestiacraft.libs.api.register.item.BasicItem;

public class DTItems {
	public static ItemEntry<BasicItem> SCULK_CHUNK;
	public static ItemEntry<BasicItem> SCULK_ALLOY;

	static {
		SCULK_CHUNK = DeepTech.REGISTRATE.item("sculk_chunk", BasicItem::new)
				.model(ItemModelGen.generated("item/sculk_chunk"))
				.register();

		SCULK_ALLOY = DeepTech.REGISTRATE.item("sculk_alloy", BasicItem::new)
				.model(ItemModelGen.generated("item/sculk_alloy"))
				.register();
	}

	public static void register() {
		DeepTech.registerLog("Items");
	}
}