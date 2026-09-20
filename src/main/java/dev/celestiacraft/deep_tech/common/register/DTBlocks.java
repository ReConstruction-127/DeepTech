package dev.celestiacraft.deep_tech.common.register;

import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.common.register.block.BasicBlocks;
import dev.celestiacraft.deep_tech.common.register.block.FrameBlocks;
import dev.celestiacraft.deep_tech.common.register.block.MachineBlocks;

public class DTBlocks {
	public static void register() {
		MachineBlocks.register();
		FrameBlocks.register();
		BasicBlocks.register();

		DeepTech.registerLog("Blocks");
	}
}