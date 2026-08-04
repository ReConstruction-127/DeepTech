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

	@Override
	public boolean matches(Container container, @NotNull Level level) {
		// 容器第0格放触发物品，第1格放目标方块（但Container无法直接存BlockState）
		// 所以我们用辅助方法，在查找时传入自定义容器
		return false;
	}

	// 手动匹配方法（在处理器中使用）
	public boolean matches(ItemStack trigger, BlockState target) {
		return triggerItem.test(trigger) && target.equals(targetBlockState);
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

	// ----- 辅助方法 -----
	public ItemStack getRandomResult(Random random) {
		int total = results.stream().mapToInt(wr -> wr.weight).sum();
		int roll = random.nextInt(total);
		for (WeightedResult wr : results) {
			roll -= wr.weight;
			if (roll < 0) return wr.stack.copy();
		}
		return results.isEmpty() ? ItemStack.EMPTY : results.get(0).stack.copy();
	}

	public static class WeightedResult {
		public final ItemStack stack;
		public final int weight;
		public WeightedResult(ItemStack stack, int weight) {
			this.stack = stack;
			this.weight = weight;
		}
	}

	public static class ExtraEffect {
		public final float chance;
		public final BlockState toState;
		public final List<ItemStack> extraDrops;
		public ExtraEffect(float chance, BlockState toState, List<ItemStack> extraDrops) {
			this.chance = chance;
			this.toState = toState;
			this.extraDrops = extraDrops;
		}
	}
}