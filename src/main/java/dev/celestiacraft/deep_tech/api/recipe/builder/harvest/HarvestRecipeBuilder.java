package dev.celestiacraft.deep_tech.api.recipe.builder.harvest;

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
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class HarvestRecipeBuilder implements RecipeBuilder {
	private final List<HarvestOutput> outputs = new ArrayList<>();
	private Block inputBlock;
	private TagKey<Block> inputTag;

	private final Advancement.Builder advancement = Advancement.Builder.advancement();

	private HarvestRecipeBuilder() {
	}

	public static HarvestRecipeBuilder builder() {
		return new HarvestRecipeBuilder();
	}

	public static class HarvestOutput {
		public final ItemStack stack;
		public final double chance;

		HarvestOutput(ItemStack stack, double chance) {
			this.stack = stack;
			this.chance = chance;
		}
	}

	public HarvestRecipeBuilder input(Block block) {
		this.inputBlock = block;
		this.inputTag = null;
		return this;
	}

	public HarvestRecipeBuilder input(TagKey<Block> blockTag) {
		this.inputTag = blockTag;
		this.inputBlock = null;
		return this;
	}

	public HarvestRecipeBuilder result(ItemLike item) {
		return result(item, 1, 1.0);
	}

	public HarvestRecipeBuilder result(ItemLike item, int count) {
		return result(item, count, 1.0);
	}

	public HarvestRecipeBuilder result(ItemLike item, int count, double chance) {
		outputs.add(new HarvestOutput(new ItemStack(item, count), chance));
		return this;
	}

	public HarvestRecipeBuilder result(ItemStack stack, double chance) {
		outputs.add(new HarvestOutput(stack.copy(), chance));
		return this;
	}

	public List<HarvestOutput> getOutputs() {
		return outputs;
	}

	public Block getInputBlock() {
		return inputBlock;
	}

	public TagKey<Block> getInputTag() {
		return inputTag;
	}

	@Override
	public @NotNull HarvestRecipeBuilder unlockedBy(@NotNull String name, @NotNull CriterionTriggerInstance instance) {
		advancement.addCriterion(name, instance);
		return this;
	}

	@Override
	public @NotNull HarvestRecipeBuilder group(@Nullable String group) {
		return this;
	}

	@Override
	public @NotNull Item getResult() {
		return outputs.isEmpty() ? null : outputs.get(0).stack.getItem();
	}

	@Override
	public void save(@NotNull Consumer<FinishedRecipe> consumer, @NotNull ResourceLocation id) {
		if (inputBlock == null && inputTag == null) {
			throw new IllegalStateException("Missing input for recipe " + id);
		}
		if (outputs.isEmpty()) {
			throw new IllegalStateException("Missing outputs for recipe " + id);
		}

		advancement.parent(ResourceLocation.tryParse("recipes/root"))
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
				.rewards(AdvancementRewards.Builder.recipe(id))
				.requirements(RequirementsStrategy.OR);

		consumer.accept(new HarvestRecipeResult(id, this, advancement, id.withPrefix("recipes/")));
	}
}