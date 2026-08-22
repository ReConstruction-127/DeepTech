package dev.celestiacraft.deep_tech.common.register.block;

import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.api.block.SNPortBlock;
import dev.celestiacraft.deep_tech.common.register.DTCreativeTabs;
import dev.celestiacraft.libs.api.register.block.BasicBlock;
import net.minecraft.world.level.block.CarpetBlock;

public class BasicBlocks {
	public static final BlockEntry<BasicBlock> SCULK_NETWORK_BLOCK;
	public static final BlockEntry<CarpetBlock> SCULK_NETWORK_VEIN;

	static {
		SCULK_NETWORK_BLOCK = DeepTech.REGISTRATE.block("sculk_network_block", BasicBlock::new)
				.blockstate(SNPortBlock.simple("block/sculk_network/cable_block"))
				.item()
				.model(NonNullBiConsumer.noop())
				.tab(DTCreativeTabs.MACHINE.getKey())
				.build()
				.register();

		SCULK_NETWORK_VEIN = DeepTech.REGISTRATE.block("sculk_network_vein", CarpetBlock::new)
				.blockstate(SNPortBlock.simple("block/sculk_network/vein"))
				.item()
				.model(NonNullBiConsumer.noop())
				.tab(DTCreativeTabs.MACHINE.getKey())
				.build()
				.register();
	}

	public static void register() {
		DeepTech.registerLog("Basic Blocks");
	}
}
