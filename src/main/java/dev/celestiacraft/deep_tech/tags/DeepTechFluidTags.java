package dev.celestiacraft.deep_tech.tags;

import dev.celestiacraft.libs.tags.TagsBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

public class DeepTechFluidTags {
	public static final TagKey<Fluid>
			EXPERIENCE;

	static {
		EXPERIENCE = TagsBuilder.fluid("experience").forge();
	}
}