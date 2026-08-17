package dev.celestiacraft.deep_tech.common.recipe.cultivation;

import dev.celestiacraft.deep_tech.common.register.DTRecipes;
import dev.celestiacraft.libs.api.recipe.ingredient.item.IngredientWithCount;
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
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 幽匿培育室配方: 2 物品输入 + 2 流体输入, 产出最多 4 种物品 + 2 种流体.
 * <p>
 * 物品侧沿用 {@link dev.celestiacraft.deep_tech.common.recipe.alloy.AlloyRecipe} 的无序匹配;
 * 流体侧由机器把各输入罐当前流体汇总后调用 {@link #matchesFluids(List)} 校验.
 * <p>
 * 物品输出带概率({@link #itemOutputChance}, 0~1), 每次加工完成时掷骰决定是否产出;
 * 流体输出不受概率影响, 必定产出.
 */
@Getter
@AllArgsConstructor
public class CultivationRecipe implements Recipe<Container> {
	private final ResourceLocation id;
	private final List<IngredientWithCount> itemInputs;
	private final List<CultivationFluidInput> fluidInputs;
	private final List<ItemStack> itemOutputs;
	private final List<FluidStack> fluidOutputs;
	private final int energyCost;
	private final int processingTime;
	/** 物品输出概率 (0~1), 每次加工完成时统一掷一次骰 */
	private final float itemOutputChance;

	/**
	 * 物品侧无序匹配: 为每个输入寻找一个未被占用的匹配槽位.
	 *
	 * @return 每个输入对应的槽位下标; 无法完全匹配时返回 {@code null}
	 */
	public int @Nullable [] matchSlots(@NotNull Container container) {
		if (itemInputs.isEmpty()) {
			return null;
		}
		int[] slots = new int[itemInputs.size()];
		boolean[] used = new boolean[container.getContainerSize()];

		for (int i = 0; i < itemInputs.size(); i++) {
			IngredientWithCount input = itemInputs.get(i);
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

	/**
	 * 流体侧匹配: 每个流体输入都能在给定流体列表中找到足够量的同种流体.
	 *
	 * @param available 机器输入罐中当前的流体(顺序无关)
	 * @return 是否全部满足
	 */
	public boolean matchesFluids(@NotNull List<FluidStack> available) {
		if (fluidInputs.isEmpty()) {
			return true;
		}
		boolean[] used = new boolean[available.size()];
		for (CultivationFluidInput input : fluidInputs) {
			boolean found = false;
			for (int i = 0; i < available.size(); i++) {
				if (used[i]) {
					continue;
				}
				if (input.matches(available.get(i))) {
					used[i] = true;
					found = true;
					break;
				}
			}
			if (!found) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean matches(@NotNull Container container, @NotNull Level level) {
		return matchSlots(container) != null;
	}

	@Override
	public @NotNull ItemStack assemble(@NotNull Container container, @NotNull RegistryAccess access) {
		return itemOutputs.isEmpty() ? ItemStack.EMPTY : itemOutputs.get(0).copy();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	@Override
	public @NotNull ItemStack getResultItem(@NotNull RegistryAccess access) {
		return itemOutputs.isEmpty() ? ItemStack.EMPTY : itemOutputs.get(0);
	}

	@Override
	public @NotNull ResourceLocation getId() {
		return id;
	}

	@Override
	public @NotNull RecipeSerializer<?> getSerializer() {
		return DTRecipes.CULTIVATION.getSerializer();
	}

	@Override
	public @NotNull RecipeType<?> getType() {
		return DTRecipes.CULTIVATION.getRecipeType();
	}

	@Override
	public @NotNull NonNullList<Ingredient> getIngredients() {
		NonNullList<Ingredient> list = NonNullList.create();
		for (IngredientWithCount input : itemInputs) {
			list.add(input.getIngredient());
		}
		return list;
	}
}
