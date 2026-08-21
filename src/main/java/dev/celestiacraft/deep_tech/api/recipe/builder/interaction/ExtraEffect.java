package dev.celestiacraft.deep_tech.api.recipe.builder.interaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

@Getter
@AllArgsConstructor
public class ExtraEffect {
	private final double chance;
	private final BlockState state;
	private final List<ItemStack> extraDrops;
}