package dev.celestiacraft.deep_tech.common.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class MachineMenu extends AbstractContainerMenu {
	private final IItemHandler inventory;

	public MachineMenu(int id, Inventory playerInventory, IItemHandler inventory) {
		super(null, id);
		this.inventory = inventory;

		// 输入槽
		addSlot(new SlotItemHandler(inventory, 0, 40, 35));
		// 输出槽
		addSlot(new SlotItemHandler(inventory, 1, 120, 35));
	}

	@Override
	public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean stillValid(@NotNull Player player) {
		return true;
	}
}