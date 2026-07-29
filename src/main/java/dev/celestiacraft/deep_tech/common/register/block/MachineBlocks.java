package dev.celestiacraft.deep_tech.common.register.block;

import com.tterrag.registrate.util.entry.BlockEntry;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.api.client.model.ItemModelGen;
import dev.celestiacraft.deep_tech.common.block.machine.crusher.CrusherBlock;
import dev.celestiacraft.deep_tech.common.block.machine.furnace.SculkFurnaceBlock;
import dev.celestiacraft.deep_tech.common.register.DTCreativeTabs;
import dev.celestiacraft.deep_tech.tags.DeepTechBlockTags;
import dev.celestiacraft.deep_tech.tags.DeepTechItemTags;

public class MachineBlocks {
	public static final BlockEntry<CrusherBlock> CRUSHER;
	public static final BlockEntry<SculkFurnaceBlock> SCULK_FURNACE;

	static {
		DTCreativeTabs.getTab("machine");

		CRUSHER = DeepTech.REGISTRATE.block("machine_crusher", CrusherBlock::new)
				.item()
				.tag(DeepTechItemTags.MACHINES)
				.model(ItemModelGen.withModel("block/machine_crusher_off"))
				.build()
				.blockstate(CrusherBlock.genBlockState())
				.tag(DeepTechBlockTags.MACHINES)
				.register();

		SCULK_FURNACE = DeepTech.REGISTRATE.block("machine_sculk_furnace", SculkFurnaceBlock::new)
				.item()
				.tag(DeepTechItemTags.MACHINES)
				.model(ItemModelGen.withModel("block/machine_sculk_furnace_off"))
				.build()
				.blockstate(SculkFurnaceBlock.genBlockState())
				.tag(DeepTechBlockTags.MACHINES)
				.register();
	}

	public static void register() {
		DeepTech.registerLog("Machine Blocks");
	}
}