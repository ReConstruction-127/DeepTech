package dev.celestiacraft.deep_tech.common.recipe.harvest;

import dev.celestiacraft.deep_tech.common.register.DTRecipes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 幽匿采集器配方(数据驱动): 指定方块(或方块标签)被采集时, 按概率产出物品。
 * 匹配不使用容器, 而是直接匹配世界中的方块状态 {@link #matches(BlockState, Level)}。
 */
@Getter
@AllArgsConstructor
public class HarvestRecipe implements Recipe<Container> {
	private final ResourceLocation id;
	private final HarvestInput input;
	private final List<HarvestOutput> results;

	/** 方块状态是否匹配本配方 */
	public boolean matches(BlockState state, Level level) {
		return input.matches(state, level);
	}

	/** 按概率掷出本次采集的输出 */
	public List<ItemStack> rollOutputs(RandomSource random) {
		List<ItemStack> outputs = new ArrayList<>();
		for (HarvestOutput result : results) {
			if (random.nextDouble() < result.getChance()) {
				outputs.add(result.getStack().copy());
			}
		}
		return outputs;
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
		return results.isEmpty() ? ItemStack.EMPTY : results.get(0).getStack();
	}

	@Override
	public @NotNull ResourceLocation getId() {
		return id;
	}

	@Override
	public @NotNull RecipeSerializer<?> getSerializer() {
		return DTRecipes.HARVEST.getSerializer();
	}

	@Override
	public @NotNull RecipeType<?> getType() {
		return DTRecipes.HARVEST.getRecipeType();
	}

	@Override
	public @NotNull NonNullList<Ingredient> getIngredients() {
		return NonNullList.create();
	}
}