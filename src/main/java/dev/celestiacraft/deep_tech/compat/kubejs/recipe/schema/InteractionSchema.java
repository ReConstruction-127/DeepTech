package dev.celestiacraft.deep_tech.compat.kubejs.recipe.schema;

import dev.celestiacraft.deep_tech.common.recipe.interaction.InteractionType;
import dev.celestiacraft.deep_tech.compat.kubejs.recipe.recipejs.InteractionRecipeJS;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.BlockComponent;
import dev.latvian.mods.kubejs.recipe.component.BooleanComponent;
import dev.latvian.mods.kubejs.recipe.component.EnumComponent;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentBuilderMap;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import net.minecraft.world.level.block.Block;

/**
 * 交互配方: 所有键可选, 脚本可先写 {@code interaction()} 再链式追加.
 * <p>
 * trigger_item / target_block 与 results 或 extra_effect 运行时校验;
 * extra_effect 形如 {"chance": 0.5, "to_block": "minecraft:stone", "drops": [...]}.
 */
public interface InteractionSchema {
	RecipeComponent<RecipeComponentBuilderMap> EXTRA_EFFECT_COMPONENT = RecipeComponent.builder()
			.add(NumberComponent.DOUBLE.key("chance").defaultOptional())
			.add(BlockComponent.BLOCK.key("to_block"))
			.add(ItemComponents.OUTPUT_ARRAY.key("drops").optional(new OutputItem[0]));

	RecipeKey<InputItem> TRIGGER_ITEM = ItemComponents.INPUT.key("trigger_item")
			.defaultOptional();
	RecipeKey<Block> TARGET_BLOCK = BlockComponent.BLOCK.key("target_block")
			.defaultOptional();
	RecipeKey<OutputItem[]> RESULTS = ItemComponents.OUTPUT_ARRAY.key("results")
			.defaultOptional();
	RecipeKey<RecipeComponentBuilderMap> EXTRA_EFFECT = EXTRA_EFFECT_COMPONENT.key("extra_effect")
			.defaultOptional()
			.preferred("extraEffect");
	RecipeKey<Boolean> CONSUME_TRIGGER = BooleanComponent.BOOLEAN.key("consume_trigger")
			.optional(false)
			.preferred("consumeTrigger");
	RecipeKey<InteractionType> INTERACTION_TYPE = new EnumComponent<>(InteractionType.class)
			.key("interaction_type")
			.optional(InteractionType.ANY).preferred("interactionType");

	RecipeSchema SCHEMA = new RecipeSchema(
			InteractionRecipeJS.class,
			InteractionRecipeJS::new,
			TRIGGER_ITEM,
			TARGET_BLOCK,
			RESULTS,
			EXTRA_EFFECT,
			CONSUME_TRIGGER,
			INTERACTION_TYPE
	);
}