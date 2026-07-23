package dev.celestiacraft.deep_tech.common.recipe.crushing;

import dev.celestiacraft.deep_tech.common.register.DTRecipes;
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

@Getter
public class CrushingRecipe implements Recipe<Container> {
	private final ResourceLocation id;
	private final Ingredient input;
	private final ItemStack output;
	private final int energyCost;
	private final int processingTime;

	public CrushingRecipe(
			ResourceLocation id,
			Ingredient input,
			ItemStack output,
			int energyCost,
			int processingTime
	) {
		this.id = id;
		this.input = input;
		this.output = output;
		this.energyCost = energyCost;
		this.processingTime = processingTime;
	}

	@Override
	public boolean matches(Container container, @NotNull Level level) {
		return input.test(container.getItem(0));
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
		return DTRecipes.CRUSHING.getSerializer();
	}

	@Override
	public @NotNull RecipeType<?> getType() {
		return DTRecipes.CRUSHING.getRecipeType();
	}

	@Override
	public @NotNull NonNullList<Ingredient> getIngredients() {
		NonNullList<Ingredient> list = NonNullList.create();
		list.add(input);
		return list;
	}
}