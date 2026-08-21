package dev.celestiacraft.deep_tech.compat.kubejs.recipe.recipejs;

import dev.celestiacraft.deep_tech.compat.kubejs.recipe.schema.CurshingSchema;
import dev.celestiacraft.deep_tech.compat.kubejs.api.DTRecipeJS;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;

/**
 * 粉碎机配方的链式构建器, 对应 {@code CrushingRecipeBuilder} 的 input/output.
 */
public class CrushingRecipeJS extends DTRecipeJS {
	public CrushingRecipeJS input(Object from) {
		setValue(CurshingSchema.INPUT, ItemComponents.INPUT.read(this, from));
		return this;
	}

	public CrushingRecipeJS output(Object from) {
		setValue(CurshingSchema.OUTPUT, ItemComponents.OUTPUT.read(this, from));
		return this;
	}

	@Override
	public void serialize() {
		InputItem input = getValue(CurshingSchema.INPUT);
		if (input == null || input.isEmpty()) {
			throw new RecipeExceptionJS("Crushing recipe needs an item input!");
		}

		OutputItem output = getValue(CurshingSchema.OUTPUT);
		if (output == null || output.isEmpty()) {
			throw new RecipeExceptionJS("Crushing recipe needs an item output!");
		}

		super.serialize();
	}
}