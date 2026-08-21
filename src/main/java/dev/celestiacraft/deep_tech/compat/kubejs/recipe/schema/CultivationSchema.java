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

/**
 * 幽匿培育室配方: 所有键可选, 脚本可先写 {@code cultivation()} 再链式追加.
 * <p>
 * 至少一个输入(物品或流体)与一个输出(物品或流体, 运行时校验);
 * 最多 2 物品/流体输入 + 4 物品/2 流体输出.
 */
public interface CultivationSchema {
	RecipeKey<InputItem[]> ITEM_INPUTS = ItemComponents.INPUT_ARRAY.key("item_inputs")
			.defaultOptional();
	RecipeKey<OutputItem[]> ITEM_OUTPUTS = ItemComponents.OUTPUT_ARRAY.key("item_outputs")
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
	);
}