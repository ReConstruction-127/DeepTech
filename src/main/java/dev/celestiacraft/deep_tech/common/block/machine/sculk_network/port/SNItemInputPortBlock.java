package dev.celestiacraft.deep_tech.common.block.machine.sculk_network.port;

import net.minecraft.world.level.block.state.BlockBehaviour;

// ✅ 正确：接受 Properties 参数并传递给父类
public class SNItemInputPortBlock extends SNPortBlock {
	public SNItemInputPortBlock(Properties properties) {
		super(properties);
	}
}
