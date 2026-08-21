package dev.celestiacraft.deep_tech.compat.kubejs.recipe.schema;

import dev.celestiacraft.deep_tech.compat.kubejs.recipe.recipejs.AssemblingRecipeJS;
import dev.latvian.mods.kubejs.fluid.InputFluid;
import dev.latvian.mods.kubejs.fluid.OutputFluid;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.FluidComponents;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

/**
 * 组装机配方: 所有键可选, 脚本可先写 {@code assembling()} 再链式追加.
 * <p>
 * item_inputs / item_outputs 必填(运行时校验), fluid_inputs / fluid_outputs / catalyst 可选,
 * 最多 16 物品输入 + 4 物品输出, 2 流体输入 + 1 流体输出.
 */
public interface AssemblingSchema {
	RecipeKey<InputItem[]> ITEM_INPUTS = ItemComponents.INPUT_ARRAY.key("item_inputs")
			.defaultOptional();
	RecipeKey<OutputItem[]> ITEM_OUTPUTS = ItemComponents.OUTPUT_ARRAY.key("item_outputs")
			.defaultOptional();
	RecipeKey<InputFluid[]> FLUID_INPUTS = FluidComponents.INPUT_ARRAY.key("fluid_inputs")
			.optional(new InputFluid[0]).preferred("fluidInputs");
	RecipeKey<OutputFluid[]> FLUID_OUTPUTS = FluidComponents.OUTPUT_ARRAY.key("fluid_outputs")
			.optional(new OutputFluid[0]).preferred("fluidOutputs");
	RecipeKey<InputItem> CATALYST = ItemComponents.INPUT.key("catalyst")
			.defaultOptional();
	RecipeKey<Integer> ENERGY_COST = NumberComponent.INT.key("energy_cost")
			.optional(50)
			.preferred("energyCost");
	RecipeKey<Integer> TIME = NumberComponent.INT.key("processing_time")
			.optional(100)
			.preferred("processingTime");

	RecipeSchema SCHEMA = new RecipeSchema(
			AssemblingRecipeJS.class,
			AssemblingRecipeJS::new,
			ITEM_OUTPUTS,
			FLUID_OUTPUTS,
			ITEM_INPUTS,
			FLUID_INPUTS,
			CATALYST,
			ENERGY_COST,
			TIME
	);
}