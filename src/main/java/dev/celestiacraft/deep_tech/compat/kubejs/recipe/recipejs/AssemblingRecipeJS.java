package dev.celestiacraft.deep_tech.compat.kubejs.recipe.recipejs;

import dev.celestiacraft.deep_tech.compat.kubejs.recipe.schema.AssemblingSchema;
import dev.celestiacraft.deep_tech.compat.kubejs.api.DTRecipeJS;
import dev.latvian.mods.kubejs.fluid.InputFluid;
import dev.latvian.mods.kubejs.fluid.OutputFluid;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;

/**
 * 组装机配方的链式构建器, 对应 {@code AssemblingRecipeBuilder} 的 itemInput/fluidInput/
 * catalyst/itemOutput/fluidOutput.
 */
public class AssemblingRecipeJS extends DTRecipeJS {
	public AssemblingRecipeJS itemInput(InputItem input) {
		addItem(AssemblingSchema.ITEM_INPUTS, input);
		return this;
	}

	public AssemblingRecipeJS fluidInput(InputFluid input) {
		addFluidInput(AssemblingSchema.FLUID_INPUTS, input);
		return this;
	}

	public AssemblingRecipeJS itemOutput(OutputItem output) {
		addItemOutput(AssemblingSchema.ITEM_OUTPUTS, output);
		return this;
	}

	public AssemblingRecipeJS fluidOutput(OutputFluid output) {
		addFluidOutput(AssemblingSchema.FLUID_OUTPUTS, output);
		return this;
	}

	@Override
	public void serialize() {
		InputItem[] inputs = getValue(AssemblingSchema.ITEM_INPUTS);
		if (inputs == null || inputs.length == 0) {
			throw new RecipeExceptionJS("Assembling recipe needs at least one item input!");
		}

		OutputItem[] outputs = getValue(AssemblingSchema.ITEM_OUTPUTS);
		if (outputs == null || outputs.length == 0) {
			throw new RecipeExceptionJS("Assembling recipe needs at least one item output!");
		}

		super.serialize();
	}
}