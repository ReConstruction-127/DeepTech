package dev.celestiacraft.deep_tech.datagen.recipes.type;

import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.api.block.FrameBlock;
import dev.celestiacraft.deep_tech.api.recipe.builder.crushing.CrushingRecipeBuilder;
import dev.celestiacraft.deep_tech.common.register.DTBlocks;
import dev.celestiacraft.deep_tech.common.register.DTMaterials;
import dev.celestiacraft.deep_tech.common.register.block.FrameBlocks;
import dev.celestiacraft.deep_tech.common.register.block.MachineBlocks;
import dev.celestiacraft.deep_tech.common.register.item.MaterialItems;
import dev.celestiacraft.deep_tech.common.register.item.ToolItems;
import dev.celestiacraft.deep_tech.datagen.recipes.DTRecipeProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import slimeknights.mantle.registration.adapter.BlockRegistryAdapter;

import javax.tools.Tool;
import java.util.function.Consumer;



public class CraftingRecipeGen extends DTRecipeProvider {
	public CraftingRecipeGen(PackOutput output) {
		super(output);
	}

	public static void register(Consumer<FinishedRecipe> consumer) {
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, FrameBlocks.MACHINE_FRAME.get())
				.pattern("ABA")
				.pattern("BCB")
				.pattern("ABA")
				.define('A', MaterialItems.SCULK_BONE)
				.define('B', Items.POLISHED_DEEPSLATE)
				.define('C', MaterialItems.SCULK_CHUNK)
				.unlockedBy("crafting_table", has(Items.CRAFTING_TABLE))
				.save(consumer, save("shaped/machine/basic_frame"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.CRUSHER.get())
				.pattern(" A ")
				.pattern("BCB")
				.pattern(" D ")
				.define('A', MaterialItems.SCULK_CIRCUIT)
				.define('B', DTMaterials.IRON.getPlate().get())
				.define('C', FrameBlocks.MACHINE_FRAME)
				.define('D', Items.PISTON)
				.unlockedBy("crafting_table", has(Items.CRAFTING_TABLE))
				.save(consumer, save("shaped/machine/crusher"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.SCULK_FURNACE.get())
				.pattern(" A ")
				.pattern("BCB")
				.pattern(" D ")
				.define('A', MaterialItems.SCULK_CIRCUIT)
				.define('B', Items.COPPER_INGOT)
				.define('C', FrameBlocks.MACHINE_FRAME)
				.define('D', Items.FURNACE)
				.unlockedBy("crafting_table", has(Items.CRAFTING_TABLE))
				.save(consumer, save("shaped/machine/sculk_furnace"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.EXP_GENERATOR.get())
				.pattern("BAB")
				.pattern("BCB")
				.pattern("BDB")
				.define('A', MaterialItems.SCULK_CIRCUIT)
				.define('B', MaterialItems.SCULK_CHUNK.get())
				.define('C', FrameBlocks.MACHINE_FRAME)
				.define('D', Items.CAULDRON)
				.unlockedBy("crafting_table", has(Items.CRAFTING_TABLE))
				.save(consumer, save("shaped/machine/exp_generator"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.ALLOY_FURNACE.get())
				.pattern("BAB")
				.pattern("BCB")
				.pattern("BDB")
				.define('A', MaterialItems.SCULK_CIRCUIT)
				.define('B', DTMaterials.COPPER.getPlate().get())
				.define('C', FrameBlocks.MACHINE_FRAME)
				.define('D', Items.BLAST_FURNACE)
				.unlockedBy("crafting_table", has(Items.CRAFTING_TABLE))
				.save(consumer, save("shaped/machine/alloy_furnace"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.ENERGY_CELL.get())
				.pattern(" A ")
				.pattern("BCB")
				.pattern(" D ")
				.define('A', MaterialItems.SCULK_CIRCUIT)
				.define('B', MaterialItems.SCULK_ALLOY)
				.define('C', FrameBlocks.MACHINE_FRAME)
				.define('D', Items.SCULK)
				.unlockedBy("crafting_table", has(Items.CRAFTING_TABLE))
				.save(consumer, save("shaped/machine/energy_cell"));


		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.RESONANCE_NODE.get())
				.pattern(" A ")
				.pattern("BCB")
				.define('A', Items.AMETHYST_SHARD)
				.define('B', MaterialItems.SCULK_BONE)
				.define('C', MaterialItems.SCULK_CHUNK)
				.unlockedBy("crafting_table", has(Items.CRAFTING_TABLE))
				.save(consumer, save("shaped/machine/resonance_node"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MaterialItems.SCULK_CIRCUIT.get())
				.pattern(" A ")
				.pattern("BCB")
				.pattern(" D ")
				.define('A', MaterialItems.SCULK_BONE)
				.define('B', Items.REDSTONE)
				.define('C', MaterialItems.SCULK_CHUNK)
				.define('D', DTMaterials.GOLD.getPlate().get())
				.unlockedBy("crafting_table", has(Items.CRAFTING_TABLE))
				.save(consumer, save("shaped/basic_circuit"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ToolItems.WRENCH.get())
				.pattern(" A ")
				.pattern(" AA")
				.pattern("A  ")
				.define('A', MaterialItems.SCULK_ALLOY)
				.unlockedBy("crafting_table", has(Items.CRAFTING_TABLE))
				.save(consumer, save("shaped/wrench"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.SCULK)
				.pattern("AA")
				.pattern("AA")
				.define('A', MaterialItems.SCULK_CHUNK)
				.unlockedBy("crafting_table", has(Items.CRAFTING_TABLE))
				.save(consumer, save("shaped/sculk_block"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.SCULK_CATALYST)
				.pattern(" A ")
				.pattern("ABA")
				.pattern(" A ")
				.define('A', MaterialItems.SCULK_BONE)
				.define('B', MaterialItems.SCULK_CHUNK)
				.unlockedBy("crafting_table", has(Items.CRAFTING_TABLE))
				.save(consumer, save("shaped/sculk_catalyst"));


//		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MachineBlocks.RESONANCE_NODE.get())
//
//				.unlockedBy("crafting_table", has(Items.CRAFTING_TABLE))
//
//				.save(consumer, save("shaped/machine/resonance_node"));
	}
}