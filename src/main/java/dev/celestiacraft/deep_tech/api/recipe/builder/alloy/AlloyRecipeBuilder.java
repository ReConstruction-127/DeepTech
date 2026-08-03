package dev.celestiacraft.deep_tech.api.recipe.builder.alloy;

import dev.celestiacraft.deep_tech.api.ingredient.IngredientWithCount;
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

public class AlloyRecipeBuilder implements RecipeBuilder {
	private final List<IngredientWithCount> inputs = new ArrayList<>();
	private ItemStack output = ItemStack.EMPTY;

	private int energyCost = 50;
	private int processingTime = 100;

	private final Advancement.Builder advancement = Advancement.Builder.advancement();

	private AlloyRecipeBuilder() {
	}

	public static AlloyRecipeBuilder builder() {
		return new AlloyRecipeBuilder();
	}

	public AlloyRecipeBuilder input(ItemLike item) {
		return input(item, 1);
	}

	public AlloyRecipeBuilder input(TagKey<Item> tag) {
		return input(tag, 1);
	}

	public AlloyRecipeBuilder input(Ingredient ingredient) {
		return input(ingredient, 1);
	}

	public AlloyRecipeBuilder input(ItemLike item, int count) {
		return input(Ingredient.of(item), count);
	}

	public AlloyRecipeBuilder input(TagKey<Item> tag, int count) {
		return input(Ingredient.of(tag), count);
	}

	public AlloyRecipeBuilder input(Ingredient ingredient, int count) {
		if (count < 1) {
			throw new IllegalArgumentException("Input count must be at least 1");
		}
		inputs.add(new IngredientWithCount(ingredient, count));
		return this;
	}

	public AlloyRecipeBuilder output(ItemLike item) {
		output = new ItemStack(item);
		return this;
	}

	public AlloyRecipeBuilder output(ItemLike item, int count) {
		output = new ItemStack(item, count);
		return this;
	}

	public AlloyRecipeBuilder output(ItemStack stack) {
		output = stack.copy();
		return this;
	}

	public AlloyRecipeBuilder energyCost(int energyCost) {
		this.energyCost = energyCost;
		return this;
	}

	public AlloyRecipeBuilder processingTime(int processingTime) {
		this.processingTime = processingTime;
		return this;
	}

	@Override
	public @NotNull AlloyRecipeBuilder unlockedBy(@NotNull String name, @NotNull CriterionTriggerInstance instance) {
		advancement.addCriterion(name, instance);
		return this;
	}

	@Override
	public @NotNull AlloyRecipeBuilder group(@Nullable String group) {
		return this;
	}

	@Override
	public @NotNull Item getResult() {
		return output.getItem();
	}

	@Override
	public void save(@NotNull Consumer<FinishedRecipe> consumer, @NotNull ResourceLocation id) {
		if (inputs.isEmpty()) {
			throw new IllegalStateException("Missing input for recipe " + id);
		}

		if (output.isEmpty()) {
			throw new IllegalStateException("Missing output for recipe " + id);
		}

		advancement.parent(ResourceLocation.tryParse("recipes/root"))
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
				.rewards(AdvancementRewards.Builder.recipe(id))
				.requirements(RequirementsStrategy.OR);

		consumer.accept(new AlloyRecipeResult(
				id,
				inputs,
				output,
				energyCost,
				processingTime,
				advancement,
				id.withPrefix("recipes/")
		));
	}
}
