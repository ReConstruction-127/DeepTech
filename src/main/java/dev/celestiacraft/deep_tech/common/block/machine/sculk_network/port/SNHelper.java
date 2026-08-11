package dev.celestiacraft.deep_tech.common.block.machine.sculk_network.port;

import dev.celestiacraft.deep_tech.common.block.machine.sculk_network.center.SNCenterBlockEntity;
import dev.celestiacraft.deep_tech.common.register.block.BasicBlocks;
import dev.celestiacraft.deep_tech.common.register.block.MachineBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class SNHelper {

	/**
	 * 从任意网络组件出发，沿脉络/组件查找最近的中枢。
	 * 仅遍历已加载的区块，最大搜索范围 16 格。
	 */
	@Nullable
	public static SNCenterBlockEntity findNetworkCenter(Level level, BlockPos start) {
		Queue<BlockPos> queue = new ArrayDeque<>();
		Set<BlockPos> visited = new HashSet<>();
		queue.add(start);
		visited.add(start);

		while (!queue.isEmpty()) {
			BlockPos pos = queue.poll();
			// 如果区块未加载，跳过（防止加载新区块）
			if (!level.isLoaded(pos)) continue;

			BlockState state = level.getBlockState(pos);
			if (state.getBlock() == MachineBlocks.SN_CENTER.get()) {
				BlockEntity be = level.getBlockEntity(pos);
				if (be instanceof SNCenterBlockEntity center) {
					return center;
				}
			}

			// 只沿网络组件扩展（超过 16 格停止，但此处用距离检查？由于BFS天然按层扩展，我们可以在入队前检查距离）
			// 但为避免无限扩展，我们在入队时检查距离（起始点距离）
			int distance = (int) Math.sqrt(pos.distSqr(start)); // 粗略距离
			if (distance >= 16) continue;

			for (Direction dir : Direction.values()) {
				BlockPos neighbor = pos.relative(dir);
				if (!visited.contains(neighbor) && isNetworkComponent(level, neighbor)) {
					visited.add(neighbor);
					queue.add(neighbor);
				}
			}
		}
		return null;
	}

	/**
	 * 判断一个方块是否是幽匿网络组件（包括中枢、脉络、端口、存储器等）。
	 */
	public static boolean isNetworkComponent(Level level, BlockPos pos) {
		if (!level.isLoaded(pos)) return false;
		BlockState state = level.getBlockState(pos);
		var block = state.getBlock();

		// 中枢
		if (block == MachineBlocks.SN_CENTER.get()) return true;

		// 脉络（厚/薄）
		if (block == BasicBlocks.SCULK_NETWORK_BLOCK.get()) return true;
		if (block == BasicBlocks.SCULK_NETWORK_VEIN.get()) return true;

		// 端口
		if (block == MachineBlocks.SN_ITEM_INPUT_PORT.get()) return true;
		if (block == MachineBlocks.SN_ITEM_OUTPUT_PORT.get()) return true;
		if (block == MachineBlocks.SN_FLUID_INPUT_PORT.get()) return true;
		if (block == MachineBlocks.SN_FLUID_OUTPUT_PORT.get()) return true;

		// 存储
		if (block == MachineBlocks.SN_ITEM_RESERVOIR.get()) return true;
		if (block == MachineBlocks.SN_FLUID_RESERVOIR.get()) return true;

		// 访问器
		if (block == MachineBlocks.SN_ACCESSOR.get()) return true;

		return false;
	}
}