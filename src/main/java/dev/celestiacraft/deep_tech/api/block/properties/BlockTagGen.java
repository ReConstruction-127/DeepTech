package dev.celestiacraft.deep_tech.api.block.properties;

import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;

public class BlockTagGen {
	public static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, BlockBuilder<T, P>> onlyPickaxe() {
		return (builder) -> {
			return builder.tag(BlockTags.MINEABLE_WITH_PICKAXE);
		};
	}

	public static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, BlockBuilder<T, P>> onlyAxe() {
		return (builder) -> {
			return builder.tag(BlockTags.MINEABLE_WITH_AXE);
		};
	}

	public static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, BlockBuilder<T, P>> pickaxeOrAxe() {
		return (builder) -> {
			return builder.tag(BlockTags.MINEABLE_WITH_AXE)
					.tag(BlockTags.MINEABLE_WITH_PICKAXE);
		};
	}

	public static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, BlockBuilder<T, P>> needMiniLevel(MiningLevel level) {
		return (builder) -> {
			return builder.tag(level.getTag());
		};
	}

	public static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, BlockBuilder<T, P>> needWooden() {
		return needMiniLevel(MiningLevel.WOODEN);
	}

	public static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, BlockBuilder<T, P>> needStone() {
		return needMiniLevel(MiningLevel.STONE);
	}

	public static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, BlockBuilder<T, P>> needIron() {
		return needMiniLevel(MiningLevel.IRON);
	}

	public static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, BlockBuilder<T, P>> needGold() {
		return needMiniLevel(MiningLevel.GOLD);
	}

	public static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, BlockBuilder<T, P>> needDiamond() {
		return needMiniLevel(MiningLevel.DIAMOND);
	}

	public static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, BlockBuilder<T, P>> needNether() {
		return needMiniLevel(MiningLevel.NETHER);
	}
}