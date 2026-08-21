package dev.celestiacraft.deep_tech.datagen.recipes.type;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import dev.celestiacraft.deep_tech.common.register.DTMaterials;
import dev.celestiacraft.deep_tech.common.register.block.BasicBlocks;
import dev.celestiacraft.deep_tech.common.register.block.FrameBlocks;
import dev.celestiacraft.deep_tech.common.register.block.MachineBlocks;
import dev.celestiacraft.deep_tech.common.register.item.MaterialItems;
import dev.celestiacraft.deep_tech.common.register.item.ToolItems;
import dev.celestiacraft.deep_tech.datagen.recipes.DTRecipeProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class CraftingRecipeGen extends DTRecipeProvider {
	public CraftingRecipeGen(PackOutput output) {
		super(output);
	}

	public static void addRecipes(Consumer<FinishedRecipe> consumer) {
		addShapedRecipe(consumer);
		addCompatRecipe(consumer);
	}

	private static void addShapedRecipe(Consumer<FinishedRecipe> consumer) {
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, FrameBlocks.MACHINE_FRAME.get(), 2)
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
				.pattern("BAB")
				.pattern("BCB")
				.pattern("BDB")
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
				.define('B', Items.COPPER_BLOCK)
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

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.SCULK_VEIN, 4)
				.pattern("AA")
				.define('A', MaterialItems.SCULK_CHUNK)
				.unlockedBy("crafting_table", has(Items.CRAFTING_TABLE))
				.save(consumer, save("shaped/sculk_vein"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BasicBlocks.SCULK_NETWORK_VEIN, 16)
				.pattern("AAA")
				.pattern("ABA")
				.pattern("AAA")
				.define('A', Items.SCULK_VEIN)
				.define('B', MaterialItems.SCULK_ALLOY)
				.unlockedBy("crafting_table", has(Items.CRAFTING_TABLE))
				.save(consumer, save("shaped/sn/vein"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BasicBlocks.SCULK_NETWORK_BLOCK, 8)
				.pattern("ACA")
				.pattern("CBC")
				.pattern("ACA")
				.define('A', Items.SCULK)
				.define('B', MaterialItems.SCULK_ALLOY)
				.define('C', MaterialItems.SCULK_BONE)
				.unlockedBy("crafting_table", has(Items.CRAFTING_TABLE))
				.save(consumer, save("shaped/sn/block"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.SN_CENTER)
				.pattern(" A ")
				.pattern("CBC")
				.pattern(" D ")
				.define('A', Items.SCULK)
				.define('B', FrameBlocks.REINFORCED_MACHINE_FRAME)
				.define('C', MaterialItems.ADVANCED_SCULK_CONTROL_CIRCUIT)
				.define('D', BasicBlocks.SCULK_NETWORK_BLOCK)
				.unlockedBy("crafting_table", has(Items.CRAFTING_TABLE))
				.save(consumer, save("shaped/sn/center"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.SN_ACCESSOR)
				.pattern("A")
				.pattern("B")
				.pattern("D")
				.define('A', Items.SCULK_SENSOR)
				.define('B', FrameBlocks.REINFORCED_MACHINE_FRAME)
				.define('D', BasicBlocks.SCULK_NETWORK_BLOCK)
				.unlockedBy("crafting_table", has(Items.CRAFTING_TABLE))
				.save(consumer, save("shaped/sn/accessor"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.SN_FLUID_INPUT_PORT)
				.pattern("A ")
				.pattern("BC")
				.define('A', MaterialItems.SCULK_ALLOY)
				.define('B', MaterialItems.SCULK_BONE)
				.define('C', BasicBlocks.SCULK_NETWORK_VEIN)
				.unlockedBy("crafting_table", has(Items.CRAFTING_TABLE))
				.save(consumer, save("shaped/sn/fluid_i"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MachineBlocks.SN_FLUID_INPUT_PORT)
				.requires(MachineBlocks.SN_FLUID_OUTPUT_PORT)
				.unlockedBy("crafting_table", has(Items.CRAFTING_TABLE))
				.save(consumer, save("shaped/sn/fluid_i2"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.SN_FLUID_OUTPUT_PORT)
				.pattern(" A")
				.pattern("CB")
				.define('A', MaterialItems.SCULK_ALLOY)
				.define('B', MaterialItems.SCULK_BONE)
				.define('C', BasicBlocks.SCULK_NETWORK_VEIN)
				.unlockedBy("crafting_table", has(Items.CRAFTING_TABLE))
				.save(consumer, save("shaped/sn/fluid_o"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MachineBlocks.SN_FLUID_OUTPUT_PORT)
				.requires(MachineBlocks.SN_FLUID_INPUT_PORT)
				.unlockedBy("crafting_table", has(Items.CRAFTING_TABLE))
				.save(consumer, save("shaped/sn/fluid_o2"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.SN_ITEM_INPUT_PORT)
				.pattern("A ")
				.pattern("BC")
				.define('A', MaterialItems.SCULK_STEEL)
				.define('B', MaterialItems.SCULK_BONE)
				.define('C', BasicBlocks.SCULK_NETWORK_VEIN)
				.unlockedBy("crafting_table", has(Items.CRAFTING_TABLE))
				.save(consumer, save("shaped/sn/item_i"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MachineBlocks.SN_ITEM_INPUT_PORT)
				.requires(MachineBlocks.SN_ITEM_OUTPUT_PORT)
				.unlockedBy("crafting_table", has(Items.CRAFTING_TABLE))
				.save(consumer, save("shaped/sn/item_i2"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.SN_ITEM_OUTPUT_PORT)
				.pattern(" A")
				.pattern("CB")
				.define('A', MaterialItems.SCULK_STEEL)
				.define('B', MaterialItems.SCULK_BONE)
				.define('C', BasicBlocks.SCULK_NETWORK_VEIN)
				.unlockedBy("crafting_table", has(Items.CRAFTING_TABLE))
				.save(consumer, save("shaped/sn/item_o"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MachineBlocks.SN_ITEM_OUTPUT_PORT)
				.requires(MachineBlocks.SN_ITEM_INPUT_PORT)
				.unlockedBy("crafting_table", has(Items.CRAFTING_TABLE))
				.save(consumer, save("shaped/sn/item_o2"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.SN_ITEM_RESERVOIR)
				.pattern("A")
				.pattern("B")
				.pattern("C")
				.define('A', MaterialItems.SCULK_STEEL)
				.define('B', FrameBlocks.REINFORCED_MACHINE_FRAME)
				.define('C', BasicBlocks.SCULK_NETWORK_BLOCK)
				.unlockedBy("crafting_table", has(Items.CRAFTING_TABLE))
				.save(consumer, save("shaped/sn/item_r"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.SN_FLUID_RESERVOIR)
				.pattern("A")
				.pattern("B")
				.pattern("C")
				.define('A', MaterialItems.SCULK_ALLOY)
				.define('B', FrameBlocks.REINFORCED_MACHINE_FRAME)
				.define('C', BasicBlocks.SCULK_NETWORK_BLOCK)
				.unlockedBy("crafting_table", has(Items.CRAFTING_TABLE))
				.save(consumer, save("shaped/sn/fluid_r"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.SCULK_COLLECTOR)
				.pattern("A")
				.pattern("B")
				.pattern("C")
				.define('A', MaterialItems.ADVANCED_SCULK_CONTROL_CIRCUIT)
				.define('B', FrameBlocks.REINFORCED_MACHINE_FRAME)
				.define('C', Items.DIAMOND_PICKAXE)
				.unlockedBy("crafting_table", has(Items.CRAFTING_TABLE))
				.save(consumer, save("shaped/collector"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.ASSEMBLER)
				.pattern("AAA")
				.pattern("CBC")
				.pattern("DED")
				.define('A', MaterialItems.SCULK_CIRCUIT)
				.define('B', FrameBlocks.REINFORCED_MACHINE_FRAME)
				.define('C', Items.PISTON)
				.define('D', Items.ECHO_SHARD)
				.define('E', Items.DIAMOND)
				.unlockedBy("crafting_table", has(Items.CRAFTING_TABLE))
				.save(consumer, save("shaped/asm"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.SCULK_NURSERY)
				.pattern("AAA")
				.pattern("CBC")
				.pattern("CCC")
				.define('A', MaterialItems.SCULK_CIRCUIT)
				.define('B', FrameBlocks.REINFORCED_MACHINE_FRAME)
				.define('C', Items.GLASS)
				.unlockedBy("crafting_table", has(Items.CRAFTING_TABLE))
				.save(consumer, save("shaped/nsy"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.PROCESSOR)
				.pattern("AAA")
				.pattern("CBC")
				.pattern("DDD")
				.define('A', MaterialItems.SCULK_CIRCUIT)
				.define('B', FrameBlocks.REINFORCED_MACHINE_FRAME)
				.define('C', MaterialItems.DENSE_SCULK_CHUNK)
				.define('D', MaterialItems.SCULK_ALLOY)
				.unlockedBy("crafting_table", has(Items.CRAFTING_TABLE))
				.save(consumer, save("shaped/processor"));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, FrameBlocks.REINFORCED_MACHINE_FRAME, 2)
				.pattern("ABA")
				.pattern("BCB")
				.pattern("ABA")
				.define('A', MaterialItems.SCULK_BONE)
				.define('B', MaterialItems.SCULK_STEEL)
				.define('C', MaterialItems.SCULK_CHUNK)
				.unlockedBy("crafting_table", has(Items.CRAFTING_TABLE))
				.save(consumer, save("shaped/frame_reinforced"));

	}

	private static void addCompatRecipe(Consumer<FinishedRecipe> consumer) {
		addModCompatRecipe(Create.ID, "exp_generator_from_create", consumer, (recipe) -> {
			ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MachineBlocks.EXP_GENERATOR.get())
					.pattern("BAB")
					.pattern("BCB")
					.pattern("BDB")
					.define('A', MaterialItems.SCULK_CIRCUIT)
					.define('B', MaterialItems.SCULK_CHUNK.get())
					.define('C', FrameBlocks.MACHINE_FRAME)
					.define('D', AllBlocks.BASIN)
					.unlockedBy("crafting_table", has(Items.CRAFTING_TABLE))
					.save(recipe, save("compat/create/exp_generator_from_create"));
		});
	}
}