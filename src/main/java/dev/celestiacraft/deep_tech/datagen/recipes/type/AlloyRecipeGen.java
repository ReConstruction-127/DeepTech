package dev.celestiacraft.deep_tech.datagen.recipes.type;

import dev.celestiacraft.deep_tech.api.recipe.builder.alloy.AlloyRecipeBuilder;
import dev.celestiacraft.deep_tech.datagen.recipes.DTRecipeProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class AlloyRecipeGen extends DTRecipeProvider {
	public AlloyRecipeGen(PackOutput output) {
		super(output);
	}

	public static void register(Consumer<FinishedRecipe> consumer) {
		AlloyRecipeBuilder.builder()
				.input(Tags.Items.INGOTS_IRON, 2)
				.input(Tags.Items.INGOTS_GOLD, 3)
				.output(Items.COPPER_INGOT)
				.energyCost(100)
				.processingTime(20 * 3)
				.save(consumer, save("alloy/test"));
	}
}