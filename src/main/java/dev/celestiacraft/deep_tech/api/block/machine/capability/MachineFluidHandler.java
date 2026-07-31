package dev.celestiacraft.deep_tech.api.block.machine.capability;

import dev.celestiacraft.deep_tech.api.block.machine.MachineBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class MachineFluidHandler implements IFluidHandler, INBTSerializable<CompoundTag> {
	private final MachineBlockEntity<?> machine;
	private final FluidStack[] fluids;

	public MachineFluidHandler(MachineBlockEntity<?> machine) {
		this.machine = machine;
		fluids = new FluidStack[machine.getMaxMachineTank()];
		Arrays.fill(fluids, FluidStack.EMPTY);
	}

	@Override
	public int getTanks() {
		return fluids.length;
	}

	@Override
	public @NotNull FluidStack getFluidInTank(int tank) {
		if (!isTankValid(tank)) {
			return FluidStack.EMPTY;
		}
		return fluids[tank];
	}

	@Override
	public int getTankCapacity(int tank) {
		if (!isTankValid(tank)) {
			return 0;
		}
		return machine.getMachineTankCapacity(tank);
	}

	@Override
	public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
		return isTankValid(tank) && machine.canFillFluid(tank, stack);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		if (resource.isEmpty()) {
			return 0;
		}

		int filled = 0;
		FluidStack remaining = resource.copy();
		for (int tank = 0; tank < fluids.length && !remaining.isEmpty(); tank++) {
			int tankFilled = fillTank(tank, remaining, action, true);
			if (tankFilled <= 0) {
				continue;
			}
			filled += tankFilled;
			remaining.shrink(tankFilled);
		}
		return filled;
	}

	@Override
	public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
		if (resource.isEmpty()) {
			return FluidStack.EMPTY;
		}

		FluidStack drained = FluidStack.EMPTY;
		int amountToDrain = resource.getAmount();
		for (int tank = 0; tank < fluids.length && amountToDrain > 0; tank++) {
			FluidStack stored = fluids[tank];
			if (stored.isEmpty() || !stored.isFluidEqual(resource) || machine.canDrainFluid(tank, stored)) {
				continue;
			}

			FluidStack tankDrained = drainTank(tank, amountToDrain, action, true);
			if (tankDrained.isEmpty()) {
				continue;
			}
			if (drained.isEmpty()) {
				drained = tankDrained;
			} else {
				drained.grow(tankDrained.getAmount());
			}
			amountToDrain -= tankDrained.getAmount();
		}
		return drained;
	}

	@Override
	public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
		if (maxDrain <= 0) {
			return FluidStack.EMPTY;
		}

		FluidStack drained = FluidStack.EMPTY;
		int amountToDrain = maxDrain;
		for (int tank = 0; tank < fluids.length && amountToDrain > 0; tank++) {
			FluidStack stored = fluids[tank];
			if (stored.isEmpty() || machine.canDrainFluid(tank, stored)) {
				continue;
			}
			if (!drained.isEmpty() && !drained.isFluidEqual(stored)) {
				continue;
			}

			FluidStack tankDrained = drainTank(tank, amountToDrain, action, true);
			if (tankDrained.isEmpty()) {
				continue;
			}
			if (drained.isEmpty()) {
				drained = tankDrained;
			} else {
				drained.grow(tankDrained.getAmount());
			}
			amountToDrain -= tankDrained.getAmount();
		}
		return drained;
	}

	public int fillTank(int tank, FluidStack resource, FluidAction action, boolean checkFillPolicy) {
		if (!isTankValid(tank) || resource.isEmpty()) {
			return 0;
		}
		if (checkFillPolicy && !machine.canFillFluid(tank, resource)) {
			return 0;
		}

		FluidStack stored = fluids[tank];
		if (!stored.isEmpty() && !stored.isFluidEqual(resource)) {
			return 0;
		}

		int capacity = getTankCapacity(tank);
		int space = stored.isEmpty() ? capacity : capacity - stored.getAmount();
		int filled = Math.min(space, resource.getAmount());
		if (filled <= 0) {
			return 0;
		}

		if (action.execute()) {
			if (stored.isEmpty()) {
				FluidStack copy = resource.copy();
				copy.setAmount(filled);
				fluids[tank] = copy;
			} else {
				stored.grow(filled);
			}
			onContentsChanged();
		}
		return filled;
	}

	public @NotNull FluidStack drainTank(int tank, int amount, FluidAction action, boolean checkDrainPolicy) {
		if (!isTankValid(tank) || amount <= 0 || fluids[tank].isEmpty()) {
			return FluidStack.EMPTY;
		}
		if (checkDrainPolicy && machine.canDrainFluid(tank, fluids[tank])) {
			return FluidStack.EMPTY;
		}

		FluidStack stored = fluids[tank];
		int drainedAmount = Math.min(amount, stored.getAmount());
		FluidStack drained = stored.copy();
		drained.setAmount(drainedAmount);

		if (action.execute()) {
			stored.shrink(drainedAmount);
			if (stored.isEmpty()) {
				fluids[tank] = FluidStack.EMPTY;
			}
			onContentsChanged();
		}
		return drained;
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		ListTag tanks = new ListTag();
		for (int tank = 0; tank < fluids.length; tank++) {
			if (fluids[tank].isEmpty()) {
				continue;
			}
			CompoundTag tankTag = new CompoundTag();
			tankTag.putInt("Tank", tank);
			fluids[tank].writeToNBT(tankTag);
			tanks.add(tankTag);
		}
		tag.put("Tanks", tanks);
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag tag) {
		Arrays.fill(fluids, FluidStack.EMPTY);

		ListTag tanks = tag.getList("Tanks", CompoundTag.TAG_COMPOUND);
		for (int index = 0; index < tanks.size(); index++) {
			CompoundTag tankTag = tanks.getCompound(index);
			int tank = tankTag.getInt("Tank");
			if (isTankValid(tank)) {
				fluids[tank] = FluidStack.loadFluidStackFromNBT(tankTag);
			}
		}
	}

	private boolean isTankValid(int tank) {
		return tank >= 0 && tank < fluids.length;
	}

	private void onContentsChanged() {
		machine.setChanged();
		machine.sync();
	}
}
