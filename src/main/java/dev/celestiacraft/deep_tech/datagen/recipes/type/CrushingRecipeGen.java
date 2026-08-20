package dev.celestiacraft.deep_tech.datagen.recipes.type;

import dev.celestiacraft.deep_tech.api.recipe.builder.crushing.CrushingRecipeBuilder;
import dev.celestiacraft.deep_tech.common.register.DTMaterials;
import dev.celestiacraft.deep_tech.common.register.item.MaterialItems;
import dev.celestiacraft.deep_tech.datagen.recipes.DTRecipeProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class CrushingRecipeGen extends DTRecipeProvider {
	public CrushingRecipeGen(PackOutput output) {
		super(output);
	}

	public static void register(Consumer<FinishedRecipe> consumer) {
		addRawOreCrushing(consumer);
		addBlockCrushing(consumer);

		CrushingRecipeBuilder.builder()
				.input(MaterialItems.SCULK_BONE)
				.output(MaterialItems.SCULK_BONEMEAL)
				.energyCost(20)
				.processingTime(100)
				.save(consumer, save("crushing/sculk_bonemeal"));
	}

	private static void addBlockCrushing(Consumer<FinishedRecipe> consumer) {
		CrushingRecipeBuilder.builder()
				.input(Tags.Items.COBBLESTONE)
				.output(Blocks.GRAVEL)
				.energyCost(20)
				.processingTime(100)
				.save(consumer, save("crushing/block/gravel"));

		CrushingRecipeBuilder.builder()
				.input(Blocks.STONE)
				.output(Blocks.COBBLESTONE)
				.energyCost(20)
				.processingTime(100)
				.save(consumer, save("crushing/block/cobblestone"));

		CrushingRecipeBuilder.builder()
				.input(Blocks.GRAVEL)
				.output(Blocks.SAND)
				.energyCost(20)
				.processingTime(100)
				.save(consumer, save("crushing/block/sand"));

		CrushingRecipeBuilder.builder()
				.input(Items.WITHER_ROSE)
				.output(MaterialItems.ALKALOID_POWDER, 16)
				.energyCost(20)
				.processingTime(100)
				.save(consumer, save("crushing/alkaloid_powder1"));

		CrushingRecipeBuilder.builder()
				.input(Items.LILY_OF_THE_VALLEY)
				.output(MaterialItems.ALKALOID_POWDER, 8)
				.energyCost(20)
				.processingTime(100)
				.save(consumer, save("crushing/alkaloid_powder2"));

		CrushingRecipeBuilder.builder()
				.input(Items.ALLIUM)
				.output(MaterialItems.ALKALOID_POWDER, 8)
				.energyCost(20)
				.processingTime(100)
				.save(consumer, save("crushing/alkaloid_powder3"));

		CrushingRecipeBuilder.builder()
				.input(Items.BLUE_ORCHID)
				.output(MaterialItems.ALKALOID_POWDER, 8)
				.energyCost(20)
				.processingTime(100)
				.save(consumer, save("crushing/alkaloid_powder4"));

		CrushingRecipeBuilder.builder()
				.input(Items.SUNFLOWER)
				.output(MaterialItems.ALKALOID_POWDER, 1)
				.energyCost(20)
				.processingTime(100)
				.save(consumer, save("crushing/alkaloid_powder5"));

		CrushingRecipeBuilder.builder()
				.input(Items.LILAC)
				.output(MaterialItems.ALKALOID_POWDER, 1)
				.energyCost(20)
				.processingTime(100)
				.save(consumer, save("crushing/alkaloid_powder6"));

		CrushingRecipeBuilder.builder()
				.input(Items.ROSE_BUSH)
				.output(MaterialItems.ALKALOID_POWDER, 1)
				.energyCost(20)
				.processingTime(100)
				.save(consumer, save("crushing/alkaloid_powder7"));

		CrushingRecipeBuilder.builder()
				.input(Items.PEONY)
				.output(MaterialItems.ALKALOID_POWDER, 1)
				.energyCost(20)
				.processingTime(100)
				.save(consumer, save("crushing/alkaloid_powder8"));
	}

	private static void addRawOreCrushing(Consumer<FinishedRecipe> consumer) {
		CrushingRecipeBuilder.builder()
				.input(Tags.Items.RAW_MATERIALS_COPPER)
				.output(DTMaterials.COPPER.getNugget(), 12)
				.energyCost(30)
				.processingTime(150)
				.save(consumer, save("crushing/raw_ore/copper"));

		CrushingRecipeBuilder.builder()
				.input(Tags.Items.RAW_MATERIALS_IRON)
				.output(Items.IRON_NUGGET, 12)
				.energyCost(30)
				.processingTime(150)
				.save(consumer, save("crushing/raw_ore/iron"));

		CrushingRecipeBuilder.builder()
				.input(Tags.Items.RAW_MATERIALS_GOLD)
				.output(Items.GOLD_NUGGET, 12)
				.energyCost(30)
				.processingTime(150)
				.save(consumer, save("crushing/raw_ore/gold"));
	}
}