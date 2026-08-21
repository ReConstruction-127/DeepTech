package dev.celestiacraft.deep_tech.datagen.recipes;

import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.datagen.recipes.type.AlloyRecipeGen;
import dev.celestiacraft.deep_tech.datagen.recipes.type.AssemblingRecipeGen;
import dev.celestiacraft.deep_tech.datagen.recipes.type.CraftingRecipeGen;
import dev.celestiacraft.deep_tech.datagen.recipes.type.CrushingRecipeGen;
import dev.celestiacraft.deep_tech.datagen.recipes.type.CultivationRecipeGen;
import dev.celestiacraft.deep_tech.datagen.recipes.type.FurnaceRecipeGen;
import dev.celestiacraft.deep_tech.datagen.recipes.type.HarvestRecipeGen;
import dev.celestiacraft.deep_tech.datagen.recipes.type.InteractionRecipeGen;
import dev.celestiacraft.deep_tech.datagen.recipes.type.ProcessorRecipeGen;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class DTRecipeProvider extends RecipeProvider {
	public DTRecipeProvider(PackOutput output) {
		super(output);
	}

	protected static ResourceLocation save(String path) {
		return DeepTech.loadResource(path);
	}

	protected static void addModCompatRecipe(
			String modId,
			String name,
			Consumer<FinishedRecipe> consumer,
			Consumer<Consumer<FinishedRecipe>> recipe
	) {
		String path = String.format("compat/%s/%s", modId, name);
		ConditionalRecipe.builder()
				.addCondition(new ModLoadedCondition(modId))
				.addRecipe(recipe)
				.build(consumer, save(path));
	}

	@Override
	protected void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
		crushing(consumer);
		alloy(consumer);
		cultivation(consumer);
		processing(consumer);
		assembling(consumer);
		interaction(consumer);
		harvest(consumer);
		shaped(consumer);
		furnace(consumer);
	}

	private void furnace(Consumer<FinishedRecipe> consumer) {
		FurnaceRecipeGen.addRecipes(consumer);
	}

	private void cultivation(Consumer<FinishedRecipe> consumer) {
		CultivationRecipeGen.addRecipes(consumer);
	}

	private void harvest(Consumer<FinishedRecipe> consumer) {
		HarvestRecipeGen.addRecipes(consumer);
	}

	private void crushing(Consumer<FinishedRecipe> consumer) {
		CrushingRecipeGen.addRecipes(consumer);
	}

	private void alloy(Consumer<FinishedRecipe> consumer) {
		AlloyRecipeGen.addRecipes(consumer);
	}

	private void processing(Consumer<FinishedRecipe> consumer) {
		ProcessorRecipeGen.addRecipes(consumer);
	}

	private void assembling(Consumer<FinishedRecipe> consumer) {
		AssemblingRecipeGen.addRecipes(consumer);
	}

	private void interaction(Consumer<FinishedRecipe> consumer) {
		InteractionRecipeGen.addRecipes(consumer);
	}

	private void shaped(Consumer<FinishedRecipe> consumer) {
		CraftingRecipeGen.addRecipes(consumer);
	}
}