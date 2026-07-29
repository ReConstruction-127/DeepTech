package dev.celestiacraft.deep_tech.api.block;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.libs.api.register.block.BasicBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockModelProvider;

public class FrameBlock extends BasicBlock {
	public FrameBlock(Properties properties) {
		super(properties);
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> genBlockState(String name) {
		return (context, provider) -> {
			BlockModelProvider models = provider.models();
			BlockModelBuilder model = models.cubeBottomTop(
					name,
					provider.modLoc("block/%s_side".formatted(name)),
					provider.modLoc("block/%s_top".formatted(name)),
					provider.modLoc("block/%s_top".formatted(name))
			);
			provider.simpleBlock(context.get(), model);
		};
	}
}