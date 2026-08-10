package dev.celestiacraft.deep_tech.common.register.block;

import com.tterrag.registrate.util.entry.BlockEntry;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.api.block.FrameBlock;
import dev.celestiacraft.deep_tech.api.block.properties.MiningLevel;
import dev.celestiacraft.deep_tech.common.register.DTCreativeTabs;
import dev.celestiacraft.libs.api.register.block.BasicBlock;
import net.minecraft.world.level.block.CarpetBlock;

public class BasicBlocks {
	public static final BlockEntry<BasicBlock> SCULK_NETWORK_BLOCK;
	public static final BlockEntry<CarpetBlock> SCULK_NETWORK_VEIN;

	static {
		SCULK_NETWORK_BLOCK = DeepTech.REGISTRATE.block("sculk_network_block", BasicBlock::new)
				.item()
				.build()
				.register();
	}
	static {
		SCULK_NETWORK_VEIN = DeepTech.REGISTRATE.block("sculk_network_block", CarpetBlock::new)
				.item()
				.build()
				.register();
	}

	public static void register() {
		DeepTech.registerLog("Basic Blocks");
	}
}