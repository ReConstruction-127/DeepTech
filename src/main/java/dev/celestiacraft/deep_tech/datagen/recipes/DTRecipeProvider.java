package dev.celestiacraft.deep_tech.datagen.recipes;

import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.datagen.recipes.type.AlloyRecipeGen;
import dev.celestiacraft.deep_tech.datagen.recipes.type.CraftingRecipeGen;
import dev.celestiacraft.deep_tech.datagen.recipes.type.CrushingRecipeGen;
import dev.celestiacraft.deep_tech.datagen.recipes.type.InteractionRecipeGen;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class DTRecipeProvider extends RecipeProvider {
	public DTRecipeProvider(PackOutput output) {
		super(output);
	}

	protected static ResourceLocation save(String path) {
		return DeepTech.loadResource(path);
	}

	@Override
	protected void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
		crushing(consumer);
		alloy(consumer);
		interaction(consumer);
		shaped(consumer);
	}

	private void crushing(Consumer<FinishedRecipe> consumer) {
		CrushingRecipeGen.register(consumer);
	}

	private void alloy(Consumer<FinishedRecipe> consumer) {
		AlloyRecipeGen.register(consumer);
	}

	private void interaction(Consumer<FinishedRecipe> consumer) {
		InteractionRecipeGen.register(consumer);
	}

	private void shaped(Consumer<FinishedRecipe> consumer) {
		CraftingRecipeGen.register(consumer);
	}
}