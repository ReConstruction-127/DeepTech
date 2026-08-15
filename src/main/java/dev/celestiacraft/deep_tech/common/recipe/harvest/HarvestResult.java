package dev.celestiacraft.deep_tech.common.recipe.harvest;

import net.minecraft.world.item.ItemStack;

/**
 * 采集配方的输出项:物品 + 概率(0.0 ~ 1.0)
 */
public class HarvestResult {
	public final ItemStack stack;
	public final double chance;

	public HarvestResult(ItemStack stack, double chance) {
		this.stack = stack;
		this.chance = chance;
	}

	public HarvestResult copy() {
		return new HarvestResult(stack.copy(), chance);
	}
}