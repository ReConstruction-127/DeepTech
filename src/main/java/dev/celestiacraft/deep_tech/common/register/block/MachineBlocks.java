package dev.celestiacraft.deep_tech.common.register.block;

import com.tterrag.registrate.util.entry.BlockEntry;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.api.client.model.ItemModelGen;
import dev.celestiacraft.deep_tech.common.block.machine.alloy_furnace.AlloyFurnaceBlock;
import dev.celestiacraft.deep_tech.common.block.machine.crusher.CrusherBlock;
import dev.celestiacraft.deep_tech.common.block.machine.energy_cell.EnergyCellBlock;
import dev.celestiacraft.deep_tech.common.block.machine.exp_generator.EXPGeneratorBlock;
import dev.celestiacraft.deep_tech.common.block.machine.furnace.SculkFurnaceBlock;
import dev.celestiacraft.deep_tech.common.block.machine.resonance_node.ResonanceNodeBlock;
import dev.celestiacraft.deep_tech.common.register.DTCreativeTabs;
import dev.celestiacraft.deep_tech.tags.DeepTechBlockTags;
import dev.celestiacraft.deep_tech.tags.DeepTechItemTags;

public class MachineBlocks {
	public static final BlockEntry<CrusherBlock> CRUSHER;
	public static final BlockEntry<SculkFurnaceBlock> SCULK_FURNACE;
	public static final BlockEntry<EXPGeneratorBlock> EXP_GENERATOR;
	public static final BlockEntry<ResonanceNodeBlock> RESONANCE_NODE;
	public static final BlockEntry<AlloyFurnaceBlock> ALLOY_FURNACE;
	public static final BlockEntry<EnergyCellBlock> ENERGY_CELL;

	static {
		DTCreativeTabs.getTab("machine");

		CRUSHER = DeepTech.REGISTRATE.block("crusher", CrusherBlock::new)
				.blockstate(CrusherBlock.genBlockState())
				.tag(DeepTechBlockTags.MACHINES)
				.item()
				.tag(DeepTechItemTags.MACHINES)
				.model(ItemModelGen.withModel("block/machine/crusher/on"))
				.build()
				.register();

		SCULK_FURNACE = DeepTech.REGISTRATE.block("sculk_furnace", SculkFurnaceBlock::new)
				.blockstate(SculkFurnaceBlock.genBlockState())
				.tag(DeepTechBlockTags.MACHINES)
				.item()
				.tag(DeepTechItemTags.MACHINES)
				.model(ItemModelGen.withModel("block/machine/furnace/on"))
				.build()
				.register();

		EXP_GENERATOR = DeepTech.REGISTRATE.block("exp_generator", EXPGeneratorBlock::new)
				.blockstate(EXPGeneratorBlock.genBlockState())
				.tag(DeepTechBlockTags.MACHINES)
				.item()
				.tag(DeepTechItemTags.MACHINES)
				.model(ItemModelGen.withModel("block/machine/exp_generator/on"))
				.build()
				.register();

		ALLOY_FURNACE = DeepTech.REGISTRATE.block("alloy_furnace", AlloyFurnaceBlock::new)
				.blockstate(AlloyFurnaceBlock.genBlockState())
				.tag(DeepTechBlockTags.MACHINES)
				.item()
				.tag(DeepTechItemTags.MACHINES)
				.model(ItemModelGen.withModel("block/machine/alloy_furnace/on"))
				.build()
				.register();

		RESONANCE_NODE = DeepTech.REGISTRATE.block("resonance_node", ResonanceNodeBlock::new)
				.blockstate(ResonanceNodeBlock.genBlockState())
				.tag(DeepTechBlockTags.MACHINES)
				.item()
				.tag(DeepTechItemTags.MACHINES)
				.model(ItemModelGen.withModel("block/machine/resonance_node"))
				.build()
				.register();

		ENERGY_CELL = DeepTech.REGISTRATE.block("energy_cell", EnergyCellBlock::new)
				.blockstate(EnergyCellBlock.genBlockState())
				.tag(DeepTechBlockTags.MACHINES)
				.item()
				.tag(DeepTechItemTags.MACHINES)
				.model(ItemModelGen.withModel("block/machine/energy_cell"))
				.build()
				.register();
	}

	public static void register() {
		DeepTech.registerLog("Machine Blocks");
	}
}