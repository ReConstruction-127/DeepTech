package dev.celestiacraft.deep_tech.datagen.recipes.type;

import dev.celestiacraft.deep_tech.api.recipe.builder.assembling.AssemblingRecipeBuilder;
import dev.celestiacraft.deep_tech.common.register.DTFluids;
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

	public static void register(Consumer<FinishedRecipe> consumer) {
		// 高级幽匿控制电路: 8 幽匿脉络 + 1 回响碎片 + 4 致密幽匿碎块 + 4 红石粉
		//   + 1 幽匿合金板 + 1 幽匿之骨 + 10mB 幽匿培养液, 催化剂 幽匿电路(不消耗)
		//   -> 1 高级幽匿控制电路 + 10mB 水
		AssemblingRecipeBuilder.builder()
				.itemInput(Items.SCULK_VEIN, 8)
				.itemInput(Items.ECHO_SHARD, 1)
				.itemInput(MaterialItems.DENSE_SCULK_CHUNK, 4)
				.itemInput(Items.REDSTONE, 4)
				.itemInput(MaterialItems.SCULK_ALLOY_PLATE, 1)
				.itemInput(MaterialItems.SCULK_BONE, 1)
				.fluidInput(DTFluids.SCULK_CULTURE.getSource(), 10)
				.catalyst(MaterialItems.SCULK_CIRCUIT)
				.itemOutput(MaterialItems.ADVANCED_SCULK_CONTROL_CIRCUIT)
				.fluidOutput(Fluids.WATER, 10)
				.save(consumer, save("assembling/advanced_sculk_control_circuit"));
	}
}