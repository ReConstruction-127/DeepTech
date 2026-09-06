package dev.celestiacraft.deep_tech.common.register;

import dev.celestiacraft.deep_tech.common.register.block.BasicBlocks;
import dev.celestiacraft.deep_tech.common.register.block.FrameBlocks;
import dev.celestiacraft.deep_tech.common.register.block.MachineBlocks;
import dev.celestiacraft.libs.utils.annotation.log.Log;

public class DTBlocks {
	@Log("DeepTech Blocks is Registered!")
	public static void register() {
		MachineBlocks.register();
		FrameBlocks.register();
		BasicBlocks.register();

//		DeepTech.registerLog("Blocks");
	}
}