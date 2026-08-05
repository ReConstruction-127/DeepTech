package dev.celestiacraft.deep_tech.api.recipe.builder.interaction;

import dev.celestiacraft.deep_tech.common.recipe.interaction.ChanceResult;
import dev.celestiacraft.deep_tech.common.recipe.interaction.InteractionType;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class InteractionRecipeBuilder implements RecipeBuilder {
	private Ingredient triggerItem;
	private BlockState targetState;
	private final List<ChanceResult> results = new ArrayList<>();
	private ExtraEffect extraEffect;
	private boolean consumeTrigger = false;
	private InteractionType interactionType = InteractionType.ANY;

	private final Advancement.Builder advancement = Advancement.Builder.advancement();

	private InteractionRecipeBuilder() {
	}

	public static InteractionRecipeBuilder builder() {
		return new InteractionRecipeBuilder();
	}

	public InteractionRecipeBuilder trigger(ItemLike item) {
		triggerItem = Ingredient.of(item);
		return this;
	}

	public InteractionRecipeBuilder trigger(TagKey<Item> tag) {
		triggerItem = Ingredient.of(tag);
		return this;
	}

	public InteractionRecipeBuilder trigger(Ingredient ingredient) {
		triggerItem = ingredient;
		return this;
	}

	public InteractionRecipeBuilder target(Block block) {
		targetState = block.defaultBlockState();
		return this;
	}

	public InteractionRecipeBuilder target(BlockState state) {
		targetState = state;
		return this;
	}

	// ✅ result 方法改为使用 chance
	public InteractionRecipeBuilder result(ItemLike item, double chance) {
		if (chance > 0 && chance <= 1) {
			results.add(new ChanceResult(new ItemStack(item), chance));
		}
		return this;
	}

	public InteractionRecipeBuilder result(ItemLike item, int count, double chance) {
		if (chance > 0 && chance <= 1) {
			results.add(new ChanceResult(new ItemStack(item, count), chance));
		}
		return this;
	}

	public InteractionRecipeBuilder result(ItemStack stack, double chance) {
		if (stack != null && !stack.isEmpty() && chance > 0 && chance <= 1) {
			results.add(new ChanceResult(stack.copy(), chance));
		}
		return this;
	}

	public InteractionRecipeBuilder extraEffect(double chance, Block toBlock, ItemLike... drops) {
		List<ItemStack> dropList = new ArrayList<>();
		for (ItemLike drop : drops) {
			dropList.add(new ItemStack(drop));
		}
		extraEffect = new ExtraEffect(chance, toBlock.defaultBlockState(), dropList);
		return this;
	}

	public InteractionRecipeBuilder extraEffect(double chance, BlockState toState, ItemStack... drops) {
		extraEffect = new ExtraEffect(chance, toState, List.of(drops));
		return this;
	}

	public InteractionRecipeBuilder consume(boolean consume) {
		consumeTrigger = consume;
		return this;
	}

	// 新增：设置交互类型
	public InteractionRecipeBuilder type(InteractionType type) {
		interactionType = type;
		return this;
	}

	@Override
	public @NotNull InteractionRecipeBuilder unlockedBy(@NotNull String name, @NotNull CriterionTriggerInstance criterion) {
		advancement.addCriterion(name, criterion);
		return this;
	}

	@Override
	public @NotNull InteractionRecipeBuilder group(@Nullable String group) {
		return this;
	}

	@Override
	public @NotNull Item getResult() {
		return results.isEmpty() ? Items.AIR : results.get(0).stack.getItem();
	}

	@Override
	public void save(@NotNull Consumer<FinishedRecipe> consumer, @NotNull ResourceLocation id) {
		if (triggerItem == null) {
			throw new IllegalStateException("Missing trigger item");
		}
		if (targetState == null) {
			throw new IllegalStateException("Missing target block");
		}
		if (results.isEmpty() && extraEffect == null) {
			throw new IllegalStateException("Recipe must have at least one result or an extra effect");
		}

		advancement.parent(ResourceLocation.tryParse("recipes/root"))
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
				.rewards(AdvancementRewards.Builder.recipe(id))
				.requirements(RequirementsStrategy.OR);

		consumer.accept(new InteractionRecipeResult(
				id,
				triggerItem,
				targetState,
				results,
				extraEffect,
				consumeTrigger,
				interactionType,
				advancement,
				id.withPrefix("recipes/")
		));
	}
}