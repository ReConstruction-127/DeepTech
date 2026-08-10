package dev.celestiacraft.deep_tech.common.block.machine.sculk_network.reservoir;

import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.widget.SlotWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import dev.celestiacraft.deep_tech.common.inventory.SimpleMachineInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class SNItemReservoirBlockEntity extends BlockEntity implements IUIHolder.BlockEntityUI {
	public SNItemReservoirBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// ========== 54 格物品栏 ==========
	private final ItemStackHandler inventory = new ItemStackHandler(54) {
		@Override
		protected void onContentsChanged(int slot) {
			setChanged();
		}
	};

	private final LazyOptional<IItemHandler> inventoryCap = LazyOptional.of(() -> inventory);

	@Override
	public ModularUI createUI(Player player) {
		WidgetGroup group = new WidgetGroup(0, 0, 176, 222);

		// 物品槽位（6 行 × 9 列 = 54 格）
		SimpleMachineInventory container = new SimpleMachineInventory(inventory);
		int index = 0;
		for (int row = 0; row < 6; row++) {
			for (int col = 0; col < 9; col++) {
				int x = 8 + col * 18;
				int y = 18 + row * 18;
				SlotWidget slot = new SlotWidget(container, index, x, y, true, true);
				group.addWidget(slot);
				index++;
			}
		}

		// 玩家背包（3 行 × 9 列）
		addPlayerInventory(group, player);

		ModularUI ui = new ModularUI(176, 222, this, player);
		ui.widget(group);
		return ui;
	}

	protected void addPlayerInventory(WidgetGroup group, Player player) {
		Container inventory = player.getInventory();

		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 9; col++) {
				SlotWidget slot = new SlotWidget(inventory, col + row * 9 + 9, 8 + col * 18, 140 + row * 18, true, true);
				slot.isPlayerContainer = true;
				group.addWidget(slot);
			}
		}

		for (int col = 0; col < 9; col++) {
			SlotWidget slot = new SlotWidget(inventory, col, 8 + col * 18, 198, true, true);
			slot.isPlayerContainer = true;
			slot.isPlayerHotBar = true;
			group.addWidget(slot);
		}
	}

	// ========== IItemHandler 暴露 ==========
	@Override
	public <T> LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> cap, @Nullable Direction side) {
		if (cap == ForgeCapabilities.ITEM_HANDLER) {
			return inventoryCap.cast();
		}
		return super.getCapability(cap, side);
	}

	public ItemStackHandler getInventory() {
		return inventory;
	}

	// ========== NBT ==========
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("Inventory", inventory.serializeNBT());
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		inventory.deserializeNBT(tag.getCompound("Inventory"));
	}
}
