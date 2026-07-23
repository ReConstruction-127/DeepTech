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
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class DTRecipes {
	private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS;
	private static final DeferredRegister<RecipeType<?>> TYPES;

	// ✅ 只保留这一个，它已经包含了 RecipeType 和 RecipeSerializer
	public static final RecipeRegistry<CrushingRecipe> CRUSHING;

	// ✅ 额外暴露 RecipeType 和 RecipeSerializer 的 RegistryObject
	public static RegistryObject<RecipeType<CrushingRecipe>> CRUSHING_TYPE;
	public static RegistryObject<RecipeSerializer<CrushingRecipe>> CRUSHING_SERIALIZER;

	static {
		SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, DeepTech.MODID);
		TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, DeepTech.MODID);

		CRUSHING_TYPE = TYPES.register("crushing", () -> new RecipeType<>() {
			@Override
			public String toString() {
				return DeepTech.MODID + ":" + "crushing";
			}
		});
		CRUSHING_SERIALIZER = SERIALIZERS.register("crushing", CrushingRecipeSerializer::new);

		// 如果 RecipeRegistry 需要传入，可以在这里构造
		CRUSHING = new RecipeRegistry<>(CRUSHING_TYPE, CRUSHING_SERIALIZER);
	}

	public static void register(IEventBus bus) {
		SERIALIZERS.register(bus);
		TYPES.register(bus);
	}

	private static <T extends Recipe<?>> RecipeRegistry<T> add(
			String name,
			Supplier<? extends RecipeSerializer<T>> serializer
	) {
		return new RecipeRegistry<>(
				TYPES.register(name, () -> new RecipeType<>() {
					@Override
					public String toString() {
						return DeepTech.MODID + ":" + name;
					}
				}),
				SERIALIZERS.register(name, serializer)
		);
	}
}