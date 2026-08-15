package dev.celestiacraft.deep_tech.common.register;

import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.common.recipe.alloy.AlloyRecipe;
import dev.celestiacraft.deep_tech.common.recipe.alloy.AlloySerializer;
import dev.celestiacraft.deep_tech.common.recipe.crushing.CrushingRecipe;
import dev.celestiacraft.deep_tech.common.recipe.crushing.CrushingRecipeSerializer;
import dev.celestiacraft.deep_tech.common.recipe.harvest.HarvestRecipe;
import dev.celestiacraft.deep_tech.common.recipe.harvest.HarvestRecipeSerializer;
import dev.celestiacraft.deep_tech.common.recipe.interaction.InteractionRecipe;
import dev.celestiacraft.deep_tech.common.recipe.interaction.InteractionRecipeSerializer;
import dev.celestiacraft.libs.api.register.recipe.RecipeEntry;

public class DTRecipes {
	public static final RecipeEntry<CrushingRecipe> CRUSHING;
	public static final RecipeEntry<AlloyRecipe> ALLOY;
	public static final RecipeEntry<InteractionRecipe> INTERACTION;
	public static final RecipeEntry<HarvestRecipe> HARVEST;

	static {
		CRUSHING = DeepTech.REGISTRATE.recipe("crushing", CrushingRecipeSerializer::new)
				.register();
		ALLOY = DeepTech.REGISTRATE.recipe("alloy", AlloySerializer::new)
				.register();
		INTERACTION = DeepTech.REGISTRATE.recipe("interaction", InteractionRecipeSerializer::new)
				.register();
		HARVEST = DeepTech.REGISTRATE.recipe("harvest", HarvestRecipeSerializer::new)
				.register();
	}

	public static void register() {
		DeepTech.registerLog("Recipe Type");
	}
}