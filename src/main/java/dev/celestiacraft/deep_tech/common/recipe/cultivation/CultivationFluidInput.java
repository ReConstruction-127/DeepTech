package dev.celestiacraft.deep_tech.common.recipe.cultivation;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

/**
 * 培育配方中的流体输入: 指定流体 + 需求量(mB).
 *
 * @param fluid  所需流体(源流体)
 * @param amount 需求量, 单位为 mB
 */
public record CultivationFluidInput(net.minecraft.world.level.material.Fluid fluid, int amount) {

	public boolean matches(@NotNull FluidStack stack) {
		return stack.getFluid() == fluid && stack.getAmount() >= amount;
	}

	@Override
	public String toString() {
		return "CultivationFluidInput{" + ForgeRegistries.FLUIDS.getKey(fluid) + " x" + amount + "}";
	}
}
