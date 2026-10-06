package dev.celestiacraft.deep_tech.common.register;

import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.common.register.block.BasicBlocks;
import dev.celestiacraft.deep_tech.common.register.block.FrameBlocks;
import dev.celestiacraft.deep_tech.common.register.block.MachineBlocks;
import dev.celestiacraft.deep_tech.common.register.block.PluginBlocks;
import dev.celestiacraft.deep_tech.common.register.block.ReactorBlocks;

public class DTBlocks {
	public static void register() {
		MachineBlocks.register();
		ReactorBlocks.register();
		PluginBlocks.register();
		FrameBlocks.register();
		BasicBlocks.register();

		DeepTech.registerLog("Blocks");
	}
}