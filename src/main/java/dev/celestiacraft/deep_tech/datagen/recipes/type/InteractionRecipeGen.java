package dev.celestiacraft.deep_tech.datagen.recipes.type;

import dev.celestiacraft.deep_tech.api.recipe.builder.interaction.InteractionRecipeBuilder;
import dev.celestiacraft.deep_tech.common.register.DTMaterials;
import dev.celestiacraft.deep_tech.common.register.item.MaterialItems;
import dev.celestiacraft.deep_tech.common.recipe.interaction.InteractionRecipe;
import dev.celestiacraft.deep_tech.datagen.recipes.DTRecipeProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Consumer;

public class InteractionRecipeGen extends DTRecipeProvider {
	// 构造函数（如果需要）
	public InteractionRecipeGen(PackOutput output) {
		super(output);
	}

	// 静态注册方法（与 CrushingRecipeGen 保持一致）
	public static void register(Consumer<FinishedRecipe> consumer) {
		// 压板：左键
		InteractionRecipeBuilder.builder()
				.trigger(Items.IRON_INGOT)
				.target(Blocks.REINFORCED_DEEPSLATE)
				.result(Items.IRON_NUGGET, 8, 25)
				.result(DTMaterials.IRON.getDust().get(), 25)
				.result(DTMaterials.IRON.getPlate().get(), 50)
				.extraEffect(0.1f, Blocks.DEEPSLATE, MaterialItems.SCULK_BONEMEAL.get())
				.consume(true)
				.type(InteractionRecipe.InteractionType.LEFT_CLICK)
				.save(consumer, save("interaction/iron_plate_craft"));

		InteractionRecipeBuilder.builder()
				.trigger(Items.COPPER_INGOT)
				.target(Blocks.REINFORCED_DEEPSLATE)
				.result(DTMaterials.COPPER.getNugget().get(), 8, 25)
				.result(DTMaterials.COPPER.getDust().get(), 25)
				.result(DTMaterials.COPPER.getPlate().get(), 50)
				.extraEffect(0.1f, Blocks.DEEPSLATE, MaterialItems.SCULK_BONEMEAL.get())
				.consume(true)
				.type(InteractionRecipe.InteractionType.LEFT_CLICK)
				.save(consumer, save("interaction/copper_plate_craft"));

		InteractionRecipeBuilder.builder()
				.trigger(Items.GOLD_INGOT)
				.target(Blocks.REINFORCED_DEEPSLATE)
				.result(Items.GOLD_NUGGET, 8, 25)
				.result(DTMaterials.GOLD.getDust().get(), 25)
				.result(DTMaterials.GOLD.getPlate().get(), 50)
				.extraEffect(0.1f, Blocks.DEEPSLATE, MaterialItems.SCULK_BONEMEAL.get())
				.consume(true)
				.type(InteractionRecipe.InteractionType.LEFT_CLICK)
				.save(consumer, save("interaction/gold_plate_craft"));

		// 强化深板岩修复：右键
		InteractionRecipeBuilder.builder()
				.trigger(MaterialItems.SCULK_BONE)
				.target(Blocks.DEEPSLATE)
				.result(Items.AIR, 1)
				.extraEffect(1.0f, Blocks.REINFORCED_DEEPSLATE)
				.consume(true)
				.type(InteractionRecipe.InteractionType.RIGHT_CLICK)
				.save(consumer, save("interaction/sculk_bone_repair"));

		// TEST：右键
		InteractionRecipeBuilder.builder()
				.trigger(Items.COPPER_INGOT)
				.target(Blocks.DIAMOND_BLOCK)
				.result(Items.AIR, 1)
				.extraEffect(1.0f, Blocks.EMERALD_BLOCK)
				.consume(false)
				.type(InteractionRecipe.InteractionType.RIGHT_CLICK)
				.save(consumer, save("interaction/test_only"));

	}
}