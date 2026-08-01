package dev.celestiacraft.deep_tech.datagen.recipes.type;

import dev.celestiacraft.deep_tech.api.recipe.builder.alloy.AlloyRecipeBuilder;
import dev.celestiacraft.deep_tech.common.register.item.MaterialItems;
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
		addSculkAlloy(consumer);

		AlloyRecipeBuilder.builder()
				.input(Tags.Items.INGOTS_IRON, 2)
				.input(Tags.Items.INGOTS_GOLD, 3)
				.output(Items.COPPER_INGOT)
				.energyCost(100)
				.processingTime(20 * 3)
				.save(consumer, save("alloy/test"));
	}

	private static void addSculkAlloy(Consumer<FinishedRecipe> consumer) {
		AlloyRecipeBuilder.builder()
				.input(Tags.Items.INGOTS_COPPER)
				.input(MaterialItems.SCULK_CHUNK)
				.output(MaterialItems.SCULK_ALLOY)
				.energyCost(100)
				.processingTime(20 * 3)
				.save(consumer, save("alloy/sculk_alloy/copper"));

		AlloyRecipeBuilder.builder()
				.input(Tags.Items.INGOTS_IRON, 2)
				.input(MaterialItems.SCULK_CHUNK)
				.output(MaterialItems.SCULK_ALLOY, 2)
				.energyCost(100 << 1)
				.processingTime(20 * 3)
				.save(consumer, save("alloy/sculk_alloy/iron"));

		AlloyRecipeBuilder.builder()
				.input(Tags.Items.INGOTS_GOLD, 4)
				.input(MaterialItems.SCULK_CHUNK)
				.output(MaterialItems.SCULK_ALLOY, 4)
				.energyCost(100 << 2)
				.processingTime(20 * 3)
				.save(consumer, save("alloy/sculk_alloy/gold"));

		AlloyRecipeBuilder.builder()
				.input(Tags.Items.INGOTS_NETHERITE, 64)
				.input(MaterialItems.SCULK_CHUNK)
				.output(MaterialItems.SCULK_ALLOY, 64)
				.energyCost(100 << 6)
				.processingTime(20 * 3)
				.save(consumer, save("alloy/sculk_alloy/netherite"));
	}
}