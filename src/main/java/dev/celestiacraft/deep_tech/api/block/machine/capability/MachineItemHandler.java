package dev.celestiacraft.deep_tech.api.block.machine.capability;

import dev.celestiacraft.deep_tech.api.block.machine.MachineBlockEntity;
import net.minecraft.nbt.CompoundTag;
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

	/**
	 * 存档里的 "Size" 是保存时的槽位数量, 反序列化会按它把 handler 改回旧大小.
	 * <p>
	 * 机器改过槽位数量时(比如能量单元从 1 个槽加到 2 个), 旧存档会把 handler 缩回旧数量,
	 * 之后访问新槽位就会直接越界崩掉, 所以这里按当前配置纠正回来.
	 */
	@Override
	public void deserializeNBT(@NotNull CompoundTag nbt) {
		super.deserializeNBT(nbt);
		if (getSlots() != machine.getMaxMachineSlot()) {
			setSize(machine.getMaxMachineSlot());
		}
	}

	@Override
	protected void onContentsChanged(int slot) {
		machine.setChanged();
	}
}
