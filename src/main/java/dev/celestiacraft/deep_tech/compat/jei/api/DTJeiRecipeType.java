package dev.celestiacraft.deep_tech.compat.jei.api;

import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.common.recipe.alloy.AlloyRecipe;
import dev.celestiacraft.deep_tech.common.recipe.assembling.AssemblingRecipe;
import dev.celestiacraft.deep_tech.common.recipe.crushing.CrushingRecipe;
import dev.celestiacraft.deep_tech.common.recipe.cultivation.CultivationRecipe;
import dev.celestiacraft.deep_tech.common.recipe.harvest.HarvestRecipe;
import dev.celestiacraft.deep_tech.common.recipe.interaction.InteractionRecipe;
import dev.celestiacraft.deep_tech.common.recipe.processor.ProcessorRecipe;
import mezz.jei.api.recipe.RecipeType;

public class DTJeiRecipeType {
	public static final RecipeType<CrushingRecipe> CRUSHING;
	public static final RecipeType<AlloyRecipe> ALLOY;
	public static final RecipeType<InteractionRecipe> INTERACTION;
	public static final RecipeType<HarvestRecipe> HARVEST;
	public static final RecipeType<CultivationRecipe> CULTIVATION;
	public static final RecipeType<ProcessorRecipe> PROCESSING;
	public static final RecipeType<AssemblingRecipe> ASSEMBLING;

	static {
		CRUSHING = addJeiRecipeType("crushing", CrushingRecipe.class);
		ALLOY = addJeiRecipeType("alloy", AlloyRecipe.class);
		INTERACTION = addJeiRecipeType("interaction", InteractionRecipe.class);
		HARVEST = addJeiRecipeType("harvest", HarvestRecipe.class);
		CULTIVATION = addJeiRecipeType("cultivation", CultivationRecipe.class);
		PROCESSING = addJeiRecipeType("processing", ProcessorRecipe.class);
		ASSEMBLING = addJeiRecipeType("assembling", AssemblingRecipe.class);
	}

	private static <T> RecipeType<T> addJeiRecipeType(String path, Class<? extends T> clazz) {
		return RecipeType.create(DeepTech.MODID, path, clazz);
	}
}