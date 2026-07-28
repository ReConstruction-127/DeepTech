package dev.celestiacraft.deep_tech.datagen.recipes.type;

import dev.celestiacraft.deep_tech.api.server.recipe.builder.crushing.CrushingRecipeBuilder;
import dev.celestiacraft.deep_tech.datagen.recipes.DTRecipeProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;
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
				.energyCost(10)
				.processingTime(100)
				.save(consumer, save("crushing/gravel"));

		CrushingRecipeBuilder.builder()
				.input(Blocks.STONE)
				.output(Blocks.COBBLESTONE)
				.energyCost(10)
				.processingTime(100)
				.save(consumer, save("crushing/cobblestone"));

		CrushingRecipeBuilder.builder()
				.input(Blocks.GRAVEL)
				.output(Blocks.SAND)
				.energyCost(10)
				.processingTime(100)
				.save(consumer, save("crushing/sand"));

		CrushingRecipeBuilder.builder()
				.input(Tags.Items.RAW_MATERIALS_IRON)
				.output(Items.IRON_NUGGET,12)
				.energyCost(15)
				.processingTime(160)
				.save(consumer, save("crushing/raw_iron"));
	}
}