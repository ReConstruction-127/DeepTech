package dev.celestiacraft.deep_tech.common.block.machine.sculk_network.reservoir;

import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.widget.ButtonWidget;
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
import net.minecraftforge.common.capabilities.Capability;
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

	// ============================================================
	//  LDLib GUI 核心方法
	// ============================================================



	@Override
	public ModularUI createUI(Player player) {
		WidgetGroup main = new WidgetGroup(0, 0, 176, 222);

		// 背景（可用纯色或纹理）
		main.addWidget(new ButtonWidget(0, 0, 176, 222, btn -> {}));

		// 储存器槽位（6行×9列）
		int index = 0;
		for (int row = 0; row < 6; row++) {
			for (int col = 0; col < 9; col++) {
				int x = 8 + col * 18;
				int y = 18 + row * 18;
				// SlotWidget 需要 Container 或 IItemTransfer，用 SimpleMachineInventory 包装 ItemStackHandler
				SlotWidget slot = new SlotWidget(new SimpleMachineInventory(inventory), index, x, y, true, true)
						.setBackgroundTexture(IGuiTexture.EMPTY);
				main.addWidget(slot);
				index++;
			}
		}

		// 玩家背包（3行）
		Container playerInv = player.getInventory();
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 9; col++) {
				int x = 8 + col * 18;
				int y = 140 + row * 18;
				SlotWidget slot = new SlotWidget(playerInv, 9 + row * 9 + col, x, y, true, true)
						.setBackgroundTexture(IGuiTexture.EMPTY);
				main.addWidget(slot);
			}
		}

		// 快捷栏（1行）
		for (int col = 0; col < 9; col++) {
			int x = 8 + col * 18;
			int y = 198;
			SlotWidget slot = new SlotWidget(playerInv, col, x, y, true, true)
					.setBackgroundTexture(IGuiTexture.EMPTY);
			main.addWidget(slot);
		}

		return new ModularUI(176, 222, this, player).widget(main);
	}

	@Override
	public boolean isInvalid() {
		return this.isRemoved();
	}

	@Override
	public boolean isRemote() {
		return this.level != null && this.level.isClientSide;
	}

	@Override
	public void markAsDirty() {
		this.setChanged();
	}

	// ============================================================
	//  同步方法（LDLib 自动同步）
	// ============================================================

	private void sync() {
		if (level != null && !level.isClientSide) {
			level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
		}
	}

	// ============================================================
	//  IItemHandler 暴露
	// ============================================================

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
		if (cap == ForgeCapabilities.ITEM_HANDLER) {
			return inventoryCap.cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		inventoryCap.invalidate();
	}

	// ============================================================
	//  NBT 持久化
	// ============================================================

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