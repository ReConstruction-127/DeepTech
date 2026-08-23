package dev.celestiacraft.deep_tech.common.block.machine.advanced.sculk_network.port;

import dev.celestiacraft.deep_tech.common.block.machine.advanced.sculk_network.center.SNCenterBlockEntity;
import dev.celestiacraft.deep_tech.common.register.block.BasicBlocks;
import dev.celestiacraft.deep_tech.common.register.block.MachineBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiConsumer;

public class SNHelper {

	public static final int NETWORK_RANGE = 16;
	private static final Direction[] DIRECTIONS = Direction.values();

	public static Set<BlockPos> collectNetwork(Level level, BlockPos start, @Nullable BiConsumer<BlockPos, Block> visitor) {
		Set<BlockPos> visited = new HashSet<>();
		Queue<BlockPos> queue = new ArrayDeque<>();
		queue.add(start);
		visited.add(start);

		int distance = 0;
		while (!queue.isEmpty()) {
			int layerSize = queue.size();
			for (int index = 0; index < layerSize; index++) {
				BlockPos current = queue.poll();
				if (visitor != null) {
					visitor.accept(current, level.getBlockState(current).getBlock());
				}
				if (distance >= NETWORK_RANGE) {
					continue;
				}
				for (Direction direction : DIRECTIONS) {
					BlockPos neighbor = current.relative(direction);
					if (!visited.contains(neighbor) && isNetworkComponent(level, neighbor)) {
						visited.add(neighbor);
						queue.add(neighbor);
					}
				}
			}
			distance++;
		}
		return visited;
	}

	/**
	 * 从任意网络组件出发, 沿脉络/组件查找最近的中枢.
	 * 仅遍历已加载的区块, 最大搜索范围 16 格.
	 */
	@Nullable
	public static SNCenterBlockEntity findNetworkCenter(Level level, BlockPos start) {
		Queue<BlockPos> queue = new ArrayDeque<>();
		Set<BlockPos> visited = new HashSet<>();
		queue.add(start);
		visited.add(start);

		while (!queue.isEmpty()) {
			BlockPos pos = queue.poll();
			// 如果区块未加载, 跳过(防止加载新区块)
			if (!level.isLoaded(pos)) continue;

			BlockState state = level.getBlockState(pos);
			if (state.getBlock() == MachineBlocks.SN_CENTER.get()) {
				BlockEntity be = level.getBlockEntity(pos);
				if (be instanceof SNCenterBlockEntity center) {
					return center;
				}
			}

			// 只沿网络组件扩展(起点 16 格半径内,入队前用距离平方检查,避免开方)
			if (pos.distSqr(start) >= 16 << 4) continue;

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
	 * 判断一个方块是否是幽匿网络组件(包括中枢, 脉络, 端口, 存储器等).
	 */
	public static boolean isNetworkComponent(Level level, BlockPos pos) {
		if (!level.isLoaded(pos)) return false;
		BlockState state = level.getBlockState(pos);
		var block = state.getBlock();

		// 中枢
		if (block == MachineBlocks.SN_CENTER.get()) return true;

		// 脉络(厚/薄)
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