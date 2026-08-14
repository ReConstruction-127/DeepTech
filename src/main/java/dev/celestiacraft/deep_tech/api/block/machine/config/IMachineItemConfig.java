package dev.celestiacraft.deep_tech.api.block.machine.config;

import net.minecraft.world.item.ItemStack;

public interface IMachineItemConfig {
	/**
	 * 获取机器总槽位数量
	 * <p>
	 * 默认情况下, 总槽位数量等于输入槽数量加输出槽数量
	 *
	 * @return 机器总槽位数量
	 */
	default int getMaxMachineSlot() {
		return getItemInputSlotCount() + getItemOutputSlotCount();
	}

	/**
	 * 获取机器输入槽数量
	 * <p>
	 * 输入槽会从实际槽位下标 0 开始连续排列
	 * <p>
	 * 例如返回 2 时, slot 0 和 slot 1 都是输入槽
	 *
	 * @return 输入槽数量
	 */
	int getItemInputSlotCount();

	/**
	 * 获取机器输出槽数量
	 * <p>
	 * 输出槽会紧接在输入槽之后连续排列
	 * <p>
	 * 例如输入槽数量为 1, 输出槽数量为 1 时, slot 1 是输出槽
	 *
	 * @return 输出槽数量
	 */
	int getItemOutputSlotCount();

	/**
	 * 将第几个输入槽转换为实际槽位下标
	 * <p>
	 * 输入槽从 slot 0 开始, 所以默认直接返回 index
	 *
	 * @param index 输入槽序号, 从 0 开始
	 * @return 实际槽位下标
	 */
	default int getItemInputSlotIndex(int index) {
		return index;
	}

	/**
	 * 将第几个输出槽转换为实际槽位下标
	 * <p>
	 * 输出槽默认排在所有输入槽之后
	 *
	 * @param index 输出槽序号, 从 0 开始
	 * @return 实际槽位下标
	 */
	default int getItemOutputSlotIndex(int index) {
		return getItemInputSlotCount() + index;
	}

	/**
	 * 判断指定槽位是否允许插入物品
	 * <p>
	 * 默认情况下, 只有输入槽允许插入物品
	 *
	 * @param slot  实际槽位下标
	 * @param stack 尝试插入的物品
	 * @return 是否允许插入物品
	 */
	default boolean canInsertItem(int slot, ItemStack stack) {
		return slot >= 0 && slot < getItemInputSlotCount();
	}

	/**
	 * 判断指定槽位是否允许提取物品
	 * <p>
	 * 默认情况下, 只有输出槽允许提取物品
	 *
	 * @param slot  实际槽位下标
	 * @param stack 尝试提取的物品
	 * @return 是否允许提取物品
	 */
	default boolean canExtractItem(int slot, ItemStack stack) {
		return slot >= getItemInputSlotCount() && slot < getMaxMachineSlot();
	}

	/**
	 * 获取指定槽位的最大物品堆叠数量
	 * <p>
	 * 默认情况下, 每个槽位最大存储原版允许的 64 个物品
	 *
	 * @param slot 实际槽位下标
	 * @return 该槽位最大物品数量
	 */
	default int getMachineSlotLimit(int slot) {
		return 64;
	}
}