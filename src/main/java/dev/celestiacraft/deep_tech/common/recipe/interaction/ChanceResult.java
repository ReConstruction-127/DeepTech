package dev.celestiacraft.deep_tech.common.recipe.interaction;

import net.minecraft.world.item.ItemStack;
// ✅ 新增概率结果类
public class ChanceResult {
	public final ItemStack stack;
	public final double chance;  // 0.0 ~ 1.0
	public ChanceResult(ItemStack stack, double chance) {
		this.stack = stack;
		this.chance = chance;
	}
}