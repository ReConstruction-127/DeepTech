package dev.celestiacraft.deep_tech.common.block.machine.advanced.sculk_network.accessor.capability;

import dev.celestiacraft.deep_tech.common.block.machine.advanced.sculk_network.accessor.SNAccessorBlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

/** 网络流体接收端:直接把灌入的流体写进网络储库 */
public class NetworkFluidSink implements IFluidHandler {
	private final SNAccessorBlockEntity accessor;

	public NetworkFluidSink(SNAccessorBlockEntity accessor) {
		this.accessor = accessor;
	}

	@Override
	public int getTanks() {
		return 1;
	}

	@Override
	public @NotNull FluidStack getFluidInTank(int tank) {
		return FluidStack.EMPTY;
	}

	@Override
	public int getTankCapacity(int tank) {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
		return true;
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		return accessor.fill(resource, action);
	}

	@Override
	public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
		return FluidStack.EMPTY;
	}

	@Override
	public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
		return FluidStack.EMPTY;
	}
}