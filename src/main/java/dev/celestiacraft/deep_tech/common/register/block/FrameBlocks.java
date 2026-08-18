package dev.celestiacraft.deep_tech.common.register.block;

import com.tterrag.registrate.util.entry.BlockEntry;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.api.block.FrameBlock;
import dev.celestiacraft.deep_tech.api.block.properties.MiningLevel;
import dev.celestiacraft.deep_tech.common.register.DTCreativeTabs;

public class FrameBlocks {
	public static final BlockEntry<FrameBlock> MACHINE_FRAME;
	public static final BlockEntry<FrameBlock> REINFORCED_MACHINE_FRAME;

	static {
		DTCreativeTabs.getTab("machine");

		MACHINE_FRAME = addFrame("machine", MiningLevel.WOODEN);
		REINFORCED_MACHINE_FRAME = addFrame("reinforced_machine", MiningLevel.IRON);
	}

	private static BlockEntry<FrameBlock> addFrame(String name, MiningLevel level) {
		String registerId = String.format("%s_frame", name);
		return DeepTech.REGISTRATE.block(registerId, FrameBlock::new)
				.transform(FrameBlock.miniProperties(level))
				.blockstate(FrameBlock.genBlockState(name))
				.item()
				.tab(DTCreativeTabs.getTabKey("machine"))
				.model(FrameBlock.item(name))
				.build()
				.register();
	}

	public static void register() {
		DeepTech.registerLog("Frame Blocks");
	}
}