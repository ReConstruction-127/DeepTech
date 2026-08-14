package dev.celestiacraft.deep_tech.common.block.machine.sculk_network.reservoir.capability;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

/**
 * 聚合所有储罐的 IFluidHandler(填装时按顺序,抽取时从后往前). 
 */
public class SNFluidReservoirTankHandler implements IFluidHandler {
	private final FluidTank[] tanks;

	public SNFluidReservoirTankHandler(FluidTank[] tanks) {
		this.tanks = tanks;
	}

	@Override
	public int getTanks() {
		return tanks.length;
	}

	@Override
	public @NotNull FluidStack getFluidInTank(int tank) {
		if (tank < 0 || tank >= tanks.length) {
			return FluidStack.EMPTY;
		}
		return tanks[tank].getFluid();
	}

	@Override
	public int getTankCapacity(int tank) {
		if (tank < 0 || tank >= tanks.length) {
			return 0;
		}
		return tanks[tank].getCapacity();
	}

	@Override
	public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
		return tank >= 0 && tank < tanks.length && tanks[tank].isFluidValid(stack);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		if (resource.isEmpty()) {
			return 0;
		}
		int filledTotal = 0;
		FluidStack remaining = resource.copy();
		for (FluidTank tank : tanks) {
			if (remaining.isEmpty()) {
				break;
			}
			int filled = tank.fill(remaining, action);
			filledTotal += filled;
			if (filled > 0) {
				remaining.shrink(filled);
			}
		}
		return filledTotal;
	}

	@Override
	public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
		if (resource.isEmpty()) {
			return FluidStack.EMPTY;
		}
		FluidStack drainedTotal = FluidStack.EMPTY;
		for (int i = tanks.length - 1; i >= 0; i--) {
			if (drainedTotal.getAmount() >= resource.getAmount()) {
				break;
			}
			FluidStack tankFluid = tanks[i].getFluid();
			if (tankFluid.isEmpty() || !tankFluid.isFluidEqual(resource)) {
				continue;
			}
			FluidStack drained = tanks[i].drain(resource.getAmount() - drainedTotal.getAmount(), action);
			if (drained.isEmpty()) {
				continue;
			}
			if (drainedTotal.isEmpty()) {
				drainedTotal = drained.copy();
			} else {
				drainedTotal.grow(drained.getAmount());
			}
		}
		return drainedTotal;
	}

	@Override
	public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
		if (maxDrain <= 0) {
			return FluidStack.EMPTY;
		}
		int drainedTotal = 0;
		FluidStack result = FluidStack.EMPTY;
		for (int i = tanks.length - 1; i >= 0; i--) {
			if (drainedTotal >= maxDrain) {
				break;
			}
			if (tanks[i].getFluid().isEmpty()) {
				continue;
			}
			FluidStack drained = tanks[i].drain(maxDrain - drainedTotal, action);
			if (drained.isEmpty()) {
				continue;
			}
			drainedTotal += drained.getAmount();
			if (result.isEmpty()) {
				result = drained.copy();
			} else {
				result.grow(drained.getAmount());
			}
		}
		return result;
	}
}