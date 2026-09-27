package dev.celestiacraft.deep_tech.common.block.machine.bionic;

import dev.celestiacraft.deep_tech.api.block.machine.MachineBlockEntity;
import dev.celestiacraft.deep_tech.api.block.machine.plugin.MachinePlugins;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 所有仿生(bionic)机器的基类.
 * <p>
 * 目前统一了一件事: 机器旁边贴加速插件时的处理速度倍率. 一个插件 x2, 最多算两个(x4);
 * 扩展坞会把它旁边的插件转给它正对着的机器, 所以隔着扩展坞插也有效.
 * <p>
 * 以后的仿生机器继承这个类, 把算出来的处理时间用 {@link #applySpeedPlugins(int)} 过一道就行,
 * 剩下的(比如进度条, 能量)完全不用管.
 */
public abstract class BionicMachineBlockEntity<T extends BionicMachineBlockEntity<T>> extends MachineBlockEntity<T> {
	public BionicMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	/**
	 * 旁边加速插件带来的速度倍率: 没有插件 1, 一个 2, 两个 4.
	 */
	public int getSpeedPluginMultiplier() {
		if (level == null) {
			return 1;
		}

		return MachinePlugins.getSpeedMultiplier(level, worldPosition);
	}

	/**
	 * 按加速插件把处理时间缩短. 机器自己的配置倍率(比如高级电炉比普通电炉快一倍)要在这之前先算掉.
	 *
	 * @param time 原本要花的 tick 数
	 * @return 加速插件生效后的 tick 数, 至少 1
	 */
	public int applySpeedPlugins(int time) {
		return Math.max(1, time / getSpeedPluginMultiplier());
	}
}
