package dev.celestiacraft.deep_tech.common.recipe.harvest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.world.item.ItemStack;

@AllArgsConstructor
@Getter
public class HarvestOutput {
	public final ItemStack stack;
	public final double chance;

	public HarvestOutput copy() {
		return new HarvestOutput(stack.copy(), chance);
	}
}