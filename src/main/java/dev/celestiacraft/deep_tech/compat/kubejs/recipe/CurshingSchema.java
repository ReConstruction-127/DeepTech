package dev.celestiacraft.deep_tech.compat.kubejs.recipe;

import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public interface CurshingSchema {
	RecipeKey<OutputItem> OUTPUT = ItemComponents.OUTPUT.key("result");
	RecipeKey<InputItem> INPUT = ItemComponents.INPUT.key("input");
	RecipeKey<Integer> ENERGY_COST = NumberComponent.INT.key("energy_cost").optional(100);
	RecipeKey<Integer> TIME = NumberComponent.INT.key("processing_time").optional(100);

	RecipeSchema SCHEMA = new RecipeSchema(OUTPUT, INPUT, ENERGY_COST, TIME);
}