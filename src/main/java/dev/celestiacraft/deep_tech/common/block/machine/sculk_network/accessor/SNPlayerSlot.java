package dev.celestiacraft.deep_tech.common.block.machine.sculk_network.accessor;

import com.lowdragmc.lowdraglib.gui.widget.SlotWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * 玩家背包槽位:Shift+单击(左键)= 该格物品整组存入网络(箱子语义)。
 * 普通单击/拖拽/双击仍走原版槽位逻辑。
 */
public class SNPlayerSlot extends SlotWidget {

	private static final int CLICK_ID = 1;

	private final SNAccessorBlockEntity accessor;
	private final int invIndex;

	public SNPlayerSlot(SNAccessorBlockEntity accessor, int invIndex) {
		this.accessor = accessor;
		this.invIndex = invIndex;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button != 0 || !isMouseOverElement(mouseX, mouseY) || !Screen.hasShiftDown()) {
			return super.mouseClicked(mouseX, mouseY, button);
		}
		writeClientAction(CLICK_ID, buf -> buf.writeVarInt(invIndex));
		return true;
	}

	@Override
	public void handleClientAction(int id, FriendlyByteBuf buffer) {
		if (id != CLICK_ID) {
			return;
		}
		Player player = getGui() == null ? null : getGui().entityPlayer;
		if (player == null || player.level().isClientSide) {
			return;
		}
		int index = buffer.readVarInt();
		ItemStack stack = player.getInventory().getItem(index);
		if (stack.isEmpty()) {
			return;
		}
		int inserted = accessor.insertItem(stack.copy(), false);
		if (inserted <= 0) {
			return;
		}
		stack.shrink(inserted);
		if (stack.isEmpty()) {
			player.getInventory().setItem(index, ItemStack.EMPTY);
		}
	}
}