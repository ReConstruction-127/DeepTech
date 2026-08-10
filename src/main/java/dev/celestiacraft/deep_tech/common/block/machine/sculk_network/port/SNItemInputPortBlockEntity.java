package dev.celestiacraft.deep_tech.common.block.machine.sculk_network.port;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 物品输入端口本身不执行任何逻辑。
 * 网络扫描与物品转运全部由中枢（{@link dev.celestiacraft.deep_tech.common.block.machine.sculk_network.center.SNCenterBlockEntity}）的 BFS 承载。
 */
public class SNItemInputPortBlockEntity extends BlockEntity {
	public SNItemInputPortBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
}
