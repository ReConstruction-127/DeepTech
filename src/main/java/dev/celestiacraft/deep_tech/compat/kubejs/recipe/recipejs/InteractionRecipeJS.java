package dev.celestiacraft.deep_tech.compat.kubejs.recipe.recipejs;

import dev.celestiacraft.deep_tech.compat.kubejs.api.DTRecipeJS;
import dev.celestiacraft.deep_tech.compat.kubejs.recipe.schema.InteractionSchema;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.component.BlockComponent;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentBuilderMap;

/**
 * 交互配方的链式构建器, 对应 {@code InteractionRecipeBuilder} 的 trigger/target/result/
 * extraEffect/consume. 交互类型用 {@code .interactionType(...)} 设置.
 */
public class InteractionRecipeJS extends DTRecipeJS {
	public InteractionRecipeJS trigger(Object from) {
		setValue(InteractionSchema.TRIGGER_ITEM, ItemComponents.INPUT.read(this, from));
		return this;
	}

	public InteractionRecipeJS target(Object from) {
		setValue(InteractionSchema.TARGET_BLOCK, BlockComponent.BLOCK.read(this, from));
		return this;
	}

	public InteractionRecipeJS result(Object from) {
		addItemOutput(InteractionSchema.RESULTS, from);
		return this;
	}

	public InteractionRecipeJS extraEffect(Object from) {
		setValue(InteractionSchema.EXTRA_EFFECT, InteractionSchema.EXTRA_EFFECT_COMPONENT.read(this, from));
		return this;
	}

	public InteractionRecipeJS consume(boolean consume) {
		setValue(InteractionSchema.CONSUME_TRIGGER, consume);
		return this;
	}

	@Override
	public void serialize() {
		if (getValue(InteractionSchema.TRIGGER_ITEM) == null) {
			throw new RecipeExceptionJS("Interaction recipe needs a trigger item!");
		}

		if (getValue(InteractionSchema.TARGET_BLOCK) == null) {
			throw new RecipeExceptionJS("Interaction recipe needs a target block!");
		}

		OutputItem[] results = getValue(InteractionSchema.RESULTS);
		RecipeComponentBuilderMap extraEffect = getValue(InteractionSchema.EXTRA_EFFECT);
		if ((results == null || results.length == 0) && extraEffect == null) {
			throw new RecipeExceptionJS("Interaction recipe needs at least one result or an extra effect!");
		}

		super.serialize();
	}
}