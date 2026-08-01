package dev.celestiacraft.deep_tech.api.ingredien;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.world.item.crafting.Ingredient;

@Getter
@AllArgsConstructor
public class IngredientWithCount {
	private final Ingredient ingredient;
	private final int count;
}