package dev.celestiacraft.deep_tech.common.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class SimpleMachineInventory implements Container {
	private final ItemStackHandler handler;

	public SimpleMachineInventory(ItemStackHandler handler) {
		this.handler = handler;
	}

	@Override
	public int getContainerSize() {
		return handler.getSlots();
	}

	@Override
	public boolean isEmpty() {
		for (int i = 0; i < handler.getSlots(); i++) {
			if (!handler.getStackInSlot(i).isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public @NotNull ItemStack getItem(int slot) {
		return handler.getStackInSlot(slot);
	}

	@Override
	public @NotNull ItemStack removeItem(int slot, int amount) {
		/*
		 * 玩家在 GUI 中用鼠标取物时绕过 canExtractItem 策略, 直接操作存储
		 * canExtractItem 仍然约束漏斗/管道等外部自动化抽取
		 */
		ItemStack stack = handler.getStackInSlot(slot);
		if (stack.isEmpty() || amount <= 0) {
			return ItemStack.EMPTY;
		}
		ItemStack removed = stack.copy();
		removed.setCount(Math.min(amount, stack.getCount()));
		stack.shrink(removed.getCount());
		handler.setStackInSlot(slot, stack);
		return removed;
	}

	@Override
	public @NotNull ItemStack removeItemNoUpdate(int slot) {
		ItemStack stack = handler.getStackInSlot(slot);
		handler.setStackInSlot(slot, ItemStack.EMPTY);
		return stack;
	}

	@Override
	public void setItem(int slot, @NotNull ItemStack stack) {
		handler.setStackInSlot(slot, stack);
	}

	@Override
	public void setChanged() {
	}

	@Override
	public boolean stillValid(@NotNull Player player) {
		return true;
	}

	@Override
	public void clearContent() {
		for (int i = 0; i < handler.getSlots(); i++) {
			handler.setStackInSlot(i, ItemStack.EMPTY);
		}
	}
}