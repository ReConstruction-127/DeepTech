package dev.celestiacraft.deep_tech.compat.kubejs.recipe.recipejs;

import dev.celestiacraft.deep_tech.compat.kubejs.api.DTRecipeJS;
import dev.celestiacraft.deep_tech.compat.kubejs.recipe.schema.ProcessorSchema;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;

/**
 * 处理器配方的链式构建器, 对应 {@code ProcessorRecipeBuilder} 的 itemInput/itemOutput.
 */
public class ProcessorRecipeJS extends DTRecipeJS {
	public ProcessorRecipeJS itemInput(Object from) {
		addItem(ProcessorSchema.ITEM_INPUTS, from);
		return this;
	}

	public ProcessorRecipeJS itemOutput(Object from) {
		addItemOutput(ProcessorSchema.ITEM_OUTPUTS, from);
		return this;
	}

	@Override
	public void serialize() {
		InputItem[] inputs = getValue(ProcessorSchema.ITEM_INPUTS);
		if (inputs == null || inputs.length == 0) {
			throw new RecipeExceptionJS("Processor recipe needs at least one item input!");
		}

		OutputItem[] outputs = getValue(ProcessorSchema.ITEM_OUTPUTS);
		if (outputs == null || outputs.length == 0) {
			throw new RecipeExceptionJS("Processor recipe needs at least one item output!");
		}

		super.serialize();
	}
}