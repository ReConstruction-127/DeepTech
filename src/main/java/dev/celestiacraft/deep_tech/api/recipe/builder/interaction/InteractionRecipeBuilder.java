package dev.celestiacraft.deep_tech.api.recipe.builder.interaction;

import dev.celestiacraft.deep_tech.common.recipe.interaction.InteractionRecipe;
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
	private final List<WeightedResult> results = new ArrayList<>();
	private ExtraEffect extraEffect;
	private boolean consumeTrigger = false;
	private InteractionRecipe.InteractionType interactionType = InteractionRecipe.InteractionType.ANY; // 默认

	private final Advancement.Builder advancement = Advancement.Builder.advancement();

	private InteractionRecipeBuilder() {}

	public static InteractionRecipeBuilder builder() { return new InteractionRecipeBuilder(); }

	// 链式方法
	public InteractionRecipeBuilder trigger(ItemLike item) { this.triggerItem = Ingredient.of(item); return this; }
	public InteractionRecipeBuilder trigger(TagKey<Item> tag) { this.triggerItem = Ingredient.of(tag); return this; }
	public InteractionRecipeBuilder trigger(Ingredient ingredient) { this.triggerItem = ingredient; return this; }
	public InteractionRecipeBuilder target(Block block) { this.targetState = block.defaultBlockState(); return this; }
	public InteractionRecipeBuilder target(BlockState state) { this.targetState = state; return this; }
	public InteractionRecipeBuilder result(ItemLike item, int weight) { results.add(new WeightedResult(new ItemStack(item), weight)); return this; }
	public InteractionRecipeBuilder result(ItemLike item, int count, int weight) { results.add(new WeightedResult(new ItemStack(item, count), weight)); return this; }
	public InteractionRecipeBuilder result(ItemStack stack, int weight) { results.add(new WeightedResult(stack.copy(), weight)); return this; }
	public InteractionRecipeBuilder extraEffect(float chance, Block toBlock, ItemLike... drops) {
		List<ItemStack> dropList = new ArrayList<>();
		for (ItemLike drop : drops) dropList.add(new ItemStack(drop));
		extraEffect = new ExtraEffect(chance, toBlock.defaultBlockState(), dropList);
		return this;
	}
	public InteractionRecipeBuilder extraEffect(float chance, BlockState toState, ItemStack... drops) {
		extraEffect = new ExtraEffect(chance, toState, List.of(drops));
		return this;
	}
	public InteractionRecipeBuilder consume(boolean consume) { this.consumeTrigger = consume; return this; }

	// 新增：设置交互类型
	public InteractionRecipeBuilder type(InteractionRecipe.InteractionType type) {
		this.interactionType = type;
		return this;
	}

	@Override public @NotNull InteractionRecipeBuilder unlockedBy(@NotNull String name, @NotNull CriterionTriggerInstance criterion) { advancement.addCriterion(name, criterion); return this; }
	@Override public @NotNull InteractionRecipeBuilder group(@Nullable String group) { return this; }
	@Override public @NotNull Item getResult() { return results.isEmpty() ? net.minecraft.world.item.Items.AIR : results.get(0).stack.getItem(); }

	@Override
	public void save(@NotNull Consumer<FinishedRecipe> consumer, @NotNull ResourceLocation id) {
		if (triggerItem == null) throw new IllegalStateException("Missing trigger item");
		if (targetState == null) throw new IllegalStateException("Missing target block");
		if (results.isEmpty()) throw new IllegalStateException("Missing results");

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

	// 内部类 public 以便 Result 访问
	public static class WeightedResult {
		public final ItemStack stack;
		public final int weight;
		public WeightedResult(ItemStack stack, int weight) { this.stack = stack; this.weight = weight; }
	}

	public static class ExtraEffect {
		public final float chance;
		public final BlockState toState;
		public final List<ItemStack> extraDrops;
		public ExtraEffect(float chance, BlockState toState, List<ItemStack> drops) {
			this.chance = chance;
			this.toState = toState;
			this.extraDrops = drops;
		}
	}
}