package dev.celestiacraft.deep_tech.datagen.recipes.type;

import dev.celestiacraft.deep_tech.api.recipe.builder.processor.ProcessorRecipeBuilder;
import dev.celestiacraft.deep_tech.api.register.material.DTMaterial;
import dev.celestiacraft.deep_tech.common.register.DTItems;
import dev.celestiacraft.deep_tech.common.register.DTMaterials;
import dev.celestiacraft.deep_tech.common.register.item.MaterialItems;
import dev.celestiacraft.deep_tech.datagen.recipes.DTRecipeProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class ProcessorRecipeGen extends DTRecipeProvider {
	public ProcessorRecipeGen(PackOutput output) {
		super(output);
	}

	public static void register(Consumer<FinishedRecipe> consumer) {
		// 铁锭 -> 铁板 (铁板已在 DTMaterials.IRON 注册)
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
				.itemInput(MaterialItems.SCULK_ALLOY)
				.itemOutput(MaterialItems.SCULK_ALLOY_PLATE)
				.energyCost(20)
				.processingTime(20 * 5)
				.save(consumer, save("processing/sculk_plate"));
	}
}