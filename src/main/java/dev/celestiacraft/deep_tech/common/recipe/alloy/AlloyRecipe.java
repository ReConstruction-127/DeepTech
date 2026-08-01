package dev.celestiacraft.deep_tech.common.recipe.alloy;

import dev.celestiacraft.deep_tech.api.ingredien.IngredientWithCount;
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

import java.util.List;

@Getter
@AllArgsConstructor
public class AlloyRecipe implements Recipe<Container> {
	private final ResourceLocation id;
	private final List<IngredientWithCount> inputs;
	private final ItemStack output;
	private final int energyCost;
	private final int processingTime;

	@Override
	public boolean matches(@NotNull Container container, @NotNull Level level) {
		if (inputs.isEmpty()) {
			return false;
		}
		// 按顺序匹配: 第 i 个输入必须匹配容器第 i 个槽位, 且数量足够
		for (int i = 0; i < inputs.size(); i++) {
			IngredientWithCount input = inputs.get(i);
			if (i >= container.getContainerSize()) {
				return false;
			}
			ItemStack stack = container.getItem(i);
			if (stack.getCount() < input.getCount() || !input.getIngredient().test(stack)) {
				return false;
			}
		}
		return true;
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
