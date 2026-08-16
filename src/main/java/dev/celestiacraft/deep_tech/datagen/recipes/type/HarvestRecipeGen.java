package dev.celestiacraft.deep_tech.datagen.recipes.type;

import dev.celestiacraft.deep_tech.api.recipe.builder.harvest.HarvestRecipeBuilder;
import dev.celestiacraft.deep_tech.common.register.item.MaterialItems;
import dev.celestiacraft.deep_tech.datagen.recipes.DTRecipeProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;
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
				.result(MaterialItems.SCULK_CHUNK.get(), 1, 0.25)
				.save(consumer, save("harvest/sculk"));

		HarvestRecipeBuilder.builder()
				.input(Blocks.SCULK_VEIN)
				.result(Items.SCULK_VEIN, 1, 0.25)
				.save(consumer, save("harvest/sculk_vein"));

		HarvestRecipeBuilder.builder()
				.input(Blocks.SCULK_CATALYST)
				.result(MaterialItems.SCULK_BONE.get(), 1, 1)
				.save(consumer, save("harvest/sculk_catalyst"));

		HarvestRecipeBuilder.builder()
				.input(Blocks.REINFORCED_DEEPSLATE)
				.result(MaterialItems.SCULK_BONE.get(), 1, 1)
				.save(consumer, save("harvest/reinforced_deepslate"));

		HarvestRecipeBuilder.builder()
				.input(Blocks.SCULK_SHRIEKER)
				.result(MaterialItems.SCULK_BONE.get(), 1, 1)
				.save(consumer, save("harvest/sculk_shrieker"));

		HarvestRecipeBuilder.builder()
				.input(Blocks.SCULK_SENSOR)
				.result(Items.REDSTONE, 1, 1)
				.save(consumer, save("harvest/sculk_sensor"));
	}
}