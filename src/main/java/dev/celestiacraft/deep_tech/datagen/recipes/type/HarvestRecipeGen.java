package dev.celestiacraft.deep_tech.datagen.recipes.type;

import dev.celestiacraft.deep_tech.api.recipe.builder.harvest.HarvestRecipeBuilder;
import dev.celestiacraft.deep_tech.common.register.item.MaterialItems;
import dev.celestiacraft.deep_tech.datagen.recipes.DTRecipeProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Consumer;

public class HarvestRecipeGen extends DTRecipeProvider {
	public HarvestRecipeGen(PackOutput output) {
		super(output);
	}

	public static void register(Consumer<FinishedRecipe> consumer) {
		addDefaultRecipes(consumer);
	}

	private static void addDefaultRecipes(Consumer<FinishedRecipe> consumer) {
		// 默认配方: 幽匿块 → 4 幽匿碎块
		HarvestRecipeBuilder.builder()
				.input(Blocks.SCULK)
				.result(MaterialItems.SCULK_CHUNK.get(), 4, 1.0)
				.save(consumer, save("harvest/sculk"));
	}
}