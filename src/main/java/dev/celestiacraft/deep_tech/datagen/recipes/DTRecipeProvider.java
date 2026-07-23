package dev.celestiacraft.deep_tech.datagen.recipes;

import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.datagen.recipes.type.CrushingRecipeGen;
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
		return DeepTech.loadResource("recipes/" + path);
	}

	@Override
	protected void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
		crushing(consumer);
	}

	private void crushing(Consumer<FinishedRecipe> consumer) {
		CrushingRecipeGen.register(consumer);
	}
}