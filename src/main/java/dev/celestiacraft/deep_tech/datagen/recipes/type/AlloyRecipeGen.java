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
	}

	private static void addSculkAlloy(Consumer<FinishedRecipe> consumer) {
		AlloyRecipeBuilder.builder()
				.input(Tags.Items.INGOTS_COPPER)
				.input(MaterialItems.SCULK_CHUNK)
				.output(MaterialItems.SCULK_ALLOY)
				.energyCost(100)
				.processingTime(20 * 20)
				.save(consumer, save("alloy/sculk_alloy/copper"));

		AlloyRecipeBuilder.builder()
				.input(Tags.Items.GEMS_AMETHYST, 1)
				.input(MaterialItems.SCULK_CHUNK)
				.output(Items.ECHO_SHARD, 1)
				.energyCost(100)
				.processingTime(20 * 20)
				.save(consumer, save("alloy/sculk_alloy/echo_shard"));
	}
}