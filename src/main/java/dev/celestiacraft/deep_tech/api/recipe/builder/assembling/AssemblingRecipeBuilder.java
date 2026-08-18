package dev.celestiacraft.deep_tech.api.recipe.builder.assembling;

import dev.celestiacraft.deep_tech.common.recipe.cultivation.CultivationFluidInput;
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
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 组装机配方构建器: 最多 16 物品输入 + 2 流体输入, 最多 4 物品输出 + 1 流体输出, 可带 1 个催化剂(不消耗).
 */
public class AssemblingRecipeBuilder implements RecipeBuilder {
	private static final int MAX_ITEM_INPUTS = 16;
	private static final int MAX_FLUID_INPUTS = 2;
	private static final int MAX_ITEM_OUTPUTS = 4;
	private static final int MAX_FLUID_OUTPUTS = 1;

	private final List<IngredientWithCount> itemInputs = new ArrayList<>();
	private final List<CultivationFluidInput> fluidInputs = new ArrayList<>();
	private final List<ItemStack> itemOutputs = new ArrayList<>();
	private final List<FluidStack> fluidOutputs = new ArrayList<>();

	private Ingredient catalyst = null;

	private int energyCost = 50;
	private int processingTime = 100;

	private final Advancement.Builder advancement = Advancement.Builder.advancement();

	private AssemblingRecipeBuilder() {
	}

	public static AssemblingRecipeBuilder builder() {
		return new AssemblingRecipeBuilder();
	}

	// ---------------- 物品输入 ----------------

	public AssemblingRecipeBuilder itemInput(ItemLike item) {
		return itemInput(item, 1);
	}

	public AssemblingRecipeBuilder itemInput(TagKey<Item> tag) {
		return itemInput(tag, 1);
	}

	public AssemblingRecipeBuilder itemInput(Ingredient ingredient) {
		return itemInput(ingredient, 1);
	}

	public AssemblingRecipeBuilder itemInput(ItemLike item, int count) {
		return itemInput(Ingredient.of(item), count);
	}

	public AssemblingRecipeBuilder itemInput(TagKey<Item> tag, int count) {
		return itemInput(Ingredient.of(tag), count);
	}

	public AssemblingRecipeBuilder itemInput(Ingredient ingredient, int count) {
		if (count < 1) {
			throw new IllegalArgumentException("Input count must be at least 1");
		}
		if (itemInputs.size() >= MAX_ITEM_INPUTS) {
			throw new IllegalStateException("Assembling recipe supports at most " + MAX_ITEM_INPUTS + " item inputs");
		}
		itemInputs.add(new IngredientWithCount(ingredient, count));
		return this;
	}

	// ---------------- 流体输入 ----------------

	public AssemblingRecipeBuilder fluidInput(Fluid fluid, int amount) {
		if (amount < 1) {
			throw new IllegalArgumentException("Fluid input amount must be at least 1");
		}
		if (fluidInputs.size() >= MAX_FLUID_INPUTS) {
			throw new IllegalStateException("Assembling recipe supports at most " + MAX_FLUID_INPUTS + " fluid inputs");
		}
		fluidInputs.add(new CultivationFluidInput(fluid, amount));
		return this;
	}

	// ---------------- 催化剂 ----------------

	public AssemblingRecipeBuilder catalyst(ItemLike item) {
		return catalyst(Ingredient.of(item));
	}

	public AssemblingRecipeBuilder catalyst(TagKey<Item> tag) {
		return catalyst(Ingredient.of(tag));
	}

	public AssemblingRecipeBuilder catalyst(Ingredient ingredient) {
		this.catalyst = ingredient;
		return this;
	}

	// ---------------- 输出 ----------------

	public AssemblingRecipeBuilder itemOutput(ItemLike item) {
		return itemOutput(item, 1);
	}

	public AssemblingRecipeBuilder itemOutput(ItemLike item, int count) {
		return itemOutput(new ItemStack(item, count));
	}

	public AssemblingRecipeBuilder itemOutput(ItemStack stack) {
		if (itemOutputs.size() >= MAX_ITEM_OUTPUTS) {
			throw new IllegalStateException("Assembling recipe supports at most " + MAX_ITEM_OUTPUTS + " item outputs");
		}
		itemOutputs.add(stack.copy());
		return this;
	}

	public AssemblingRecipeBuilder fluidOutput(Fluid fluid, int amount) {
		if (amount < 1) {
			throw new IllegalArgumentException("Fluid output amount must be at least 1");
		}
		if (fluidOutputs.size() >= MAX_FLUID_OUTPUTS) {
			throw new IllegalStateException("Assembling recipe supports at most " + MAX_FLUID_OUTPUTS + " fluid output");
		}
		fluidOutputs.add(new FluidStack(fluid, amount));
		return this;
	}

	// ---------------- 能量/时间 ----------------

	public AssemblingRecipeBuilder energyCost(int energyCost) {
		this.energyCost = energyCost;
		return this;
	}

	public AssemblingRecipeBuilder processingTime(int processingTime) {
		this.processingTime = processingTime;
		return this;
	}

	@Override
	public @NotNull AssemblingRecipeBuilder unlockedBy(@NotNull String name, @NotNull CriterionTriggerInstance instance) {
		advancement.addCriterion(name, instance);
		return this;
	}

	@Override
	public @NotNull AssemblingRecipeBuilder group(@Nullable String group) {
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

		consumer.accept(new AssemblingRecipeResult(
				id,
				itemInputs,
				fluidInputs,
				catalyst,
				itemOutputs,
				fluidOutputs,
				energyCost,
				processingTime,
				advancement,
				id.withPrefix("recipes/")
		));
	}
}