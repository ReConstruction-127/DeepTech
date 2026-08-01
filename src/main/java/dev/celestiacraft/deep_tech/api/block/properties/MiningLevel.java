package dev.celestiacraft.deep_tech.api.block.properties;

import lombok.Getter;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;

import java.util.Locale;

public enum MiningLevel {
	WOODEN(Tags.Blocks.NEEDS_WOOD_TOOL),
	STONE(BlockTags.NEEDS_STONE_TOOL),
	IRON(BlockTags.NEEDS_IRON_TOOL),
	GOLD(Tags.Blocks.NEEDS_GOLD_TOOL),
	DIAMOND(BlockTags.NEEDS_DIAMOND_TOOL),
	NETHER(Tags.Blocks.NEEDS_NETHERITE_TOOL);

	@Getter
	private final TagKey<Block> tag;

	MiningLevel(TagKey<Block> tag) {
		this.tag = tag;
	}

	public static MiningLevel from(String key) {
		try {
			return valueOf(key.toUpperCase(Locale.ROOT));
		} catch (Exception exception) {
			throw new IllegalArgumentException("Unknown ToolType: " + key);
		}
	}
}