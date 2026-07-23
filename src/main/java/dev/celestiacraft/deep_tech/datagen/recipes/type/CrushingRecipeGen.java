package dev.celestiacraft.deep_tech.datagen.recipes.type;

import dev.celestiacraft.deep_tech.api.server.recipe.builder.crushing.CrushingRecipeBuilder;
import dev.celestiacraft.deep_tech.datagen.recipes.DTRecipeProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class CrushingRecipeGen extends DTRecipeProvider {
	public CrushingRecipeGen(PackOutput output) {
		super(output);
	}

	public static void register(Consumer<FinishedRecipe> consumer) {
		CrushingRecipeBuilder.builder()
				.input(Tags.Items.COBBLESTONE)
				.output(Blocks.GRAVEL)
				.energyCost(200)
				.processingTime(150)
				.save(consumer, save("crushing/gravel"));
	}
}