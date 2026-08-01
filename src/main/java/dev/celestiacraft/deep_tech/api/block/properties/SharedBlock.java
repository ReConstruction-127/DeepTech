package dev.celestiacraft.deep_tech.api.block.properties;

import dev.celestiacraft.deep_tech.common.register.block.FrameBlocks;
import net.minecraft.world.level.block.Block;

public class SharedBlock {
	public static Block frame() {
		return FrameBlocks.MACHINE_FRAME.get();
	}
}