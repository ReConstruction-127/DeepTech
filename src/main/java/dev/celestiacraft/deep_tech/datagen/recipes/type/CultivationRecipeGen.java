package dev.celestiacraft.deep_tech.datagen.recipes.type;

import dev.celestiacraft.deep_tech.api.recipe.builder.cultivation.CultivationRecipeBuilder;
import dev.celestiacraft.deep_tech.common.register.DTFluids;
import dev.celestiacraft.deep_tech.common.register.item.MaterialItems;
import dev.celestiacraft.deep_tech.datagen.recipes.DTRecipeProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.level.material.Fluids;

import java.util.function.Consumer;

public class CultivationRecipeGen extends DTRecipeProvider {
	public CultivationRecipeGen(PackOutput output) {
		super(output);
	}

	public static void register(Consumer<FinishedRecipe> consumer) {
		// 幽匿碎块 + 幽匿培养液 + 液体经验 -> 致密幽匿碎块 + 水
		CultivationRecipeBuilder.builder()
				.itemInput(MaterialItems.SCULK_CHUNK)
				.fluidInput(DTFluids.SCULK_CULTURE.getSource(), 10)
				.fluidInput(DTFluids.SCULK_INDUCTION_FLUID.getSource(), 10)
				.itemOutput(MaterialItems.DENSE_SCULK_CHUNK)
				.fluidOutput(Fluids.WATER, 10)
				.energyCost(20)
				.processingTime(100)
				.itemOutputChance(1)
				.save(consumer, save("cultivation/dense_sculk_chunk"));


		CultivationRecipeBuilder.builder()
				.itemInput(MaterialItems.ALKALOID_POWDER)
				.fluidInput(Fluids.WATER.getSource(), 100)
				.fluidOutput(DTFluids.SCULK_INDUCTION_FLUID.getSource(), 50)
				.energyCost(20)
				.processingTime(100)
				.itemOutputChance(1)
				.save(consumer, save("cultivation/induction_fluid"));


		CultivationRecipeBuilder.builder()
				.itemInput(MaterialItems.SCULK_CHUNK)
				.fluidInput(Fluids.WATER.getSource(), 100)
				.fluidInput(DTFluids.LIQUID_EXPERIENCE.getSource(), 10)
				.fluidOutput(DTFluids.SCULK_CULTURE.getSource(), 20)
				.energyCost(20)
				.processingTime(100)
				.itemOutputChance(1)
				.save(consumer, save("cultivation/sculk_culture"));


		CultivationRecipeBuilder.builder()
				.itemInput(MaterialItems.SCULK_CHUNK)
				.fluidInput(DTFluids.SCULK_CULTURE.getSource(), 5)
				.fluidOutput(Fluids.WATER.getSource(), 10)
				.itemOutput(MaterialItems.SCULK_CHUNK, 2)
				.energyCost(20)
				.processingTime(100)
				.itemOutputChance(100)
				.save(consumer, save("cultivation/sculk_copy"));


		CultivationRecipeBuilder.builder()
				.itemInput(MaterialItems.DENSE_SCULK_CHUNK)
				.fluidInput(DTFluids.SCULK_CULTURE.getSource(), 10)
				.fluidOutput(Fluids.WATER.getSource(), 20)
				.itemOutput(MaterialItems.DENSE_SCULK_CHUNK, 2)
				.energyCost(20)
				.processingTime(200)
				.itemOutputChance(100)
				.save(consumer, save("cultivation/dense_sculk_copy"));
	}
}