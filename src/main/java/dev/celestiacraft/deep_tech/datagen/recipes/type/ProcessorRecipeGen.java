package dev.celestiacraft.deep_tech.datagen.recipes.type;

import dev.celestiacraft.deep_tech.api.recipe.builder.processor.ProcessorRecipeBuilder;
import dev.celestiacraft.deep_tech.common.register.DTMaterials;
import dev.celestiacraft.deep_tech.datagen.recipes.DTRecipeProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class ProcessorRecipeGen extends DTRecipeProvider {
	public ProcessorRecipeGen(PackOutput output) {
		super(output);
	}

	public static void addRecipes(Consumer<FinishedRecipe> consumer) {
		ProcessorRecipeBuilder.builder()
				.itemInput(Items.IRON_INGOT)
				.itemOutput(DTMaterials.IRON.getPlate())
				.energyCost(20)
				.processingTime(20 * 3)
				.save(consumer, save("processing/iron_plate"));

		ProcessorRecipeBuilder.builder()
				.itemInput(Items.COPPER_INGOT)
				.itemOutput(DTMaterials.COPPER.getPlate())
				.energyCost(20)
				.processingTime(20 * 3)
				.save(consumer, save("processing/copper_plate"));

		ProcessorRecipeBuilder.builder()
				.itemInput(Items.GOLD_INGOT)
				.itemOutput(DTMaterials.GOLD.getPlate())
				.energyCost(20)
				.processingTime(20 * 3)
				.save(consumer, save("processing/gold_plate"));

		ProcessorRecipeBuilder.builder()
				.itemInput(DTMaterials.SCULK_ALLOY.getIngot())
				.itemOutput(DTMaterials.SCULK_ALLOY.getPlate())
				.energyCost(20)
				.processingTime(20 * 5)
				.save(consumer, save("processing/sculk_plate"));
	}
}