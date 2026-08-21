package dev.celestiacraft.deep_tech.compat.kubejs.recipe.schema;

import dev.celestiacraft.deep_tech.compat.kubejs.recipe.recipejs.HarvestRecipeJS;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.BlockComponent;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentBuilderMap;
import dev.latvian.mods.kubejs.recipe.component.TagKeyComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

/**
 * 收割机配方: 所有键可选, 脚本可先写 {@code harvest()} 再链式追加.
 * <p>
 * input 形如 {"block": "..."} 或 {"block_tag": "..."}, results 为带概率输出数组
 * [{item, count, chance}]; input 与至少一个 result 运行时校验.
 */
public interface HarvestSchema {
	RecipeComponent<RecipeComponentBuilderMap> INPUT_COMPONENT = RecipeComponent.builder()
			.add(BlockComponent.BLOCK.key("block").defaultOptional())
			.add(TagKeyComponent.BLOCK.key("block_tag").defaultOptional());

	RecipeKey<RecipeComponentBuilderMap> INPUT = INPUT_COMPONENT.key("input")
			.defaultOptional();
	RecipeKey<OutputItem[]> RESULTS = ItemComponents.OUTPUT_ARRAY.key("results")
			.defaultOptional();

	RecipeSchema SCHEMA = new RecipeSchema(
			HarvestRecipeJS.class,
			HarvestRecipeJS::new,
			INPUT,
			RESULTS
	);
}