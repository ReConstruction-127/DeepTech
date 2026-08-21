package dev.celestiacraft.deep_tech.compat.kubejs.recipe.recipejs;

import dev.celestiacraft.deep_tech.compat.kubejs.api.DTRecipeJS;
import dev.celestiacraft.deep_tech.compat.kubejs.recipe.schema.InteractionSchema;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.component.BlockComponent;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentBuilderMap;
import net.minecraft.world.level.block.Block;

import java.util.Map;

/**
 * 交互配方的链式构建器, 对应 {@code InteractionRecipeBuilder} 的 trigger/target/result/
 * extraEffect/consume. 交互类型用 {@code .interactionType(...)} 设置.
 */
public class InteractionRecipeJS extends DTRecipeJS {
	public InteractionRecipeJS trigger(InputItem input) {
		setValue(InteractionSchema.TRIGGER, ItemComponents.INPUT.read(this, input));
		return this;
	}

	public InteractionRecipeJS target(Block block) {
		setValue(InteractionSchema.TARGET, BlockComponent.BLOCK.read(this, block));
		return this;
	}

	public InteractionRecipeJS result(OutputItem output) {
		addItemOutput(InteractionSchema.RESULTS, output);
		return this;
	}

	public InteractionRecipeJS extraEffect(double chance, Block toBlock, OutputItem... drops) {
		Map<String, Object> map = drops.length == 0
				? Map.of("chance", chance, "to_block", toBlock)
				: Map.of("chance", chance, "to_block", toBlock, "drops", drops);

		setValue(InteractionSchema.EXTRA_EFFECT, InteractionSchema.EXTRA_EFFECT_COMPONENT.read(this, map));
		return this;
	}

	public InteractionRecipeJS consume(boolean consume) {
		setValue(InteractionSchema.CONSUME_TRIGGER, consume);
		return this;
	}

	@Override
	public void serialize() {
		if (getValue(InteractionSchema.TRIGGER) == null) {
			throw new RecipeExceptionJS("Interaction recipe needs a trigger item!");
		}

		if (getValue(InteractionSchema.TARGET) == null) {
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