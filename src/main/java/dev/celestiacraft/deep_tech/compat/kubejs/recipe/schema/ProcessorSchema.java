package dev.celestiacraft.deep_tech.compat.kubejs.recipe.schema;

import dev.celestiacraft.deep_tech.compat.kubejs.recipe.recipejs.ProcessorRecipeJS;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

/**
 * 处理器配方: 所有键可选, 脚本可先写 {@code processing()} 再链式追加.
 * <p>
 * item_inputs / item_outputs 必填(运行时校验), 最多 2 物品输入 + 2 物品输出.
 */
public interface ProcessorSchema {
	RecipeKey<InputItem[]> ITEM_INPUTS = ItemComponents.INPUT_ARRAY.key("item_inputs")
			.defaultOptional();
	RecipeKey<OutputItem[]> ITEM_OUTPUTS = ItemComponents.OUTPUT_ARRAY.key("item_outputs")
			.defaultOptional();
	RecipeKey<Integer> ENERGY_COST = NumberComponent.INT.key("energy_cost")
			.optional(50)
			.preferred("energyCost");
	RecipeKey<Integer> TIME = NumberComponent.INT.key("processing_time")
			.optional(100)
			.preferred("processingTime");

	RecipeSchema SCHEMA = new RecipeSchema(
			ProcessorRecipeJS.class,
			ProcessorRecipeJS::new,
			ITEM_OUTPUTS,
			ITEM_INPUTS,
			ENERGY_COST,
			TIME
	);
}