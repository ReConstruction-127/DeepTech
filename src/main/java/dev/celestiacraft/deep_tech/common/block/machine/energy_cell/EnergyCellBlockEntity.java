package dev.celestiacraft.deep_tech.common.block.machine.energy_cell;

import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Position;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.api.block.machine.MachineBlockEntity;
import dev.celestiacraft.deep_tech.api.gui.MachineItemSlots;
import dev.celestiacraft.deep_tech.api.gui.widget.EnergyBarWidget;
import dev.celestiacraft.deep_tech.common.register.block.MachineBlocks;
import dev.celestiacraft.deep_tech.config.common.machine.EnergyCellConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnergyCellBlockEntity extends MachineBlockEntity<EnergyCellBlockEntity> implements IUIHolder.BlockEntityUI {
	public EnergyCellBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		DeepTech.LOGGER.info("EnergyCell slots = {}", getItemHandler().getSlots());
	}


	@Override
	public int getMachineMaxEnergy() {
		return EnergyCellConfig.MAX_ENERGY.get();
	}

	@Override
	public int getMaxReceive() {
		return EnergyCellConfig.MAX_RECEIVE.get();
	}

	@Override
	public int getItemInputSlotCount() {
		return 1;
	}

	@Override
	public int getItemOutputSlotCount() {
		return 0;
	}

	@Override
	public int getMaxExtract() {
		return EnergyCellConfig.MAX_EXTRACT.get(); // ✅ 支持输出
	}
	@Override
	public int getMaxMachineSlot() {
		return 1; // 1 个输入槽
	}

	// ========== Capability：方向限制（一刀切） ==========

	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
		if (capability == ForgeCapabilities.ENERGY) {
			if (side == null) {
				return super.getCapability(capability, side);
			}

			// ✅ 顶部和底部：只输出
			if (side == Direction.UP || side == Direction.DOWN) {
				return LazyOptional.of(() -> new OutputOnlyEnergyStorage()).cast();
			}

			// ✅ 侧面：只输入
			return LazyOptional.of(() -> new InputOnlyEnergyStorage()).cast();
		}
		return super.getCapability(capability, side);
	}

	// ========== 内部类：侧面只输入 ==========

	private class InputOnlyEnergyStorage implements IEnergyStorage {
		@Override
		public int receiveEnergy(int maxReceive, boolean simulate) {
			int received = Math.min(maxReceive, getMachineMaxEnergy() - getEnergy());
			if (!simulate && received > 0) {
				setEnergy(getEnergy() + received);
				setChanged();
				sync();
			}
			return received;
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate) {
			return 0;
		}

		@Override
		public int getEnergyStored() {
			return getEnergy();
		}

		@Override
		public int getMaxEnergyStored() {
			return getMachineMaxEnergy();
		}

		@Override
		public boolean canExtract() {
			return false;
		}

		@Override
		public boolean canReceive() {
			return true;
		}
	}

	// ========== 内部类：顶部/底部只输出 ==========

	private class OutputOnlyEnergyStorage implements IEnergyStorage {
		@Override
		public int receiveEnergy(int maxReceive, boolean simulate) {
			return 0;
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate) {
			int extracted = Math.min(maxExtract, getEnergy());
			if (!simulate && extracted > 0) {
				setEnergy(getEnergy() - extracted);
				setChanged();
				sync();
			}
			return extracted;
		}

		@Override
		public int getEnergyStored() {
			return getEnergy();
		}

		@Override
		public int getMaxEnergyStored() {
			return getMachineMaxEnergy();
		}

		@Override
		public boolean canExtract() {
			return true;
		}

		@Override
		public boolean canReceive() {
			return false;
		}
	}

	// ========== Server Tick ==========

	@Override
	public void serverTick(Level level, BlockPos pos, BlockState state, EnergyCellBlockEntity entity) {
		if (level.isClientSide()) return;

		// ===== 主动推送：从顶部和底部向外推送 =====
		if (level.getGameTime() % 2 == 0 && getEnergy() > 0) {
			for (Direction dir : Direction.values()) {
				// ✅ 只从顶部和底部推送
				if (dir != Direction.UP && dir != Direction.DOWN) continue;

				BlockEntity target = level.getBlockEntity(pos.relative(dir));
				if (target == null) continue;

				target.getCapability(ForgeCapabilities.ENERGY, dir.getOpposite())
						.ifPresent(storage -> {
							int sent = storage.receiveEnergy(Math.min(getEnergy(), getMaxExtract()), false);
							if (sent > 0) {
								setEnergy(getEnergy() - sent);
								setChanged();
								sync();
							}
						});
			}
		}

		chargeItemInSlot();
	}

	private void chargeItemInSlot() {
		// ✅ 利用父类的 getItemHandler()
		var handler = getItemHandler();
		if (handler.getSlots() <= 0) {
			DeepTech.LOGGER.warn("EnergyCell: No slots");
			return;
		}

		ItemStack stack = handler.getStackInSlot(0);
		if (stack.isEmpty()) return;

		stack.getCapability(ForgeCapabilities.ENERGY, null).ifPresent(itemEnergy -> {
			if (!itemEnergy.canReceive()) return;

			int maxCharge = EnergyCellConfig.MAX_CHARGE.get();
			int available = Math.min(getEnergy(), maxCharge);
			int used = itemEnergy.receiveEnergy(available, false);

			if (used > 0) {
				setEnergy(getEnergy() - used);
				setChanged();
				sync();
			}
		});
	}


	@Override
	public ModularUI createUI(Player player) {
		ModularUI ui = new ModularUI(176, 166, this, player);
		ui.widget(createUIWidget(player));
		return ui;
	}

	private WidgetGroup createUIWidget(Player player) {
		WidgetGroup group = new WidgetGroup(0, 0, 176, 166);
		group.setBackground(new ResourceTexture(DeepTech.loadGui("energy_cell")));

		LabelWidget title = new LabelWidget(
				8,
				8,
				MachineBlocks.ENERGY_CELL.get().getName()
		);
		title.setColor(0xFF5D5F60);
		group.addWidget(title);

		group.addWidget(new EnergyBarWidget(
				43,
				25,
				this::getEnergyStored,
				getMaxEnergyStored()
		));

		// 根据配置动态生成物品槽位: 输入/输出槽数量为 0 时不会创建任何 widget
		MachineItemSlots.add(
				group,
				this,
				getItemHandler(),
				new Position(97, 38),
				null
		);

		addPlayerInventory(group, player);
		return group;
	}
}