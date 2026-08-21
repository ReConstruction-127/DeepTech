package dev.celestiacraft.deep_tech.compat.kubejs.recipe.schema;

import dev.celestiacraft.deep_tech.compat.kubejs.recipe.recipejs.CrushingRecipeJS;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

/**
 * 粉碎机配方: 所有键可选, 脚本可先写 {@code crushing()} 再链式追加 input/output.
 * <p>
 * input / result 运行时校验.
 */
public interface CurshingSchema {
	RecipeKey<OutputItem> OUTPUT = ItemComponents.OUTPUT.key("result")
			.defaultOptional();
	RecipeKey<InputItem> INPUT = ItemComponents.INPUT.key("input")
			.defaultOptional();
	RecipeKey<Integer> ENERGY_COST = NumberComponent.INT.key("energy_cost")
			.optional(100)
			.preferred("energyCost");
	RecipeKey<Integer> TIME = NumberComponent.INT.key("processing_time")
			.optional(100)
			.preferred("processingTime");

	RecipeSchema SCHEMA = new RecipeSchema(
			CrushingRecipeJS.class,
			CrushingRecipeJS::new,
			OUTPUT,
			INPUT,
			ENERGY_COST,
			TIME
	);
}