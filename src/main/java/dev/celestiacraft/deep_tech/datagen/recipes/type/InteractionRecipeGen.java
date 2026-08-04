package dev.celestiacraft.deep_tech.datagen.recipes.type;

import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.api.recipe.builder.interaction.InteractionRecipeBuilder;
import dev.celestiacraft.deep_tech.common.register.DTMaterials;
import dev.celestiacraft.deep_tech.common.register.item.MaterialItems;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Consumer;

public class InteractionRecipeGen {
	public static void register(Consumer<FinishedRecipe> consumer) {
		InteractionRecipeBuilder.builder()
				.trigger(Ingredient.of(Items.IRON_INGOT))
				.target(Blocks.REINFORCED_DEEPSLATE.defaultBlockState())
				.result(new ItemStack(Items.IRON_NUGGET, 8), 25)
				.result(new ItemStack(DTMaterials.IRON.getDust().get()), 25)
				.result(new ItemStack(DTMaterials.IRON.getPlate().get()), 50)
				.extraEffect(0.1f, Blocks.DEEPSLATE.defaultBlockState(),
						new ItemStack(MaterialItems.SCULK_BONEMEAL.get()))
				.consume(false)
				.save(consumer, DeepTech.loadResource("interaction/iron_plate_craft"));
	}
}