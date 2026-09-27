package dev.celestiacraft.deep_tech.api.block.machine.plugin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.Set;

/**
 * 机器旁边加速插件的查询.
 * <p>
 * 机器只需要调 {@link #getSpeedMultiplier(Level, BlockPos)}: 它会看机器六个面上贴着什么,
 * 是加速插件就算一个, 是扩展坞这种 {@link ISpeedPluginSource} 就问它要数量,
 * 然后把总数封顶在 {@link #MAX_SPEED_PLUGINS}(一台机器最多两个, 也就是最多 x4).
 */
public final class MachinePlugins {
	/**
	 * 一台机器最多吃两个加速插件
	 */
	public static final int MAX_SPEED_PLUGINS = 2;

	private MachinePlugins() {
	}

	/**
	 * 机器旁边一共有几个加速插件(已封顶).
	 */
	public static int getSpeedPluginCount(Level level, BlockPos pos) {
		Set<BlockPos> visited = new HashSet<>();
		visited.add(pos.immutable());

		return countSpeedPlugins(level, pos, visited);
	}

	/**
	 * 加速插件带来的速度倍率: 没有插件 1, 一个 2, 两个 4.
	 */
	public static int getSpeedMultiplier(Level level, BlockPos pos) {
		return 1 << getSpeedPluginCount(level, pos);
	}

	private static int countSpeedPlugins(Level level, BlockPos pos, Set<BlockPos> visited) {
		int count = 0;

		for (Direction direction : Direction.values()) {
			if (count >= MAX_SPEED_PLUGINS) {
				break;
			}

			BlockPos neighbor = pos.relative(direction);
			if (level.getBlockState(neighbor).getBlock() instanceof ISpeedPluginSource source) {
				count += source.getSpeedPluginCount(level, neighbor, visited);
			}
		}

		return Math.min(count, MAX_SPEED_PLUGINS);
	}
}
