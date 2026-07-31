package dev.celestiacraft.deep_tech.api.block.machine.config;

import net.minecraftforge.fluids.FluidStack;

public interface IMachineFluidConfig {
	default int getMaxMachineTank() {
		return getFluidInputTankCount() + getFluidOutputTankCount();
	}

	default int getFluidInputTankCount() {
		return 0;
	}

	default int getFluidOutputTankCount() {
		return 0;
	}

	default int getFluidInputTankIndex(int index) {
		return index;
	}

	default int getFluidOutputTankIndex(int index) {
		return getFluidInputTankCount() + index;
	}

	default int getMachineTankCapacity(int tank) {
		return 0;
	}

	default boolean canFillFluid(int tank, FluidStack stack) {
		return tank >= 0 && tank < getFluidInputTankCount();
	}

	default boolean canDrainFluid(int tank, FluidStack stack) {
		return tank >= getFluidInputTankCount() && tank < getMaxMachineTank();
	}
}
