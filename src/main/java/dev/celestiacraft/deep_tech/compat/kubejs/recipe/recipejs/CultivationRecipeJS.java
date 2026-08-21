package dev.celestiacraft.deep_tech.compat.kubejs.recipe.recipejs;

import dev.celestiacraft.deep_tech.compat.kubejs.recipe.schema.CultivationSchema;
import dev.celestiacraft.deep_tech.compat.kubejs.api.DTRecipeJS;
import dev.latvian.mods.kubejs.fluid.InputFluid;
import dev.latvian.mods.kubejs.fluid.OutputFluid;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;

/**
 * 幽匿培育室配方的链式构建器, 对应 {@code CultivationRecipeBuilder} 的 itemInput/fluidInput/
 * itemOutput/fluidOutput.
 */
public class CultivationRecipeJS extends DTRecipeJS {
	public CultivationRecipeJS itemInput(Object from) {
		addItem(CultivationSchema.ITEM_INPUTS, from);
		return this;
	}

	public CultivationRecipeJS fluidInput(Object from) {
		addFluidInput(CultivationSchema.FLUID_INPUTS, from);
		return this;
	}

	public CultivationRecipeJS itemOutput(Object from) {
		addItemOutput(CultivationSchema.ITEM_OUTPUTS, from);
		return this;
	}

	public CultivationRecipeJS fluidOutput(Object from) {
		addFluidOutput(CultivationSchema.FLUID_OUTPUTS, from);
		return this;
	}

	@Override
	public void serialize() {
		InputItem[] itemInputs = getValue(CultivationSchema.ITEM_INPUTS);
		InputFluid[] fluidInputs = getValue(CultivationSchema.FLUID_INPUTS);
		boolean hasInput = (itemInputs != null && itemInputs.length > 0) || (fluidInputs != null && fluidInputs.length > 0);
		if (!hasInput) {
			throw new RecipeExceptionJS("Cultivation recipe needs at least one item or fluid input!");
		}

		OutputItem[] itemOutputs = getValue(CultivationSchema.ITEM_OUTPUTS);
		OutputFluid[] fluidOutputs = getValue(CultivationSchema.FLUID_OUTPUTS);
		boolean hasOutput = (itemOutputs != null && itemOutputs.length > 0) || (fluidOutputs != null && fluidOutputs.length > 0);
		if (!hasOutput) {
			throw new RecipeExceptionJS("Cultivation recipe needs at least one item or fluid output!");
		}

		super.serialize();
	}
}