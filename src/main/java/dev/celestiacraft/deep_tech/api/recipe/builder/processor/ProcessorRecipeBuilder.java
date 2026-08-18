package dev.celestiacraft.deep_tech.api.recipe.builder.processor;

import dev.celestiacraft.libs.api.recipe.ingredient.item.IngredientWithCount;
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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ProcessorRecipeBuilder implements RecipeBuilder {
	private final List<IngredientWithCount> itemInputs = new ArrayList<>();
	private final List<ItemStack> itemOutputs = new ArrayList<>();

	private int energyCost = 50;
	private int processingTime = 100;

	private final Advancement.Builder advancement = Advancement.Builder.advancement();

	private ProcessorRecipeBuilder() {
	}

	public static ProcessorRecipeBuilder builder() {
		return new ProcessorRecipeBuilder();
	}

	public ProcessorRecipeBuilder itemInput(ItemLike item) {
		return itemInput(item, 1);
	}

	public ProcessorRecipeBuilder itemInput(TagKey<Item> tag) {
		return itemInput(tag, 1);
	}

	public ProcessorRecipeBuilder itemInput(Ingredient ingredient) {
		return itemInput(ingredient, 1);
	}

	public ProcessorRecipeBuilder itemInput(ItemLike item, int count) {
		return itemInput(Ingredient.of(item), count);
	}

	public ProcessorRecipeBuilder itemInput(TagKey<Item> tag, int count) {
		return itemInput(Ingredient.of(tag), count);
	}

	public ProcessorRecipeBuilder itemInput(Ingredient ingredient, int count) {
		if (count < 1) {
			throw new IllegalArgumentException("Input count must be at least 1");
		}
		if (itemInputs.size() >= 2) {
			throw new IllegalStateException("Processor recipe supports at most 2 item inputs");
		}
		itemInputs.add(new IngredientWithCount(ingredient, count));
		return this;
	}

	public ProcessorRecipeBuilder itemOutput(ItemLike item) {
		return itemOutput(item, 1);
	}

	public ProcessorRecipeBuilder itemOutput(ItemLike item, int count) {
		return itemOutput(new ItemStack(item, count));
	}

	public ProcessorRecipeBuilder itemOutput(ItemStack stack) {
		if (itemOutputs.size() >= 2) {
			throw new IllegalStateException("Processor recipe supports at most 2 item outputs");
		}
		itemOutputs.add(stack.copy());
		return this;
	}

	public ProcessorRecipeBuilder energyCost(int energyCost) {
		this.energyCost = energyCost;
		return this;
	}

	public ProcessorRecipeBuilder processingTime(int processingTime) {
		this.processingTime = processingTime;
		return this;
	}

	@Override
	public @NotNull ProcessorRecipeBuilder unlockedBy(@NotNull String name, @NotNull CriterionTriggerInstance instance) {
		advancement.addCriterion(name, instance);
		return this;
	}

	@Override
	public @NotNull ProcessorRecipeBuilder group(@Nullable String group) {
		return this;
	}

	@Override
	public @NotNull Item getResult() {
		return itemOutputs.isEmpty() ? null : itemOutputs.get(0).getItem();
	}

	@Override
	public void save(@NotNull Consumer<FinishedRecipe> consumer, @NotNull ResourceLocation id) {
		if (itemInputs.isEmpty()) {
			throw new IllegalStateException("Missing input for recipe " + id);
		}

		if (itemOutputs.isEmpty()) {
			throw new IllegalStateException("Missing output for recipe " + id);
		}

		advancement.parent(ResourceLocation.tryParse("recipes/root"))
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
				.rewards(AdvancementRewards.Builder.recipe(id))
				.requirements(RequirementsStrategy.OR);

		consumer.accept(new ProcessorRecipeResult(
				id,
				itemInputs,
				itemOutputs,
				energyCost,
				processingTime,
				advancement,
				id.withPrefix("recipes/")
		));
	}
}