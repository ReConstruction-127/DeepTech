package dev.celestiacraft.deep_tech.compat.kubejs.recipe.recipejs;

import dev.celestiacraft.deep_tech.common.recipe.harvest.HarvestInput;
import dev.celestiacraft.deep_tech.compat.kubejs.api.DTRecipeJS;
import dev.celestiacraft.deep_tech.compat.kubejs.recipe.schema.HarvestSchema;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentBuilderMap;

import java.util.Map;

/**
 * 收割机配方的链式构建器, 对应 {@code HarvestRecipeBuilder} 的 input/result.
 * <p>
 * input 直接使用配方运行时类型 {@link HarvestInput}: 方块或方块标签.
 */
public class HarvestRecipeJS extends DTRecipeJS {
	public HarvestRecipeJS input(HarvestInput input) {
		if (input == null || input.isEmpty()) {
			throw new RecipeExceptionJS("Harvest input can't be empty!");
		}

		if (input.getBlock() != null) {
			setValue(HarvestSchema.INPUT, HarvestSchema.INPUT_COMPONENT.read(
					this,
					Map.of("block", input.getBlock())
			));
		} else {
			setValue(HarvestSchema.INPUT, HarvestSchema.INPUT_COMPONENT.read(
					this,
					Map.of("block_tag", input.getBlockTag())
			));
		}

		return this;
	}

	public HarvestRecipeJS result(OutputItem output) {
		addItemOutput(HarvestSchema.RESULTS, output);
		return this;
	}

	@Override
	public void serialize() {
		RecipeComponentBuilderMap input = getValue(HarvestSchema.INPUT);
		if (input == null) {
			throw new RecipeExceptionJS("Harvest recipe needs an input block or block tag!");
		}

		OutputItem[] results = getValue(HarvestSchema.RESULTS);
		if (results == null || results.length == 0) {
			throw new RecipeExceptionJS("Harvest recipe needs at least one result!");
		}

		super.serialize();
	}
}