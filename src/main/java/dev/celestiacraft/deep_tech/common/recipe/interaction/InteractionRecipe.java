package dev.celestiacraft.deep_tech.common.recipe.interaction;

import dev.celestiacraft.deep_tech.common.register.DTRecipes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

@Getter
@AllArgsConstructor
public class InteractionRecipe implements Recipe<Container> {
	private final ResourceLocation id;
	private final Ingredient triggerItem;
	private final BlockState targetBlockState;
	private final List<WeightedResult> results;
	private final ExtraEffect extraEffect;
	private final boolean consumeTrigger;
	private final InteractionType interactionType;

	public boolean matches(ItemStack trigger, BlockState target, boolean isRightClick) {
		// 先检查物品和方块
		if (!triggerItem.test(trigger) || !target.equals(targetBlockState)) {
			return false;
		}
		// 再检查交互类型
		if (interactionType == InteractionType.ANY) {
			return true;
		}
		if (interactionType == InteractionType.LEFT_CLICK && !isRightClick) {
			return true;
		}
		return interactionType == InteractionType.RIGHT_CLICK && isRightClick;
	}


	@Override
	public boolean matches(@NotNull Container container, @NotNull Level level) {
		return false;
	}

	@Override
	public @NotNull ItemStack assemble(@NotNull Container container, @NotNull RegistryAccess access) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	@Override
	public @NotNull ItemStack getResultItem(@NotNull RegistryAccess access) {
		return results.isEmpty() ? ItemStack.EMPTY : results.get(0).stack;
	}

	@Override
	public @NotNull ResourceLocation getId() {
		return id;
	}

	@Override
	public @NotNull RecipeSerializer<?> getSerializer() {
		return DTRecipes.INTERACTION.getSerializer();
	}

	@Override
	public @NotNull RecipeType<?> getType() {
		return DTRecipes.INTERACTION.getRecipeType();
	}

	@Override
	public @NotNull NonNullList<Ingredient> getIngredients() {
		return NonNullList.of(triggerItem);
	}

	public ItemStack getRandomResult(Random random) {
		int total = results.stream()
				.mapToInt((result) -> {
					return result.weight;
				})
				.sum();
		int roll = random.nextInt(total);
		for (WeightedResult wr : results) {
			roll -= wr.weight;
			if (roll < 0) return wr.stack.copy();
		}
		return results.isEmpty() ? ItemStack.EMPTY : results.get(0).stack.copy();
	}
}