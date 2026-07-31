package dev.celestiacraft.deep_tech.api.block.machine.config;

import net.minecraftforge.fluids.FluidStack;

public interface IMachineFluidConfig {
	/**
	 * 获取机器总流体罐数量
	 * <p>
	 * 默认情况下, 总流体罐数量等于输入流体罐数量加输出流体罐数量
	 *
	 * @return 机器总流体罐数量
	 */
	default int getMaxMachineTank() {
		return getFluidInputTankCount() + getFluidOutputTankCount();
	}

	/**
	 * 获取机器输入流体罐数量
	 * <p>
	 * 输入流体罐会从实际流体罐下标 0 开始连续排列
	 * <p>
	 * 例如返回 2 时, tank 0 和 tank 1 都是输入流体罐
	 *
	 * @return 输入流体罐数量
	 */
	default int getFluidInputTankCount() {
		return 0;
	}

	/**
	 * 获取机器输出流体罐数量
	 * <p>
	 * 输出流体罐会紧接在输入流体罐之后连续排列
	 * <p>
	 * 例如输入流体罐数量为 1、输出流体罐数量为 1 时, tank 1 是输出流体罐
	 *
	 * @return 输出流体罐数量
	 */
	default int getFluidOutputTankCount() {
		return 0;
	}

	/**
	 * 将第几个输入流体罐转换为实际流体罐下标
	 * <p>
	 * 输入流体罐从 tank 0 开始, 所以默认直接返回 index
	 *
	 * @param index 输入流体罐序号, 从 0 开始
	 * @return 实际流体罐下标
	 */
	default int getFluidInputTankIndex(int index) {
		return index;
	}

	/**
	 * 将第几个输出流体罐转换为实际流体罐下标
	 * <p>
	 * 输出流体罐默认排在所有输入流体罐之后
	 *
	 * @param index 输出流体罐序号, 从 0 开始
	 * @return 实际流体罐下标
	 */
	default int getFluidOutputTankIndex(int index) {
		return getFluidInputTankCount() + index;
	}

	/**
	 * 获取指定流体罐的容量
	 *
	 * @param tank 实际流体罐下标
	 * @return 流体罐最大容量, 单位为 mB
	 */
	default int getMachineTankCapacity(int tank) {
		return 0;
	}

	/**
	 * 判断指定流体罐是否允许填充流体
	 * <p>
	 * 默认情况下, 只有输入流体罐允许填充流体
	 *
	 * @param tank  实际流体罐下标
	 * @param stack 尝试填充的流体
	 * @return 是否允许填充流体
	 */
	default boolean canFillFluid(int tank, FluidStack stack) {
		return tank >= 0 && tank < getFluidInputTankCount();
	}

	/**
	 * 判断指定流体罐是否允许排出流体
	 * <p>
	 * 默认情况下, 只有输出流体罐允许排出流体
	 *
	 * @param tank  实际流体罐下标
	 * @param stack 尝试排出的流体
	 * @return 是否允许排出流体
	 */
	default boolean canDrainFluid(int tank, FluidStack stack) {
		return tank >= getFluidInputTankCount() && tank < getMaxMachineTank();
	}
}