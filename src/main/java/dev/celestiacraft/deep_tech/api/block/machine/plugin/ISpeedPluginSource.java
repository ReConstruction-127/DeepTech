package dev.celestiacraft.deep_tech.api.block.machine.plugin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Set;

/**
 * 能"报出"自己旁边有几个加速插件的方块.
 * <p>
 * 加速插件本身实现这个接口(自己算一个); 扩展坞这类会把旁边方块的效果转出去的方块也实现它,
 * 于是插件隔着扩展坞一样能加速. 查询入口在 {@link MachinePlugins}.
 */
public interface ISpeedPluginSource {
	/**
	 * 数一数自己旁边有多少个加速插件.
	 *
	 * @param level   所在世界
	 * @param pos     自己的位置
	 * @param visited 本次查询已经走过的位置, 用来断掉互相转发形成的环(比如两个扩展坞对着问)
	 * @return 能提供的加速插件数量
	 */
	int getSpeedPluginCount(Level level, BlockPos pos, Set<BlockPos> visited);
}
