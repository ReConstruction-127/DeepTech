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

public interface HarvestSchema {
	RecipeComponent<RecipeComponentBuilderMap> INPUT_COMPONENT = RecipeComponent.builder()
			.add(BlockComponent.BLOCK.key("block").defaultOptional())
			.add(TagKeyComponent.BLOCK.key("block_tag").defaultOptional());

	RecipeKey<RecipeComponentBuilderMap> INPUT = INPUT_COMPONENT.key("input")
			.defaultOptional()
			.preferred("harvestInput");
	RecipeKey<OutputItem[]> RESULTS = ItemComponents.OUTPUT_ARRAY.key("results")
			.defaultOptional();

	RecipeSchema SCHEMA = new RecipeSchema(
			HarvestRecipeJS.class,
			HarvestRecipeJS::new,
			INPUT,
			RESULTS
	).constructor();
}