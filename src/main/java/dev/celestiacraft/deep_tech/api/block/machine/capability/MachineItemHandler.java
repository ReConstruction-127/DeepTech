package dev.celestiacraft.deep_tech.api.block.machine.capability;

import dev.celestiacraft.deep_tech.api.block.machine.MachineBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class MachineItemHandler extends ItemStackHandler {
	private final MachineBlockEntity<?> machine;

	public MachineItemHandler(MachineBlockEntity<?> machine) {
		super(machine.getMaxMachineSlot());
		this.machine = machine;
	}

	@Override
	public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
		if (!machine.canInsertItem(slot, stack)) return stack;
		return super.insertItem(slot, stack, simulate);
	}

	@Override
	public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
		ItemStack stack = super.getStackInSlot(slot);
		if (!machine.canExtractItem(slot, stack)) return ItemStack.EMPTY;
		return super.extractItem(slot, amount, simulate);
	}

	@Override
	public int getSlotLimit(int slot) {
		return machine.getMachineSlotLimit(slot);
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		return machine.canInsertItem(slot, stack);
	}

	@Override
	protected void onContentsChanged(int slot) {
		machine.setChanged();
	}
}
