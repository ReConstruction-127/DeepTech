package dev.celestiacraft.deep_tech.api.recipe.builder.cultivation;

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
 * 幽匿培育室配方构建器: 最多 2 物品输入 + 2 流体输入, 最多 4 物品输出 + 2 流体输出.
 */
public class CultivationRecipeBuilder implements RecipeBuilder {
	private static final int MAX_ITEM_INPUTS = 2;
	private static final int MAX_FLUID_INPUTS = 2;
	private static final int MAX_ITEM_OUTPUTS = 4;
	private static final int MAX_FLUID_OUTPUTS = 2;

	private final List<IngredientWithCount> itemInputs = new ArrayList<>();
	private final List<CultivationFluidInput> fluidInputs = new ArrayList<>();
	private final List<ItemStack> itemOutputs = new ArrayList<>();
	private final List<FluidStack> fluidOutputs = new ArrayList<>();

	private int energyCost = 50;
	private int processingTime = 100;
	private float itemOutputChance = 1.0f;

	private final Advancement.Builder advancement = Advancement.Builder.advancement();

	private CultivationRecipeBuilder() {
	}

	public static CultivationRecipeBuilder builder() {
		return new CultivationRecipeBuilder();
	}

	// ---------------- 物品输入 ----------------

	public CultivationRecipeBuilder itemInput(ItemLike item) {
		return itemInput(item, 1);
	}

	public CultivationRecipeBuilder itemInput(TagKey<Item> tag) {
		return itemInput(tag, 1);
	}

	public CultivationRecipeBuilder itemInput(Ingredient ingredient) {
		return itemInput(ingredient, 1);
	}

	public CultivationRecipeBuilder itemInput(ItemLike item, int count) {
		return itemInput(Ingredient.of(item), count);
	}

	public CultivationRecipeBuilder itemInput(TagKey<Item> tag, int count) {
		return itemInput(Ingredient.of(tag), count);
	}

	public CultivationRecipeBuilder itemInput(Ingredient ingredient, int count) {
		if (count < 1) {
			throw new IllegalArgumentException("Item input count must be at least 1");
		}
		if (itemInputs.size() >= MAX_ITEM_INPUTS) {
			throw new IllegalStateException("Cultivation recipe supports at most " + MAX_ITEM_INPUTS + " item inputs");
		}
		itemInputs.add(new IngredientWithCount(ingredient, count));
		return this;
	}

	// ---------------- 流体输入 ----------------

	public CultivationRecipeBuilder fluidInput(Fluid fluid) {
		return fluidInput(fluid, 1);
	}

	public CultivationRecipeBuilder fluidInput(Fluid fluid, int amount) {
		if (amount < 1) {
			throw new IllegalArgumentException("Fluid input amount must be at least 1");
		}
		if (fluidInputs.size() >= MAX_FLUID_INPUTS) {
			throw new IllegalStateException("Cultivation recipe supports at most " + MAX_FLUID_INPUTS + " fluid inputs");
		}
		fluidInputs.add(new CultivationFluidInput(fluid, amount));
		return this;
	}

	// ---------------- 物品输出 ----------------

	public CultivationRecipeBuilder itemOutput(ItemLike item) {
		return itemOutput(item, 1);
	}

	public CultivationRecipeBuilder itemOutput(ItemLike item, int count) {
		return itemOutput(new ItemStack(item, count));
	}

	public CultivationRecipeBuilder itemOutput(ItemStack stack) {
		if (stack.isEmpty()) {
			throw new IllegalArgumentException("Item output must not be empty");
		}
		if (itemOutputs.size() >= MAX_ITEM_OUTPUTS) {
			throw new IllegalStateException("Cultivation recipe supports at most " + MAX_ITEM_OUTPUTS + " item outputs");
		}
		itemOutputs.add(stack.copy());
		return this;
	}

	// ---------------- 流体输出 ----------------

	public CultivationRecipeBuilder fluidOutput(Fluid fluid, int amount) {
		if (amount < 1) {
			throw new IllegalArgumentException("Fluid output amount must be at least 1");
		}
		if (fluidOutputs.size() >= MAX_FLUID_OUTPUTS) {
			throw new IllegalStateException("Cultivation recipe supports at most " + MAX_FLUID_OUTPUTS + " fluid outputs");
		}
		fluidOutputs.add(new FluidStack(fluid, amount));
		return this;
	}

	public CultivationRecipeBuilder energyCost(int energyCost) {
		this.energyCost = energyCost;
		return this;
	}

	public CultivationRecipeBuilder processingTime(int processingTime) {
		this.processingTime = processingTime;
		return this;
	}

	/**
	 * 设置物品输出概率 (0~100). 每次加工完成时按该百分比掷骰决定是否产出物品;
	 * 流体输出不受影响, 必定产出.
	 */
	public CultivationRecipeBuilder itemOutputChance(int percent) {
		if (percent < 0 || percent > 100) {
			throw new IllegalArgumentException("Item output chance must be between 0 and 100");
		}
		this.itemOutputChance = percent / 100.0f;
		return this;
	}

	@Override
	public @NotNull CultivationRecipeBuilder unlockedBy(@NotNull String name, @NotNull CriterionTriggerInstance instance) {
		advancement.addCriterion(name, instance);
		return this;
	}

	@Override
	public @NotNull CultivationRecipeBuilder group(@Nullable String group) {
		return this;
	}

	@Override
	public @NotNull Item getResult() {
		return itemOutputs.isEmpty() ? null : itemOutputs.get(0).getItem();
	}

	@Override
	public void save(@NotNull Consumer<FinishedRecipe> consumer, @NotNull ResourceLocation id) {
		if (itemInputs.isEmpty() && fluidInputs.isEmpty()) {
			throw new IllegalStateException("Missing input for recipe " + id);
		}
		if (itemOutputs.isEmpty() && fluidOutputs.isEmpty()) {
			throw new IllegalStateException("Missing output for recipe " + id);
		}

		advancement.parent(ResourceLocation.tryParse("recipes/root"))
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
				.rewards(AdvancementRewards.Builder.recipe(id))
				.requirements(RequirementsStrategy.OR);

		consumer.accept(new CultivationRecipeResult(
				id,
				itemInputs,
				fluidInputs,
				itemOutputs,
				fluidOutputs,
				energyCost,
				processingTime,
				itemOutputChance,
				advancement,
				id.withPrefix("recipes/")
		));
	}
}