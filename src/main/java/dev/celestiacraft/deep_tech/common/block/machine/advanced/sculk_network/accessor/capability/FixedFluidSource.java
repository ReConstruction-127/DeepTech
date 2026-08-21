package dev.celestiacraft.deep_tech.common.block.machine.advanced.sculk_network.accessor.capability;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

/**
 * 已从网络抽出的流体的固定来源:供 tryFillContainer 模拟/实际灌入.
 * 灌完剩余的仍可通过 {@link #getFluid()} 读回,由调用方归还网络.
 */
public class FixedFluidSource implements IFluidHandler {
	private FluidStack fluid;

	public FixedFluidSource(FluidStack fluid) {
		this.fluid = fluid;
	}

	/** 灌入后剩余的流体(调用方应归还网络) */
	public FluidStack getFluid() {
		return fluid;
	}

	@Override
	public int getTanks() {
		return 1;
	}

	@Override
	public @NotNull FluidStack getFluidInTank(int tank) {
		return fluid;
	}

	@Override
	public int getTankCapacity(int tank) {
		return fluid.getAmount();
	}

	@Override
	public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
		return true;
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
		return drain(resource.getAmount(), action);
	}

	@Override
	public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
		if (fluid.isEmpty() || maxDrain <= 0) {
			return FluidStack.EMPTY;
		}
		FluidStack out = fluid.copy();
		out.setAmount(Math.min(maxDrain, fluid.getAmount()));
		if (action.execute()) {
			fluid.shrink(out.getAmount());
		}
		return out;
	}
}