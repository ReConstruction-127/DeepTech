package dev.celestiacraft.deep_tech.compat.jei.api;

import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.common.recipe.alloy.AlloyRecipe;
import dev.celestiacraft.deep_tech.common.recipe.crushing.CrushingRecipe;
import dev.celestiacraft.deep_tech.common.recipe.harvest.HarvestRecipe;
import dev.celestiacraft.deep_tech.common.recipe.interaction.InteractionRecipe;
import mezz.jei.api.recipe.RecipeType;

public class DTJeiRecipeType {
	public static final RecipeType<CrushingRecipe> CRUSHING;
	public static final RecipeType<AlloyRecipe> ALLOY;
	public static final RecipeType<InteractionRecipe> INTERACTION;
	public static final RecipeType<HarvestRecipe> HARVEST;

	static {
		CRUSHING = addJeiRecipeType("crushing", CrushingRecipe.class);
		ALLOY = addJeiRecipeType("alloy", AlloyRecipe.class);
		INTERACTION = addJeiRecipeType("interaction", InteractionRecipe.class);
		HARVEST = addJeiRecipeType("harvest", HarvestRecipe.class);
	}

	private static <T> RecipeType<T> addJeiRecipeType(String path, Class<? extends T> clazz) {
		return RecipeType.create(DeepTech.MODID, path, clazz);
	}
}