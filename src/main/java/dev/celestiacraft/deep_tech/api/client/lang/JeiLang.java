package dev.celestiacraft.deep_tech.api.client.lang;

import dev.celestiacraft.deep_tech.DeepTech;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class JeiLang {
	public static MutableComponent setTranCategoryTitle(String key) {
		return Component.translatable(String.format("jei.category.%s.%s", DeepTech.MODID, key));
	}
}