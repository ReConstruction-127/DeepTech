package dev.celestiacraft.deep_tech.compat.jei.api;

import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.common.recipe.crushing.CrushingRecipe;
import mezz.jei.api.recipe.RecipeType;

public class DTJeiRecipeType {
	public static final RecipeType<CrushingRecipe> CRUSHING;

	static {
		CRUSHING = addJeiRecipeType("crushing", CrushingRecipe.class);
	}

	private static <T> RecipeType<T> addJeiRecipeType(String path, Class<? extends T> clazz) {
		return RecipeType.create(DeepTech.MODID, path, clazz);
	}
}