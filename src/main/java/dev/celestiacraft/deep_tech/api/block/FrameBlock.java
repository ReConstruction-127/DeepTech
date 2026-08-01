package dev.celestiacraft.deep_tech.api.block;

import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import dev.celestiacraft.deep_tech.api.block.properties.BlockTagGen;
import dev.celestiacraft.deep_tech.api.block.properties.MiningLevel;
import dev.celestiacraft.deep_tech.api.client.model.ItemModelGen;
import dev.celestiacraft.deep_tech.tags.DeepTechBlockTags;
import dev.celestiacraft.libs.api.register.block.BasicBlock;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockModelProvider;

public class FrameBlock extends BasicBlock {
	public FrameBlock(Properties properties) {
		super(properties.sound(SoundType.DEEPSLATE_BRICKS)
				.strength(5.0F, 5.0F)
				.requiresCorrectToolForDrops());
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> genBlockState(String name) {
		return (context, provider) -> {
			String id = "%s_frame".formatted(name);
			BlockModelProvider models = provider.models();

			BlockModelBuilder model = models.cubeBottomTop(
					id,
					provider.modLoc("block/%s_frame_side".formatted(name)),
					provider.modLoc("block/%s_frame_top".formatted(name)),
					provider.modLoc("block/%s_frame_top".formatted(name))
			);

			provider.simpleBlock(context.get(), model);
		};
	}

	public static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> item(String name) {
		return ItemModelGen.withModel("block/%s_frame".formatted(name));
	}

	public static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, BlockBuilder<T, P>> miniProperties(MiningLevel level) {
		return (builder) -> {
			return builder.tag(DeepTechBlockTags.WRENCH_PICKUP)
					.transform(BlockTagGen.needMiniLevel(level))
					.transform(BlockTagGen.onlyPickaxe());
		};
	}
}