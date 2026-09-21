package dev.celestiacraft.deep_tech.common.block.machine.advanced.sculk_network.accessor.capability;

import dev.celestiacraft.deep_tech.common.block.machine.advanced.sculk_network.accessor.SNAccessorBlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public class NetworkFluidSource implements IFluidHandler {
	private final SNAccessorBlockEntity accessor;
	private final FluidStack fluid;

	public NetworkFluidSource(SNAccessorBlockEntity accessor, FluidStack fluid) {
		this.accessor = accessor;
		this.fluid = fluid.copy();
	}

	@Override
	public int getTanks() {
		return 1;
	}

	@Override
	public @NotNull FluidStack getFluidInTank(int tank) {
		return drain(Integer.MAX_VALUE, FluidAction.SIMULATE);
	}

	@Override
	public int getTankCapacity(int tank) {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
		return stack.isFluidEqual(fluid);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		return 0;
	}

	@Override
	public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
		if (resource.isEmpty() || !resource.isFluidEqual(fluid)) {
			return FluidStack.EMPTY;
		}
		return accessor.drain(resource, action);
	}

	@Override
	public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
		if (fluid.isEmpty() || maxDrain <= 0) {
			return FluidStack.EMPTY;
		}
		return accessor.drain(new FluidStack(fluid, maxDrain), action);
	}
}
