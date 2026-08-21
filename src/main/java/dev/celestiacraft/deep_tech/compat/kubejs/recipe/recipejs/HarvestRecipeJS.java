package dev.celestiacraft.deep_tech.compat.kubejs.recipe.recipejs;

import dev.celestiacraft.deep_tech.compat.kubejs.api.DTRecipeJS;
import dev.celestiacraft.deep_tech.compat.kubejs.recipe.schema.HarvestSchema;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentBuilderMap;
import net.minecraft.world.level.block.Block;

import java.util.Map;

/**
 * 收割机配方的链式构建器, 对应 {@code HarvestRecipeBuilder} 的 input/result.
 * <p>
 * input 接受方块 id、{@code #方块标签} 或 {"block": ...} / {"block_tag": ...} 对象.
 */
public class HarvestRecipeJS extends DTRecipeJS {
	public HarvestRecipeJS input(Object from) {
		if (from instanceof CharSequence s && !s.toString().isEmpty() && s.toString().charAt(0) == '#') {
			setValue(HarvestSchema.INPUT, HarvestSchema.INPUT_COMPONENT.read(this, Map.of("block_tag", s.toString())));
		} else if (from instanceof CharSequence || from instanceof Block) {
			setValue(HarvestSchema.INPUT, HarvestSchema.INPUT_COMPONENT.read(this, Map.of("block", from)));
		} else {
			setValue(HarvestSchema.INPUT, HarvestSchema.INPUT_COMPONENT.read(this, from));
		}

		return this;
	}

	public HarvestRecipeJS result(Object from) {
		addItemOutput(HarvestSchema.RESULTS, from);
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