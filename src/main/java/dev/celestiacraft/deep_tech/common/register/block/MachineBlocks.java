package dev.celestiacraft.deep_tech.common.register.block;

import com.tterrag.registrate.util.entry.BlockEntry;
import dev.celestiacraft.deep_tech.DeepTech;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.deep_tech.api.client.model.ItemModelGen;
import dev.celestiacraft.deep_tech.common.block.machine.alloy_furnace.AlloyFurnaceBlock;
import dev.celestiacraft.deep_tech.common.block.machine.crusher.CrusherBlock;
import dev.celestiacraft.deep_tech.common.block.machine.collector.SculkCollectorBlock;
import dev.celestiacraft.deep_tech.common.block.machine.energy_cell.EnergyCellBlock;
import dev.celestiacraft.deep_tech.common.block.machine.exp_generator.EXPGeneratorBlock;
import dev.celestiacraft.deep_tech.common.block.machine.furnace.SculkFurnaceBlock;
import dev.celestiacraft.deep_tech.common.block.machine.resonance_node.ResonanceNodeBlock;
import dev.celestiacraft.deep_tech.common.block.machine.sculk_network.accessor.SNAccessorBlock;
import dev.celestiacraft.deep_tech.common.block.machine.sculk_network.center.SNCenterBlock;
import dev.celestiacraft.deep_tech.common.block.machine.sculk_network.port.SNFluidInputPortBlock;
import dev.celestiacraft.deep_tech.common.block.machine.sculk_network.port.SNFluidOutputPortBlock;
import dev.celestiacraft.deep_tech.common.block.machine.sculk_network.port.SNItemInputPortBlock;
import dev.celestiacraft.deep_tech.common.block.machine.sculk_network.port.SNItemOutputPortBlock;
import dev.celestiacraft.deep_tech.common.block.machine.sculk_network.reservoir.SNFluidReservoirBlock;
import dev.celestiacraft.deep_tech.common.block.machine.sculk_network.reservoir.SNItemReservoirBlock;
import dev.celestiacraft.deep_tech.common.block.machine.sculk_nursery.SculkNurseryBlock;
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
	public static final BlockEntry<SNCenterBlock> SN_CENTER;
	public static final BlockEntry<SNItemReservoirBlock> SN_ITEM_RESERVOIR;
	public static final BlockEntry<SNItemInputPortBlock> SN_ITEM_INPUT_PORT;
	public static final BlockEntry<SNItemOutputPortBlock> SN_ITEM_OUTPUT_PORT;
	public static final BlockEntry<SNAccessorBlock> SN_ACCESSOR;
	public static final BlockEntry<SculkCollectorBlock> SCULK_COLLECTOR;
	public static final BlockEntry<SculkNurseryBlock> SCULK_NURSERY;

	public static final BlockEntry<SNFluidReservoirBlock> SN_FLUID_RESERVOIR;
	public static final BlockEntry<SNFluidInputPortBlock> SN_FLUID_INPUT_PORT;
	public static final BlockEntry<SNFluidOutputPortBlock> SN_FLUID_OUTPUT_PORT;

	static {
		DTCreativeTabs.getTab("machine");

		CRUSHER = DeepTech.REGISTRATE.block("crusher", CrusherBlock::new)
				.blockstate(CrusherBlock.genBlockState())
				.tag(DeepTechBlockTags.MACHINES)
				.item()
				.tab(DTCreativeTabs.getTabKey("machine"))
				.tag(DeepTechItemTags.MACHINES)
				.model(ItemModelGen.withModel("block/machine/crusher/on"))
				.build()
				.register();

		SCULK_FURNACE = DeepTech.REGISTRATE.block("sculk_furnace", SculkFurnaceBlock::new)
				.blockstate(SculkFurnaceBlock.genBlockState())
				.tag(DeepTechBlockTags.MACHINES)
				.item()
				.tab(DTCreativeTabs.getTabKey("machine"))
				.tag(DeepTechItemTags.MACHINES)
				.model(ItemModelGen.withModel("block/machine/furnace/on"))
				.build()
				.register();

		EXP_GENERATOR = DeepTech.REGISTRATE.block("exp_generator", EXPGeneratorBlock::new)
				.blockstate(EXPGeneratorBlock.genBlockState())
				.tag(DeepTechBlockTags.MACHINES)
				.item()
				.tab(DTCreativeTabs.getTabKey("machine"))
				.tag(DeepTechItemTags.MACHINES)
				.model(ItemModelGen.withModel("block/machine/exp_generator/on"))
				.build()
				.register();

		ALLOY_FURNACE = DeepTech.REGISTRATE.block("alloy_furnace", AlloyFurnaceBlock::new)
				.blockstate(AlloyFurnaceBlock.genBlockState())
				.tag(DeepTechBlockTags.MACHINES)
				.item()
				.tab(DTCreativeTabs.getTabKey("machine"))
				.tag(DeepTechItemTags.MACHINES)
				.model(ItemModelGen.withModel("block/machine/alloy_furnace/on"))
				.build()
				.register();

		RESONANCE_NODE = DeepTech.REGISTRATE.block("resonance_node", ResonanceNodeBlock::new)
				.blockstate(ResonanceNodeBlock.genBlockState())
				.tag(DeepTechBlockTags.MACHINES)
				.item()
				.tab(DTCreativeTabs.getTabKey("machine"))
				.tag(DeepTechItemTags.MACHINES)
				.model(ItemModelGen.withModel("block/machine/resonance_node"))
				.build()
				.register();

		ENERGY_CELL = DeepTech.REGISTRATE.block("energy_cell", EnergyCellBlock::new)
				.blockstate(EnergyCellBlock.genBlockState())
				.tag(DeepTechBlockTags.MACHINES)
				.item()
				.tab(DTCreativeTabs.getTabKey("machine"))
				.tag(DeepTechItemTags.MACHINES)
				.model(ItemModelGen.withModel("block/machine/energy_cell"))
				.build()
				.register();
		SN_CENTER = DeepTech.REGISTRATE.block("sculk_network_center", SNCenterBlock::new)
				.blockstate(NonNullBiConsumer.noop())
				.tag(DeepTechBlockTags.MACHINES)
				.item()
				.tab(DTCreativeTabs.getTabKey("machine"))
				.tag(DeepTechItemTags.MACHINES)
				.model(NonNullBiConsumer.noop())
				.build()
				.register();
		SN_ITEM_RESERVOIR = DeepTech.REGISTRATE.block("sculk_network_item_reservoir", SNItemReservoirBlock::new)
				.blockstate(NonNullBiConsumer.noop())
				.tag(DeepTechBlockTags.MACHINES)
				.item()
				.tab(DTCreativeTabs.getTabKey("machine"))
				.tag(DeepTechItemTags.MACHINES)
				.model(NonNullBiConsumer.noop())
				.build()
				.register();
		SN_ITEM_INPUT_PORT = DeepTech.REGISTRATE.block("sculk_network_item_input_port", SNItemInputPortBlock::new)
				.blockstate(NonNullBiConsumer.noop())
				.tag(DeepTechBlockTags.MACHINES)
				.item()
				.tab(DTCreativeTabs.getTabKey("machine"))
				.tag(DeepTechItemTags.MACHINES)
				.model(NonNullBiConsumer.noop())
				.build()
				.register();
		SN_ITEM_OUTPUT_PORT = DeepTech.REGISTRATE.block("sculk_network_item_output_port", SNItemOutputPortBlock::new)
				.blockstate(NonNullBiConsumer.noop())
				.tag(DeepTechBlockTags.MACHINES)
				.item()
				.tab(DTCreativeTabs.getTabKey("machine"))
				.tag(DeepTechItemTags.MACHINES)
				.model(NonNullBiConsumer.noop())
				.build()
				.register();
		SN_FLUID_RESERVOIR = DeepTech.REGISTRATE.block("sculk_network_fluid_reservoir", SNFluidReservoirBlock::new)
				.blockstate(NonNullBiConsumer.noop())
				.tag(DeepTechBlockTags.MACHINES)
				.item()
				.tab(DTCreativeTabs.getTabKey("machine"))
				.tag(DeepTechItemTags.MACHINES)
				.model(NonNullBiConsumer.noop())
				.build()
				.register();
		SN_FLUID_INPUT_PORT = DeepTech.REGISTRATE.block("sculk_network_fluid_input_port", SNFluidInputPortBlock::new)
				.blockstate(NonNullBiConsumer.noop())
				.tag(DeepTechBlockTags.MACHINES)
				.item()
				.tab(DTCreativeTabs.getTabKey("machine"))
				.tag(DeepTechItemTags.MACHINES)
				.model(NonNullBiConsumer.noop())
				.build()
				.register();
		SN_FLUID_OUTPUT_PORT = DeepTech.REGISTRATE.block("sculk_network_fluid_output_port", SNFluidOutputPortBlock::new)
				.blockstate(NonNullBiConsumer.noop())
				.tag(DeepTechBlockTags.MACHINES)
				.item()
				.tab(DTCreativeTabs.getTabKey("machine"))
				.tag(DeepTechItemTags.MACHINES)
				.model(NonNullBiConsumer.noop())
				.build()
				.register();
		SN_ACCESSOR = DeepTech.REGISTRATE.block("sculk_network_accessor", SNAccessorBlock::new)
				.blockstate(NonNullBiConsumer.noop())
				.tag(DeepTechBlockTags.MACHINES)
				.item()
				.tab(DTCreativeTabs.getTabKey("machine"))
				.tag(DeepTechItemTags.MACHINES)
				.model(NonNullBiConsumer.noop())
				.build()
				.register();

		SCULK_COLLECTOR = DeepTech.REGISTRATE.block("sculk_collector", SculkCollectorBlock::new)
				.blockstate(SculkCollectorBlock.genBlockState())
				.tag(DeepTechBlockTags.MACHINES)
				.item()
				.tab(DTCreativeTabs.getTabKey("machine"))
				.tag(DeepTechItemTags.MACHINES)
				.model(ItemModelGen.withModel("block/machine/sculk_collector/off"))
				.build()
				.register();

		SCULK_NURSERY = DeepTech.REGISTRATE.block("sculk_nursery", SculkNurseryBlock::new)
				.blockstate(SculkNurseryBlock.genBlockState())
				.tag(DeepTechBlockTags.MACHINES)
				.item()
				.tab(DTCreativeTabs.getTabKey("machine"))
				.tag(DeepTechItemTags.MACHINES)
				.model(ItemModelGen.withModel("block/machine/sculk_nursery/on"))
				.build()
				.register();
	}

	public static void register() {
		DeepTech.registerLog("Machine Blocks");
	}
}