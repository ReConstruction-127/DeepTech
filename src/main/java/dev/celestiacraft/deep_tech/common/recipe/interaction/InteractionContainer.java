package dev.celestiacraft.deep_tech.common.recipe.interaction;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public record InteractionContainer(ItemStack triggerItem, BlockState targetState) implements Container {
	// 所有方法可空实现，因为只用于 matches
	@Override public int getContainerSize() { return 0; }
	@Override public boolean isEmpty() { return true; }
	@Override public ItemStack getItem(int slot) { return ItemStack.EMPTY; }
	@Override public ItemStack removeItem(int slot, int amount) { return ItemStack.EMPTY; }
	@Override public ItemStack removeItemNoUpdate(int slot) { return ItemStack.EMPTY; }
	@Override public void setItem(int slot, ItemStack stack) {}
	@Override public void setChanged() {}
	@Override public boolean stillValid(net.minecraft.world.entity.player.Player player) { return true; }
	@Override public void clearContent() {}
}