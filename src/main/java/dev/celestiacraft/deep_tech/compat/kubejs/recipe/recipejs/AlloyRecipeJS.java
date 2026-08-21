package dev.celestiacraft.deep_tech.compat.kubejs.recipe.recipejs;

import dev.celestiacraft.deep_tech.compat.kubejs.recipe.schema.AlloySchema;
import dev.celestiacraft.deep_tech.compat.kubejs.api.DTRecipeJS;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;

/**
 * 合金炉配方的链式构建器, 对应 {@code AlloyRecipeBuilder} 的 input/output.
 */
public class AlloyRecipeJS extends DTRecipeJS {
	public AlloyRecipeJS input(Object from) {
		addItem(AlloySchema.INPUTS, from);
		return this;
	}

	public AlloyRecipeJS output(Object from) {
		setValue(AlloySchema.OUTPUT, ItemComponents.OUTPUT.read(this, from));
		return this;
	}

	@Override
	public void serialize() {
		InputItem[] inputs = getValue(AlloySchema.INPUTS);
		if (inputs == null || inputs.length == 0) {
			throw new RecipeExceptionJS("Alloy recipe needs at least one item input!");
		}

		OutputItem output = getValue(AlloySchema.OUTPUT);
		if (output == null || output.isEmpty()) {
			throw new RecipeExceptionJS("Alloy recipe needs an item output!");
		}

		super.serialize();
	}
}