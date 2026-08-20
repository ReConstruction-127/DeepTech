package dev.celestiacraft.deep_tech.datagen.recipes.type;

import dev.celestiacraft.deep_tech.common.register.DTMaterials;
import dev.celestiacraft.deep_tech.common.register.item.MaterialItems;
import dev.celestiacraft.deep_tech.datagen.recipes.DTRecipeProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Consumer;

public class FurnaceRecipeGen extends DTRecipeProvider {
	public FurnaceRecipeGen(PackOutput output) {
		super(output);
	}

	public static void register(Consumer<FinishedRecipe> consumer) {
		smelting(consumer);
		blasting(consumer);
		smoking(consumer);
		campfire(consumer);
	}

	private static void smelting(Consumer<FinishedRecipe> consumer) {
		SimpleCookingRecipeBuilder.smelting(
						Ingredient.of(DTMaterials.IRON.getDust().get()),
						RecipeCategory.MISC,
						Items.IRON_INGOT,
						0.7F,
						200
				)
				.unlockedBy("has_iron_dust", has(DTMaterials.IRON.getDust().get()))
				.save(consumer, save("smelting/iron_ingot_from_dust"));

		SimpleCookingRecipeBuilder.smelting(
						Ingredient.of(DTMaterials.COPPER.getDust().get()),
						RecipeCategory.MISC,
						Items.COPPER_INGOT,
						0.7F,
						200
				)
				.unlockedBy("has_copper_dust", has(DTMaterials.COPPER.getDust().get()))
				.save(consumer, save("smelting/copper_ingot_from_dust"));

		SimpleCookingRecipeBuilder.smelting(
						Ingredient.of(DTMaterials.GOLD.getDust().get()),
						RecipeCategory.MISC,
						Items.GOLD_INGOT,
						0.7F,
						200
				)
				.unlockedBy("has_gold_dust", has(DTMaterials.GOLD.getDust().get()))
				.save(consumer, save("smelting/gold_ingot_from_dust"));

	}

	private static void blasting(Consumer<FinishedRecipe> consumer) {
		SimpleCookingRecipeBuilder.blasting(
						Ingredient.of(DTMaterials.COPPER.getDust().get()),
						RecipeCategory.MISC,
						Items.COPPER_INGOT,
						0.7F,
						100
				)
				.unlockedBy("has_copper_dust", has(DTMaterials.COPPER.getDust().get()))
				.save(consumer, save("blasting/copper_ingot_from_dust"));

		SimpleCookingRecipeBuilder.blasting(
						Ingredient.of(DTMaterials.IRON.getDust().get()),
						RecipeCategory.MISC,
						Items.IRON_INGOT,
						0.7F,
						100
				)
				.unlockedBy("has_iron_dust", has(DTMaterials.IRON.getDust().get()))
				.save(consumer, save("blasting/iron_ingot_from_dust"));

		SimpleCookingRecipeBuilder.blasting(
						Ingredient.of(DTMaterials.GOLD.getDust().get()),
						RecipeCategory.MISC,
						Items.GOLD_INGOT,
						0.7F,
						100
				)
				.unlockedBy("has_gold_dust", has(DTMaterials.GOLD.getDust().get()))
				.save(consumer, save("blasting/gold_ingot_from_dust"));
	}

	private static void smoking(Consumer<FinishedRecipe> consumer) {
		SimpleCookingRecipeBuilder.smoking(
						Ingredient.of(MaterialItems.SCULK_BONEMEAL.get()),
						RecipeCategory.MISC,
						Items.BONE_MEAL,
						0.35F,
						100
				)
				.unlockedBy("has_sculk_bonemeal", has(MaterialItems.SCULK_BONEMEAL.get()))
				.save(consumer, save("smoking/bone_meal_from_sculk_bonemeal"));
	}

	private static void campfire(Consumer<FinishedRecipe> consumer) {
//		SimpleCookingRecipeBuilder.campfireCooking(
//						Ingredient.of(MaterialItems.SCULK_CHUNK.get()),
//						RecipeCategory.MISC,
//						MaterialItems.DENSE_SCULK_CHUNK.get(),
//						0.35F,
//						600
//				)
//				.unlockedBy("has_sculk_chunk", has(MaterialItems.SCULK_CHUNK.get()))
//				.save(consumer, save("campfire/dense_sculk_chunk_from_sculk_chunk"));
// 用作营火配方的例子，勿删，未来可能加入其他营火配方
	}
}