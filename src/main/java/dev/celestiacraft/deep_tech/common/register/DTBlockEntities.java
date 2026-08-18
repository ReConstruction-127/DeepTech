package dev.celestiacraft.deep_tech.common.register;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.common.block.machine.alloy_furnace.AlloyFurnaceBlockEntity;
import dev.celestiacraft.deep_tech.common.block.machine.crusher.CrusherBlockEntity;
import dev.celestiacraft.deep_tech.common.block.machine.collector.SculkCollectorBlockEntity;
import dev.celestiacraft.deep_tech.common.block.machine.energy_cell.EnergyCellBlockEntity;
import dev.celestiacraft.deep_tech.common.block.machine.exp_generator.EXPGeneratorBlockEntity;
import dev.celestiacraft.deep_tech.common.block.machine.furnace.SculkFurnaceBlockEntity;
import dev.celestiacraft.deep_tech.common.block.machine.resonance_node.ResonanceNodeBlockEntity;
import dev.celestiacraft.deep_tech.common.block.machine.sculk_network.accessor.SNAccessorBlockEntity;
import dev.celestiacraft.deep_tech.common.block.machine.sculk_network.center.SNCenterBlockEntity;
import dev.celestiacraft.deep_tech.common.block.machine.sculk_network.port.SNFluidInputPortBlockEntity;
import dev.celestiacraft.deep_tech.common.block.machine.sculk_network.port.SNFluidOutputPortBlockEntity;
import dev.celestiacraft.deep_tech.common.block.machine.sculk_network.port.SNItemInputPortBlockEntity;
import dev.celestiacraft.deep_tech.common.block.machine.sculk_network.port.SNItemOutputPortBlockEntity;
import dev.celestiacraft.deep_tech.common.block.machine.sculk_network.reservoir.SNFluidReservoirBlockEntity;
import dev.celestiacraft.deep_tech.common.block.machine.sculk_network.reservoir.SNItemReservoirBlockEntity;
import dev.celestiacraft.deep_tech.common.block.machine.sculk_nursery.SculkNurseryBlockEntity;
import dev.celestiacraft.deep_tech.common.block.machine.processor.ProcessorBlockEntity;
import dev.celestiacraft.deep_tech.common.block.machine.assembler.AssemblerBlockEntity;
import dev.celestiacraft.deep_tech.common.register.block.MachineBlocks;

public class DTBlockEntities {
	public static final BlockEntityEntry<CrusherBlockEntity> CRUSHER;
	public static final BlockEntityEntry<SculkFurnaceBlockEntity> SCULK_FURNACE;
	public static final BlockEntityEntry<EXPGeneratorBlockEntity> EXP_GENERATOR;
	public static final BlockEntityEntry<ResonanceNodeBlockEntity> RESONANCE_NODE;
	public static final BlockEntityEntry<AlloyFurnaceBlockEntity> ALLOY_FURNACE;
	public static final BlockEntityEntry<EnergyCellBlockEntity> ENERGY_CELL;

	public static final BlockEntityEntry<SNCenterBlockEntity> SN_CENTER;
	public static final BlockEntityEntry<SNItemReservoirBlockEntity> SN_ITEM_RESERVOIR;
	public static final BlockEntityEntry<SNFluidReservoirBlockEntity> SN_FLUID_RESERVOIR;
	public static final BlockEntityEntry<SNItemInputPortBlockEntity> SN_ITEM_INPUT_PORT;
	public static final BlockEntityEntry<SNFluidInputPortBlockEntity> SN_FLUID_INPUT_PORT;
	public static final BlockEntityEntry<SNItemOutputPortBlockEntity> SN_ITEM_OUTPUT_PORT;
	public static final BlockEntityEntry<SNFluidOutputPortBlockEntity> SN_FLUID_OUTPUT_PORT;
	public static final BlockEntityEntry<SNAccessorBlockEntity> SN_ACCESSOR;
	public static final BlockEntityEntry<SculkCollectorBlockEntity> SCULK_COLLECTOR;
	public static final BlockEntityEntry<SculkNurseryBlockEntity> SCULK_NURSERY;
	public static final BlockEntityEntry<ProcessorBlockEntity> PROCESSOR;
	public static final BlockEntityEntry<AssemblerBlockEntity> ASSEMBLER;

	static {
		CRUSHER = DeepTech.REGISTRATE.blockEntity("crusher", CrusherBlockEntity::new)
				.validBlock(MachineBlocks.CRUSHER)
				.register();

		SCULK_FURNACE = DeepTech.REGISTRATE.blockEntity("furnace", SculkFurnaceBlockEntity::new)
				.validBlock(MachineBlocks.SCULK_FURNACE)
				.register();

		EXP_GENERATOR = DeepTech.REGISTRATE.blockEntity("exp_generator", EXPGeneratorBlockEntity::new)
				.validBlock(MachineBlocks.EXP_GENERATOR)
				.register();

		RESONANCE_NODE = DeepTech.REGISTRATE.blockEntity("resonance_node", ResonanceNodeBlockEntity::new)
				.validBlock(MachineBlocks.RESONANCE_NODE)
				.register();

		ALLOY_FURNACE = DeepTech.REGISTRATE.blockEntity("alloy_furnace", AlloyFurnaceBlockEntity::new)
				.validBlock(MachineBlocks.ALLOY_FURNACE)
				.register();

		ENERGY_CELL = DeepTech.REGISTRATE.blockEntity("energy_cell", EnergyCellBlockEntity::new)
				.validBlock(MachineBlocks.ENERGY_CELL)
				.register();

		SN_ACCESSOR = DeepTech.REGISTRATE.blockEntity("sculk_network_accessor", SNAccessorBlockEntity::new)
				.validBlock(MachineBlocks.SN_ACCESSOR)
				.register();
		SCULK_COLLECTOR = DeepTech.REGISTRATE.blockEntity("sculk_collector", SculkCollectorBlockEntity::new)
				.validBlock(MachineBlocks.SCULK_COLLECTOR)
				.register();

		SCULK_NURSERY = DeepTech.REGISTRATE.blockEntity("sculk_nursery", SculkNurseryBlockEntity::new)
				.validBlock(MachineBlocks.SCULK_NURSERY)
				.register();

		PROCESSOR = DeepTech.REGISTRATE.blockEntity("processor", ProcessorBlockEntity::new)
				.validBlock(MachineBlocks.PROCESSOR)
				.register();

		ASSEMBLER = DeepTech.REGISTRATE.blockEntity("assembler", AssemblerBlockEntity::new)
				.validBlock(MachineBlocks.ASSEMBLER)
				.register();

		SN_CENTER = DeepTech.REGISTRATE.blockEntity("sculk_network_center", SNCenterBlockEntity::new)
				.validBlock(MachineBlocks.SN_CENTER)
				.register();

		SN_ITEM_INPUT_PORT = DeepTech.REGISTRATE.blockEntity("sculk_network_item_input_port", SNItemInputPortBlockEntity::new)
				.validBlock(MachineBlocks.SN_ITEM_INPUT_PORT)
				.register();

		SN_ITEM_OUTPUT_PORT = DeepTech.REGISTRATE.blockEntity("sculk_network_item_output_port", SNItemOutputPortBlockEntity::new)
				.validBlock(MachineBlocks.SN_ITEM_OUTPUT_PORT)
				.register();

		SN_FLUID_INPUT_PORT = DeepTech.REGISTRATE.blockEntity("sculk_network_fluid_input_port", SNFluidInputPortBlockEntity::new)
				.validBlock(MachineBlocks.SN_FLUID_INPUT_PORT)
				.register();

		SN_FLUID_OUTPUT_PORT = DeepTech.REGISTRATE.blockEntity("sculk_network_fluid_output_port", SNFluidOutputPortBlockEntity::new)
				.validBlock(MachineBlocks.SN_FLUID_OUTPUT_PORT)
				.register();

		SN_ITEM_RESERVOIR = DeepTech.REGISTRATE.blockEntity("sculk_network_item_reservoir", SNItemReservoirBlockEntity::new)
				.validBlock(MachineBlocks.SN_ITEM_RESERVOIR)
				.register();

		SN_FLUID_RESERVOIR = DeepTech.REGISTRATE.blockEntity("sculk_network_fluid_reservoir", SNFluidReservoirBlockEntity::new)
				.validBlock(MachineBlocks.SN_FLUID_RESERVOIR)
				.register();
	}

	public static void register() {
		DeepTech.registerLog("Block Entities");
	}
}