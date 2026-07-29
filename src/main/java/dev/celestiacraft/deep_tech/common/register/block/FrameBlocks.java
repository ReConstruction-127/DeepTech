package dev.celestiacraft.deep_tech.common.register.block;

import com.tterrag.registrate.util.entry.BlockEntry;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.api.block.FrameBlock;
import dev.celestiacraft.deep_tech.api.client.model.ItemModelGen;
import dev.celestiacraft.deep_tech.common.register.DTCreativeTabs;

public class FrameBlocks {
	public static final BlockEntry<FrameBlock> MACHINE_FRAME;

	static {
		DTCreativeTabs.getTab("machine");

		MACHINE_FRAME = DeepTech.REGISTRATE.block("machine_frame", FrameBlock::new)
				.item()
				.model(ItemModelGen.withModel("block/machine_frame"))
				.build()
				.blockstate(FrameBlock.genBlockState("machine_frame"))
				.register();
	}

	public static void register() {
		DeepTech.registerLog("Frame Blocks");
	}
}
