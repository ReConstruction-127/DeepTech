package dev.celestiacraft.deep_tech.tags;

import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.libs.tags.TagsBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class DeepTechBlockTags {
	public static final TagKey<Block>
			WRENCH_PICKUP,
			MACHINES;

	static {
		WRENCH_PICKUP = TagsBuilder.block("wrench_pickup").namespace(DeepTech.MODID);
		MACHINES = TagsBuilder.block("machines").namespace(DeepTech.MODID);
	}
}