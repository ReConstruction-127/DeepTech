package dev.celestiacraft.deep_tech.common.register;

import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.api.register.recipe.RecipeRegistry;
import dev.celestiacraft.deep_tech.common.recipe.crushing.CrushingRecipe;
import dev.celestiacraft.deep_tech.common.recipe.crushing.CrushingRecipeSerializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class DTRecipes {
	private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS;
	private static final DeferredRegister<RecipeType<?>> TYPES;

	public static final RecipeRegistry<CrushingRecipe> CRUSHING;

	static {
		SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, DeepTech.MODID);
		TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, DeepTech.MODID);

		CRUSHING = add("crushing", CrushingRecipeSerializer::new);
	}

	public static void register(IEventBus bus) {
		SERIALIZERS.register(bus);
		TYPES.register(bus);
	}

	private static <T extends Recipe<?>> RecipeRegistry<T> add(
			String name,
			Supplier<? extends RecipeSerializer<T>> serializer
	) {
		return new RecipeRegistry<>(TYPES.register(name, () -> {
			return new RecipeType<>() {
				@Override
				public String toString() {
					return DeepTech.MODID + ":" + name;
				}
			};
		}), SERIALIZERS.register(name, serializer));
	}
}