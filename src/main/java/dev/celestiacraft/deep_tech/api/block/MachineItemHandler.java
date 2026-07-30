package dev.celestiacraft.deep_tech.api.block;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class MachineItemHandler extends ItemStackHandler {
	private final MachineBlockEntity<?> machine;

	public MachineItemHandler(MachineBlockEntity<?> machine) {
		this.machine = machine;
	}

	@Override
	public int getSlots() {
		return machine.getInventory().getSlots();
	}

	@Override
	public @NotNull ItemStack getStackInSlot(int slot) {
		return machine.getInventory().getStackInSlot(slot);
	}

	@Override
	public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		if (slot != 0) return stack;
		return machine.getInventory().insertItem(slot, stack, simulate);
	}

	@Override
	public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (slot != 1) return ItemStack.EMPTY;
		return machine.getInventory().extractItem(slot, amount, simulate);
	}

	@Override
	public int getSlotLimit(int slot) {
		return machine.getInventory().getSlotLimit(slot);
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return slot == 0 && machine.getInventory().isItemValid(slot, stack);
	}

	@Override
	protected void onContentsChanged(int slot) {
		machine.setChanged();
	}
}