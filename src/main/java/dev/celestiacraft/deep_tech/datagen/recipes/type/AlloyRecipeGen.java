package dev.celestiacraft.deep_tech.datagen.recipes.type;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.data.recipe.CommonMetal;
import dev.celestiacraft.deep_tech.api.recipe.builder.alloy.AlloyRecipeBuilder;
import dev.celestiacraft.deep_tech.common.register.DTMaterials;
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

	public static void addRecipes(Consumer<FinishedRecipe> consumer) {
		addSculkAlloy(consumer);
		addCompatRecipe(consumer);

		AlloyRecipeBuilder.builder()
				.input(Tags.Items.GEMS_AMETHYST, 1)
				.input(MaterialItems.SCULK_CHUNK)
				.output(Items.ECHO_SHARD, 1)
				.energyCost(100)
				.processingTime(20 * 20)
				.save(consumer, save("alloy/echo_shard"));
	}

	private static void addSculkAlloy(Consumer<FinishedRecipe> consumer) {
		AlloyRecipeBuilder.builder()
				.input(Tags.Items.INGOTS_COPPER)
				.input(MaterialItems.SCULK_CHUNK)
				.output(DTMaterials.SCULK_ALLOY.getIngot())
				.energyCost(100)
				.processingTime(20 * 20)
				.save(consumer, save("alloy/sculk_alloy/copper"));

		AlloyRecipeBuilder.builder()
				.input(Tags.Items.INGOTS_IRON)
				.input(MaterialItems.SCULK_CHUNK)
				.output(DTMaterials.SCULK_STEEL.getIngot())
				.energyCost(100)
				.processingTime(20 * 20)
				.save(consumer, save("alloy/sculk_alloy/iron"));
	}

	private static void addCompatRecipe(Consumer<FinishedRecipe> consumer) {
		addModCompatRecipe(Create.ID, "andestie_alloy_1", consumer, (recipe) -> {
			AlloyRecipeBuilder.builder()
					.input(Tags.Items.NUGGETS_IRON)
					.input(Items.ANDESITE)
					.output(AllItems.ANDESITE_ALLOY, 1)
					.energyCost(100)
					.processingTime(20 * 10)
					.save(recipe, save("compat/create/andestie_alloy_1"));
		});

		addModCompatRecipe(Create.ID, "andestie_alloy_2", consumer, (recipe) -> {
			AlloyRecipeBuilder.builder()
					.input(CommonMetal.ZINC.nuggets)
					.input(Items.ANDESITE)
					.output(AllItems.ANDESITE_ALLOY, 1)
					.energyCost(100)
					.processingTime(20 * 10)
					.save(recipe, save("compat/create/andestie_alloy_2"));
		});

		addModCompatRecipe(Create.ID, "andestie_alloy_3", consumer, (recipe) -> {
			AlloyRecipeBuilder.builder()
					.input(Tags.Items.INGOTS_IRON)
					.input(Items.ANDESITE, 9)
					.output(AllBlocks.ANDESITE_ALLOY_BLOCK)
					.energyCost(100 * 9)
					.processingTime(20 * (10 * 9))
					.save(recipe, save("compat/create/andestie_alloy_3"));
		});

		addModCompatRecipe(Create.ID, "andestie_alloy_4", consumer, (recipe) -> {
			AlloyRecipeBuilder.builder()
					.input(CommonMetal.ZINC.ingots)
					.input(Items.ANDESITE, 9)
					.output(AllBlocks.ANDESITE_ALLOY_BLOCK)
					.energyCost(100 * 9)
					.processingTime(20 * (10 * 9))
					.save(recipe, save("compat/create/andestie_alloy_4"));
		});
	}
}