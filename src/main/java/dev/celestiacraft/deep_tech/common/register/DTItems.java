package dev.celestiacraft.deep_tech.common.register;

import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.common.register.item.MaterialItems;
import dev.celestiacraft.deep_tech.common.register.item.ToolItems;

public class DTItems {
	public static void register() {
		MaterialItems.register();
		ToolItems.register();

		DeepTech.registerLog("Items");
	}
}