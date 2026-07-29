package dev.celestiacraft.deep_tech.tags;

import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.libs.tags.TagsBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class DeepTechItemTags {
	public static final TagKey<Item>
			WRENCH,
			MACHINES;

	static {
		WRENCH = TagsBuilder.item("tools/wrench").forge();
		MACHINES = TagsBuilder.item("machines").namespace(DeepTech.MODID);
	}
}