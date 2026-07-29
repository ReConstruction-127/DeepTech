package dev.celestiacraft.deep_tech.common.register.block;

import com.tterrag.registrate.util.entry.BlockEntry;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.api.client.model.ItemModelGen;
import dev.celestiacraft.deep_tech.common.block.machine.crusher.CrusherBlock;
import dev.celestiacraft.deep_tech.common.block.machine.furnace.SculkFurnaceBlock;
import dev.celestiacraft.deep_tech.common.register.DTCreativeTabs;

public class MachineBlocks {
	public static final BlockEntry<CrusherBlock> CRUSHER;
	public static final BlockEntry<SculkFurnaceBlock> SCULK_FURNACE;

	static {
		DTCreativeTabs.getTab("machine");

		CRUSHER = DeepTech.REGISTRATE.block("machine_crusher", CrusherBlock::new)
				.item()
				.model(ItemModelGen.withModel("block/machine_crusher_off"))
				.build()
				.blockstate(CrusherBlock.genBlockState())
				.register();

		SCULK_FURNACE = DeepTech.REGISTRATE.block("machine_sculk_furnace", SculkFurnaceBlock::new)
				.item()
				.model(ItemModelGen.withModel("block/machine_sculk_furnace_off"))
				.build()
				.blockstate(SculkFurnaceBlock.genBlockState())
				.register();
	}

	public static void register() {
		DeepTech.registerLog("Machine Blocks");
	}
}