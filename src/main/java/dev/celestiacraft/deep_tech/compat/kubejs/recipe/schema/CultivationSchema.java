package dev.celestiacraft.deep_tech.compat.kubejs.recipe.schema;

import dev.celestiacraft.deep_tech.compat.kubejs.recipe.recipejs.CultivationRecipeJS;
import dev.latvian.mods.kubejs.fluid.InputFluid;
import dev.latvian.mods.kubejs.fluid.OutputFluid;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.FluidComponents;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

public interface CultivationSchema {
	RecipeKey<OutputItem[]> ITEM_OUTPUTS = ItemComponents.OUTPUT_ARRAY.key("item_outputs")
			.defaultOptional();
	RecipeKey<InputItem[]> ITEM_INPUTS = ItemComponents.INPUT_ARRAY.key("item_inputs")
			.defaultOptional();
	RecipeKey<InputFluid[]> FLUID_INPUTS = FluidComponents.INPUT_ARRAY.key("fluid_inputs")
			.optional(new InputFluid[0])
			.preferred("fluidInputs");
	RecipeKey<OutputFluid[]> FLUID_OUTPUTS = FluidComponents.OUTPUT_ARRAY.key("fluid_outputs")
			.optional(new OutputFluid[0])
			.preferred("fluidOutputs");
	RecipeKey<Float> ITEM_OUTPUT_CHANCE = NumberComponent.FLOAT.key("item_output_chance")
			.optional(1.0F)
			.preferred("itemOutputChance");
	RecipeKey<Integer> ENERGY_COST = NumberComponent.INT.key("energy_cost")
			.optional(50)
			.preferred("energyCost");
	RecipeKey<Integer> TIME = NumberComponent.INT.key("processing_time")
			.optional(100)
			.preferred("processingTime");

	RecipeSchema SCHEMA = new RecipeSchema(
			CultivationRecipeJS.class,
			CultivationRecipeJS::new,
			ITEM_OUTPUTS,
			FLUID_OUTPUTS,
			ITEM_INPUTS,
			FLUID_INPUTS,
			ITEM_OUTPUT_CHANCE,
			ENERGY_COST, TIME
	).constructor();
}