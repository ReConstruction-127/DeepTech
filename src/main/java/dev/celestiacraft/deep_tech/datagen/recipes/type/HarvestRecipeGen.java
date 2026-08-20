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

		HarvestRecipeBuilder.builder()
				.input(Blocks.REDSTONE_ORE)
				.result(Items.REDSTONE, 8, 1)
				.save(consumer, save("harvest/redstone_ore"));

		HarvestRecipeBuilder.builder()
				.input(Blocks.DEEPSLATE_REDSTONE_ORE)
				.result(Items.REDSTONE, 8, 1)
				.save(consumer, save("harvest/deep_redstone_ore"));

		HarvestRecipeBuilder.builder()
				.input(Blocks.COAL_ORE)
				.result(Items.COAL, 4, 1)
				.save(consumer, save("harvest/coal_ore"));

		HarvestRecipeBuilder.builder()
				.input(Blocks.DEEPSLATE_COAL_ORE)
				.result(Items.COAL, 4, 1)
				.save(consumer, save("harvest/deep_coal_ore"));

		HarvestRecipeBuilder.builder()
				.input(Blocks.EMERALD_ORE)
				.result(Items.EMERALD, 4, 1)
				.save(consumer, save("harvest/emerald_ore"));

		HarvestRecipeBuilder.builder()
				.input(Blocks.DEEPSLATE_EMERALD_ORE)
				.result(Items.EMERALD, 4, 1)
				.save(consumer, save("harvest/deep_emerald_ore"));

		HarvestRecipeBuilder.builder()
				.input(Blocks.IRON_ORE)
				.result(Items.RAW_IRON, 4, 1)
				.save(consumer, save("harvest/iron_ore"));

		HarvestRecipeBuilder.builder()
				.input(Blocks.DEEPSLATE_IRON_ORE)
				.result(Items.RAW_IRON, 4, 1)
				.save(consumer, save("harvest/deep_iron_ore"));

		HarvestRecipeBuilder.builder()
				.input(Blocks.COPPER_ORE)
				.result(Items.RAW_COPPER, 8, 1)
				.save(consumer, save("harvest/copper_ore"));

		HarvestRecipeBuilder.builder()
				.input(Blocks.DEEPSLATE_COPPER_ORE)
				.result(Items.RAW_COPPER, 8, 1)
				.save(consumer, save("harvest/deep_copper_ore"));

		HarvestRecipeBuilder.builder()
				.input(Blocks.GOLD_ORE)
				.result(Items.RAW_GOLD, 4, 1)
				.save(consumer, save("harvest/gold_ore"));

		HarvestRecipeBuilder.builder()
				.input(Blocks.DEEPSLATE_GOLD_ORE)
				.result(Items.RAW_GOLD, 4, 1)
				.save(consumer, save("harvest/deep_gold_ore"));

		HarvestRecipeBuilder.builder()
				.input(Blocks.LAPIS_ORE)
				.result(Items.LAPIS_LAZULI, 16, 1)
				.save(consumer, save("harvest/lapis_ore"));

		HarvestRecipeBuilder.builder()
				.input(Blocks.DEEPSLATE_LAPIS_ORE)
				.result(Items.LAPIS_LAZULI, 16, 1)
				.save(consumer, save("harvest/deep_lapis_ore"));

		HarvestRecipeBuilder.builder()
				.input(Blocks.DIAMOND_ORE)
				.result(Items.DIAMOND, 4, 1)
				.save(consumer, save("harvest/diamond_ore"));

		HarvestRecipeBuilder.builder()
				.input(Blocks.DEEPSLATE_DIAMOND_ORE)
				.result(Items.DIAMOND, 4, 1)
				.save(consumer, save("harvest/deep_diamond_ore"));

		HarvestRecipeBuilder.builder()
				.input(Blocks.NETHER_QUARTZ_ORE)
				.result(Items.QUARTZ, 4, 1)
				.save(consumer, save("harvest/quzrtz_ore"));

		HarvestRecipeBuilder.builder()
				.input(Blocks.NETHER_GOLD_ORE)
				.result(Items.RAW_GOLD, 1, 1)
				.save(consumer, save("harvest/nether_gold_ore"));
	}
}