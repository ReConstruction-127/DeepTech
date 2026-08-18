package dev.celestiacraft.deep_tech.common.recipe.assembling;

import dev.celestiacraft.deep_tech.common.recipe.cultivation.CultivationFluidInput;
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
 * 组装机配方: 最多 16 物品输入 + 2 流体输入, 产出最多 4 种物品 + 1 种流体.
 * <p>
 * 物品侧沿用无序匹配; 流体侧按种类匹配(顺序无关);
 * 催化剂(catalyst)只在催化剂槽存在即可, 加工过程中不消耗.
 */
@Getter
@AllArgsConstructor
public class AssemblingRecipe implements Recipe<Container> {
	private final ResourceLocation id;
	private final List<IngredientWithCount> itemInputs;
	private final List<CultivationFluidInput> fluidInputs;
	/** 催化剂: 可空, 要求催化剂槽中存在匹配物品, 但不消耗 */
	private final Ingredient catalyst;
	private final List<ItemStack> itemOutputs;
	private final List<FluidStack> fluidOutputs;
	private final int energyCost;
	private final int processingTime;

	/**
	 * 无序匹配: 为每个输入寻找一个未被占用的匹配槽位.
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

	/** 检查催化剂槽中的物品是否满足催化剂要求 (催化剂不消耗) */
	public boolean matchesCatalyst(@NotNull ItemStack stack) {
		return catalyst == null || (!stack.isEmpty() && catalyst.test(stack));
	}

	/** 流体按种类匹配, 与罐顺序无关 */
	public boolean matchesFluids(@NotNull List<FluidStack> available) {
		boolean[] used = new boolean[available.size()];
		for (CultivationFluidInput fluidInput : fluidInputs) {
			boolean found = false;
			for (int i = 0; i < available.size(); i++) {
				if (used[i]) {
					continue;
				}
				if (fluidInput.matches(available.get(i))) {
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
		return DTRecipes.ASSEMBLING.getSerializer();
	}

	@Override
	public @NotNull RecipeType<?> getType() {
		return DTRecipes.ASSEMBLING.getRecipeType();
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