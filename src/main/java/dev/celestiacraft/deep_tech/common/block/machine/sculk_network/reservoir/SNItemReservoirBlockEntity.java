package dev.celestiacraft.deep_tech.common.block.machine.sculk_network.reservoir;

import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.SlotWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Position;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.common.inventory.SimpleMachineInventory;
import dev.celestiacraft.deep_tech.common.register.block.MachineBlocks;
import dev.celestiacraft.libs.api.register.block.BasicBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SNItemReservoirBlockEntity extends BasicBlockEntity implements IUIHolder.BlockEntityUI {
	public SNItemReservoirBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// ========== 54 格物品栏 ==========
	private final ItemStackHandler inventory = new ItemStackHandler(54) {
		@Override
		protected void onContentsChanged(int slot) {
			markDirty();
		}
	};

	private final LazyOptional<IItemHandler> inventoryCap = LazyOptional.of(() -> inventory);

	// ============================================================
	//  LDLib GUI 核心方法
	// ============================================================



	@Override
	public ModularUI createUI(Player player) {
		ModularUI ui = new ModularUI(176, 222, this, player);
		WidgetGroup main = new WidgetGroup(0, 0, 176, 222);
		main.setBackground(new ResourceTexture(DeepTech.loadGui("item_reservoir")));

		LabelWidget title = new LabelWidget(8, 8, MachineBlocks.SN_ITEM_RESERVOIR.get().getName());
		title.setColor(0xFF5D5F60);
		main.addWidget(title);

		// 储存器槽位（6行×9列）
		int index = 0;
		for (int row = 0; row < 6; row++) {
			for (int col = 0; col < 9; col++) {
				int x = 8 + col * 18;
				int y = 18 + row * 18;
				// 与机器槽位一致:new + setContainerSlot(SimpleMachineInventory 包装 ItemStackHandler)
				addMachineSlot(main, new SimpleMachineInventory(inventory), index, x, y);
				index++;
			}
		}

		// 玩家背包（3行）
		Container playerInv = player.getInventory();
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 9; col++) {
				int x = 8 + col * 18;
				int y = 140 + row * 18;
				addPlayerSlot(main, playerInv, 9 + row * 9 + col, x, y);
			}
		}

		// 快捷栏（1行）
		for (int col = 0; col < 9; col++) {
			int x = 8 + col * 18;
			int y = 198;
			addPlayerSlot(main, playerInv, col, x, y);
		}

		return ui.widget(main);
	}

	// 机器槽位（与 MachineItemSlots.createSlot 一致,isPlayerContainer 默认为 false）
	private void addMachineSlot(WidgetGroup group, Container container, int slotIndex, int x, int y) {
		SlotWidget slot = new SlotWidget();
		slot.setContainerSlot(container, slotIndex);
		slot.setSelfPosition(new Position(x, y));
		slot.setBackground((ResourceTexture) null);
		slot.setCanTakeItems(true);
		slot.setCanPutItems(true);
		group.addWidget(slot);
	}

	// 玩家槽位（与 MachineBlockEntity.addPlayerInventory 一致,isPlayerContainer 必须为 true,
	// shift-click 时 LDLib 才把这里当作玩家背包目标槽）
	private void addPlayerSlot(WidgetGroup group, Container container, int slotIndex, int x, int y) {
		SlotWidget slot = new SlotWidget();
		slot.initTemplate();
		slot.setContainerSlot(container, slotIndex);
		slot.isPlayerContainer = true;
		slot.setSelfPosition(new Position(x, y));
		slot.setBackground((ResourceTexture) null);
		group.addWidget(slot);
	}

	@Override
	public boolean isInvalid() {
		return isRemoved();
	}

	@Override
	public boolean isRemote() {
		return isClient();
	}

	@Override
	public void markAsDirty() {
		markDirty();
	}

	// ============================================================
	//  IItemHandler 暴露
	// ============================================================

	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (cap == ForgeCapabilities.ITEM_HANDLER) {
			return inventoryCap.cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	protected void onCapsInvalidated() {
		super.onCapsInvalidated();
		inventoryCap.invalidate();
	}

	// ============================================================
	//  NBT 持久化
	// ============================================================

	@Override
	protected void write(CompoundTag tag) {
		tag.put("Inventory", inventory.serializeNBT());
	}

	@Override
	protected void read(CompoundTag tag) {
		inventory.deserializeNBT(tag.getCompound("Inventory"));
	}
}