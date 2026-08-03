package dev.celestiacraft.deep_tech.common.recipe.alloy;

import dev.celestiacraft.deep_tech.api.ingredient.IngredientWithCount;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
@AllArgsConstructor
public class AlloyRecipe implements Recipe<Container> {
	private final ResourceLocation id;
	private final List<IngredientWithCount> inputs;
	private final ItemStack output;
	private final int energyCost;
	private final int processingTime;

	/**
	 * 无序匹配: 为每个输入寻找一个未被占用的匹配槽位.
	 *
	 * @return 每个输入对应的槽位下标; 无法完全匹配时返回 {@code null}
	 */
	public int @Nullable [] matchSlots(@NotNull Container container) {
		if (inputs.isEmpty()) {
			return null;
		}
		int[] slots = new int[inputs.size()];
		boolean[] used = new boolean[container.getContainerSize()];

		for (int i = 0; i < inputs.size(); i++) {
			IngredientWithCount input = inputs.get(i);
			boolean found = false;
			for (int slot = 0; slot < container.getContainerSize(); slot++) {
				if (used[slot]) {
					continue;
				}
				ItemStack stack = container.getItem(slot);
				if (stack.getCount() >= input.getCount() && input.getIngredient().test(stack)) {
					used[slot] = true;
					slots[i] = slot;
					found = true;
					break;
				}
			}
			if (!found) {
				return null;
			}
		}
		return slots;
	}

	@Override
	public boolean matches(@NotNull Container container, @NotNull Level level) {
		return matchSlots(container) != null;
	}

	@Override
	public @NotNull ItemStack assemble(@NotNull Container container, @NotNull RegistryAccess access) {
		return output.copy();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	@Override
	public @NotNull ItemStack getResultItem(@NotNull RegistryAccess access) {
		return output;
	}

	@Override
	public @NotNull ResourceLocation getId() {
		return id;
	}

	@Override
	public @NotNull RecipeSerializer<?> getSerializer() {
		return DTRecipes.ALLOY.getSerializer();
	}

	@Override
	public @NotNull RecipeType<?> getType() {
		return DTRecipes.ALLOY.getRecipeType();
	}

	@Override
	public @NotNull NonNullList<Ingredient> getIngredients() {
		NonNullList<Ingredient> list = NonNullList.create();
		for (IngredientWithCount input : inputs) {
			list.add(input.getIngredient());
		}
		return list;
	}
}
