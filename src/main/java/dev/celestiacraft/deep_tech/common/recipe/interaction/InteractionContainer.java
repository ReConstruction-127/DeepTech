package dev.celestiacraft.deep_tech.common.recipe.interaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

@Getter
@AllArgsConstructor
public class InteractionContainer implements Container {
	ItemStack triggerItem;
	BlockState targetState;

	@Override
	public int getContainerSize() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public @NotNull ItemStack getItem(int slot) {
		return ItemStack.EMPTY;
	}

	@Override
	public @NotNull ItemStack removeItem(int slot, int amount) {
		return ItemStack.EMPTY;
	}

	@Override
	public @NotNull ItemStack removeItemNoUpdate(int slot) {
		return ItemStack.EMPTY;
	}

	@Override
	public void setItem(int slot, @NotNull ItemStack stack) {
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
	}
}