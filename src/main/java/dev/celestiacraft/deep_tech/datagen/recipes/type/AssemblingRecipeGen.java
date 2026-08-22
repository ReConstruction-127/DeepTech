package dev.celestiacraft.deep_tech.datagen.recipes.type;

import dev.celestiacraft.deep_tech.api.recipe.builder.assembling.AssemblingRecipeBuilder;
import dev.celestiacraft.deep_tech.common.register.DTFluids;
import dev.celestiacraft.deep_tech.common.register.DTMaterials;
import dev.celestiacraft.deep_tech.common.register.item.MaterialItems;
import dev.celestiacraft.deep_tech.datagen.recipes.DTRecipeProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;

import java.util.function.Consumer;

public class AssemblingRecipeGen extends DTRecipeProvider {
	public AssemblingRecipeGen(PackOutput output) {
		super(output);
	}

	public static void addRecipes(Consumer<FinishedRecipe> consumer) {
		AssemblingRecipeBuilder.builder()
				.itemInput(Items.SCULK_VEIN, 8)
				.itemInput(Items.ECHO_SHARD, 1)
				.itemInput(MaterialItems.DENSE_SCULK_CHUNK, 4)
				.itemInput(Items.REDSTONE, 4)
				.itemInput(DTMaterials.SCULK_ALLOY.getPlate(), 1)
				.itemInput(MaterialItems.SCULK_BONE, 1)
				.fluidInput(DTFluids.SCULK_CULTURE.getSource(), 10)
				.catalyst(MaterialItems.SCULK_CIRCUIT)
				.itemOutput(MaterialItems.ADVANCED_SCULK_CONTROL_CIRCUIT)
				.fluidOutput(Fluids.WATER, 10)
				.save(consumer, save("assembling/advanced_sculk_control_circuit"));

		AssemblingRecipeBuilder.builder()
				.itemInput(Items.SCULK_VEIN, 1)
				.itemInput(Items.REDSTONE, 1)
				.itemInput(DTMaterials.GOLD.getPlate(), 1)
				.itemInput(MaterialItems.SCULK_BONE, 1)
				.catalyst(MaterialItems.SCULK_CIRCUIT)
				.itemOutput(MaterialItems.SCULK_CIRCUIT)
				.save(consumer, save("assembling/sculk_circuit"));
	}
}