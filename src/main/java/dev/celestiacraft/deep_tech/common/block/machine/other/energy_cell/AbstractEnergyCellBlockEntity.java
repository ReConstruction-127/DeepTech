package dev.celestiacraft.deep_tech.common.block.machine.other.energy_cell;

import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Position;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.api.block.machine.MachineBlockEntity;
import dev.celestiacraft.deep_tech.api.block.machine.capability.MachineItemHandler;
import dev.celestiacraft.deep_tech.api.gui.MachineItemSlots;
import dev.celestiacraft.deep_tech.api.gui.widget.EnergyBarWidget;
import dev.celestiacraft.deep_tech.common.block.machine.other.energy_cell.capability.InputOnlyEnergyStorage;
import dev.celestiacraft.deep_tech.common.block.machine.other.energy_cell.capability.OutputOnlyEnergyStorage;
import dev.celestiacraft.deep_tech.common.register.block.MachineBlocks;
import dev.celestiacraft.deep_tech.config.common.machine.other.EnergyCellConfig;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractEnergyCellBlockEntity extends MachineBlockEntity<AbstractEnergyCellBlockEntity> implements IUIHolder.BlockEntityUI {
	public AbstractEnergyCellBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	/**
	 * 两个输入槽:
	 * <ul>
	 *     <li>下标 0: 给槽里的物品充电(能量单元 -> 物品)</li>
	 *     <li>下标 1: 给能量单元充电(物品 -> 能量单元)</li>
	 * </ul>
	 */
	@Override
	public int getItemInputSlotCount() {
		return 2;
	}

	@Override
	public int getMaxExtract() {
		return EnergyCellConfig.MAX_EXTRACT.get();
	}

	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
		if (capability == ForgeCapabilities.ENERGY) {
			if (direction == null) {
				return super.getCapability(capability, direction);
			}

			if (direction == Direction.UP || direction == Direction.DOWN) {
				return LazyOptional.of(() -> {
					return new OutputOnlyEnergyStorage(this);
				}).cast();
			}

			return LazyOptional.of(() -> {
				return new InputOnlyEnergyStorage(this);
			}).cast();
		}

		return super.getCapability(capability, direction);
	}

	@Override
	public void serverTick(Level level, BlockPos pos, BlockState state, AbstractEnergyCellBlockEntity entity) {
		if (level.isClientSide()) {
			return;
		}

		if (level.getGameTime() % 2 == 0 && getEnergy() > 0) {
			for (Direction direction : Direction.values()) {
				if (direction != Direction.UP && direction != Direction.DOWN) {
					continue;
				}

				BlockEntity target = level.getBlockEntity(pos.relative(direction));
				if (target == null) {
					continue;
				}

				target.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite())
						.ifPresent((storage) -> {
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
		dischargeItemInSlot();
	}

	/**
	 * 把下标 1 槽位里物品的能量抽出来充自己(物品 -> 能量单元).
	 * <p>
	 * 速率复用 {@link EnergyCellConfig#MAX_CHARGE}, 和反方向的充电保持一致.
	 */
	private void dischargeItemInSlot() {
		MachineItemHandler handler = getItemHandler();
		if (handler.getSlots() < 2) {
			return;
		}

		int space = getMachineMaxEnergy() - getEnergy();
		if (space <= 0) {
			return;
		}

		ItemStack stack = handler.getStackInSlot(1);
		if (stack.isEmpty()) {
			return;
		}

		stack.getCapability(ForgeCapabilities.ENERGY, null).ifPresent(itemEnergy -> {
			if (!itemEnergy.canExtract()) {
				return;
			}

			int moved = itemEnergy.extractEnergy(Math.min(space, EnergyCellConfig.MAX_CHARGE.get()), false);
			if (moved > 0) {
				setEnergy(getEnergy() + moved);
				setChanged();
				sync();
			}
		});
	}

	private void chargeItemInSlot() {
		MachineItemHandler handler = getItemHandler();
		if (handler.getSlots() <= 0) {
			DeepTech.LOGGER.warn("EnergyCell: No slots");
			return;
		}

		ItemStack stack = handler.getStackInSlot(0);
		if (stack.isEmpty()) {
			return;
		}

		stack.getCapability(ForgeCapabilities.ENERGY, null).ifPresent(itemEnergy -> {
			if (!itemEnergy.canReceive()) {
				return;
			}

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

		// 下标 0: 给物品充电
		MachineItemSlots.addSlot(
				group,
				getItemHandler(),
				0,
				new Position(97, 28),
				true,
				true
		);

		// 下标 1: 给能量单元充电, 位置在上一格正下方(贴图还没画这一格的框, 先留空)
		MachineItemSlots.addSlot(
				group,
				getItemHandler(),
				1,
				new Position(97, 46),
				true,
				true
		);

		addPlayerInventory(group, player);
		return group;
	}
}