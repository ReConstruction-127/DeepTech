package dev.celestiacraft.deep_tech.api.fluid;

import com.lowdragmc.lowdraglib.side.fluid.FluidStack;
import com.lowdragmc.lowdraglib.side.fluid.IFluidTransfer;
import com.lowdragmc.lowdraglib.side.fluid.forge.FluidHelperImpl;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

/**
 * 把一个"只含单个储罐"的 Forge {@link IFluidHandler}(如 {@link FluidTank}
 * 或 {@code MachineFluidHandler} 的单罐视图)包装成 LDLib 的 {@link IFluidTransfer}(只有 1 个 tank).
 * <p>
 * LDLib TankWidget 的桶点击是对整个 IFluidTransfer 做 fill/drain(不区分 tank 索引),
 * 因此每个 UI 槽位必须各自绑定一个"只暴露该罐"的 transfer,点击才精确作用于对应罐,
 * 而不是命中聚合 handler 的固定顺序(第一罐优先).
 */
public class SingleTankFluidTransfer implements IFluidTransfer {
	private final IFluidHandler tank;

	public SingleTankFluidTransfer(IFluidHandler tank) {
		this.tank = tank;
	}

	@Override
	public int getTanks() {
		return 1;
	}

	@Override
	public @NotNull FluidStack getFluidInTank(int index) {
		return FluidHelperImpl.toFluidStack(tank.getFluidInTank(0));
	}

	@Override
	public void setFluidInTank(int index, @NotNull FluidStack stack) {
		IFluidHandler.FluidAction action = IFluidHandler.FluidAction.EXECUTE;
		tank.drain(Integer.MAX_VALUE, action);
		net.minecraftforge.fluids.FluidStack forge = FluidHelperImpl.toFluidStack(stack);
		if (!forge.isEmpty()) {
			tank.fill(forge, action);
		}
	}

	@Override
	public long getTankCapacity(int index) {
		return tank.getTankCapacity(0);
	}

	@Override
	public boolean isFluidValid(int index, @NotNull FluidStack stack) {
		return tank.isFluidValid(0, FluidHelperImpl.toFluidStack(stack));
	}

	@Override
	public long fill(int index, FluidStack stack, boolean simulate, boolean isFill) {
		return tank.fill(FluidHelperImpl.toFluidStack(stack), action(simulate));
	}

	@Override
	public boolean supportsFill(int index) {
		return true;
	}

	@Override
	public @NotNull FluidStack drain(int index, FluidStack stack, boolean simulate, boolean isFill) {
		return FluidHelperImpl.toFluidStack(tank.drain(FluidHelperImpl.toFluidStack(stack), action(simulate)));
	}

	@Override
	public boolean supportsDrain(int index) {
		return true;
	}

	@Override
	public @NotNull FluidStack drain(long maxDrain, boolean simulate, boolean isFill) {
		return FluidHelperImpl.toFluidStack(tank.drain((int) Math.min(maxDrain, Integer.MAX_VALUE), action(simulate)));
	}

	private static IFluidHandler.FluidAction action(boolean simulate) {
		return simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE;
	}

	@Override
	public @NotNull Object createSnapshot() {
		return tank.getFluidInTank(0).copy();
	}

	@Override
	public void restoreFromSnapshot(Object snapshot) {
		IFluidHandler.FluidAction action = IFluidHandler.FluidAction.EXECUTE;
		tank.drain(Integer.MAX_VALUE, action);
		net.minecraftforge.fluids.FluidStack forge = (net.minecraftforge.fluids.FluidStack) snapshot;
		if (!forge.isEmpty()) {
			tank.fill(forge, action);
		}
	}
}