package dev.celestiacraft.deep_tech.compat.kubejs.recipe.schema;

import dev.celestiacraft.deep_tech.compat.kubejs.recipe.recipejs.AlloyRecipeJS;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public interface AlloySchema {
	RecipeKey<OutputItem> OUTPUT = ItemComponents.OUTPUT.key("result")
			.defaultOptional();
	RecipeKey<InputItem[]> INPUTS = ItemComponents.INPUT_ARRAY.key("inputs")
			.defaultOptional();
	RecipeKey<Integer> ENERGY_COST = NumberComponent.INT.key("energy_cost")
			.optional(100)
			.preferred("energyCost");
	RecipeKey<Integer> TIME = NumberComponent.INT.key("processing_time")
			.optional(100)
			.preferred("processingTime");

	RecipeSchema SCHEMA = new RecipeSchema(
			AlloyRecipeJS.class,
			AlloyRecipeJS::new,
			OUTPUT,
			INPUTS,
			ENERGY_COST,
			TIME
	).constructor();
}