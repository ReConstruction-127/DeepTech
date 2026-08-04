package dev.celestiacraft.deep_tech.api.recipe.builder.interaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.world.item.ItemStack;

@Getter
@AllArgsConstructor
public class WeightedResult {
	public final ItemStack stack;
	public final double weight;
}