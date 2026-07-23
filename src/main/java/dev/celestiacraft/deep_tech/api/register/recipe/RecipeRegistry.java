package dev.celestiacraft.deep_tech.api.register.recipe;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.function.Supplier;

public class RecipeRegistry<T extends Recipe<?>> implements Supplier<RecipeType<T>> {
	private final Supplier<RecipeType<T>> type;
	private final Supplier<RecipeSerializer<T>> serializer;

	public RecipeRegistry(Supplier<RecipeType<T>> type, Supplier<RecipeSerializer<T>> serializer) {
		this.type = type;
		this.serializer = serializer;
	}

	@Override
	public RecipeType<T> get() {
		return type.get();
	}

	public RecipeType<T> getRecipeType() {
		return get();
	}

	public RecipeSerializer<T> getSerializer() {
		return serializer.get();
	}

	public Supplier<RecipeType<T>> typeHolder() {
		return type;
	}

	public Supplier<RecipeSerializer<T>> serializerHolder() {
		return serializer;
	}
}