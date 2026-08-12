package dev.celestiacraft.deep_tech.common.block.machine.sculk_network.reservoir;

import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.SlotWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Position;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.api.fluid.SingleTankFluidTransfer;
import dev.celestiacraft.deep_tech.api.gui.widget.ProportionalTankWidget;
import dev.celestiacraft.deep_tech.common.register.block.MachineBlocks;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SNFluidReservoirBlockEntity extends BlockEntity implements IUIHolder.BlockEntityUI {
	// 9 个 8B 储罐 (8 * 1000 = 8000 mB),总容量 72B = 72000 mB
	public static final int TANK_COUNT = 9;
	public static final int TANK_CAPACITY = 8 * 1000;

	private final FluidTank[] tanks = new FluidTank[TANK_COUNT];

	// 聚合所有储罐的 IFluidHandler(填装时按顺序,抽取时从后往前)
	private final IFluidHandler tankHandler = new IFluidHandler() {
		@Override
		public int getTanks() {
			return TANK_COUNT;
		}

		@Override
		public @NotNull FluidStack getFluidInTank(int tank) {
			if (tank < 0 || tank >= TANK_COUNT) {
				return FluidStack.EMPTY;
			}
			return tanks[tank].getFluid();
		}

		@Override
		public int getTankCapacity(int tank) {
			if (tank < 0 || tank >= TANK_COUNT) {
				return 0;
			}
			return tanks[tank].getCapacity();
		}

		@Override
		public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
			return tank >= 0 && tank < TANK_COUNT && tanks[tank].isFluidValid(stack);
		}

		@Override
		public int fill(FluidStack resource, FluidAction action) {
			if (resource.isEmpty()) {
				return 0;
			}
			int filledTotal = 0;
			FluidStack remaining = resource.copy();
			for (FluidTank tank : tanks) {
				if (remaining.isEmpty()) {
					break;
				}
				int filled = tank.fill(remaining, action);
				filledTotal += filled;
				if (filled > 0) {
					remaining.shrink(filled);
				}
			}
			return filledTotal;
		}

		@Override
		public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
			if (resource.isEmpty()) {
				return FluidStack.EMPTY;
			}
			FluidStack drainedTotal = FluidStack.EMPTY;
			for (int i = TANK_COUNT - 1; i >= 0; i--) {
				if (drainedTotal.getAmount() >= resource.getAmount()) {
					break;
				}
				FluidStack tankFluid = tanks[i].getFluid();
				if (tankFluid.isEmpty() || !tankFluid.isFluidEqual(resource)) {
					continue;
				}
				FluidStack drained = tanks[i].drain(resource.getAmount() - drainedTotal.getAmount(), action);
				if (drained.isEmpty()) {
					continue;
				}
				if (drainedTotal.isEmpty()) {
					drainedTotal = drained.copy();
				} else {
					drainedTotal.grow(drained.getAmount());
				}
			}
			return drainedTotal;
		}

		@Override
		public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
			if (maxDrain <= 0) {
				return FluidStack.EMPTY;
			}
			int drainedTotal = 0;
			FluidStack result = FluidStack.EMPTY;
			for (int i = TANK_COUNT - 1; i >= 0; i--) {
				if (drainedTotal >= maxDrain) {
					break;
				}
				if (tanks[i].getFluid().isEmpty()) {
					continue;
				}
				FluidStack drained = tanks[i].drain(maxDrain - drainedTotal, action);
				if (drained.isEmpty()) {
					continue;
				}
				drainedTotal += drained.getAmount();
				if (result.isEmpty()) {
					result = drained.copy();
				} else {
					result.grow(drained.getAmount());
				}
			}
			return result;
		}
	};

	private final LazyOptional<IFluidHandler> tankCap = LazyOptional.of(() -> tankHandler);

	public SNFluidReservoirBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		for (int i = 0; i < TANK_COUNT; i++) {
			tanks[i] = new FluidTank(TANK_CAPACITY) {
				@Override
				protected void onContentsChanged() {
					setChanged();
					sync();
				}
			};
		}
	}

	// ========== 储罐访问(供中枢传输与 UI 使用) ==========
	public IFluidHandler getTank() {
		return tankHandler;
	}

	public int getTankCount() {
		return TANK_COUNT;
	}

	public FluidTank getFluidTank(int index) {
		return tanks[index];
	}

	public int getTankAmount(int index) {
		return tanks[index].getFluidAmount();
	}

	public FluidStack getTankFluid(int index) {
		return tanks[index].getFluid();
	}

	public int getTankCapacity(int index) {
		return tanks[index].getCapacity();
	}

	// ========== Capability ==========
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
		if (cap == ForgeCapabilities.FLUID_HANDLER) {
			return tankCap.cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		tankCap.invalidate();
	}

	// ========== NBT ==========
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		for (int i = 0; i < TANK_COUNT; i++) {
			tag.put("Tank" + i, tanks[i].writeToNBT(new CompoundTag()));
		}
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		for (int i = 0; i < TANK_COUNT; i++) {
			tanks[i].readFromNBT(tag.getCompound("Tank" + i));
		}
	}

	// ========== 同步 ==========
	private void sync() {
		if (level != null && !level.isClientSide) {
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
		}
	}

	// ============================================================
	//  LDLib GUI(仿照其他机器 UI 样式)
	// ============================================================

	@Override
	public ModularUI createUI(Player player) {
		ModularUI ui = new ModularUI(176, 166, this, player);
		ui.widget(createUIWidget(player));
		return ui;
	}

	private WidgetGroup createUIWidget(Player player) {
		WidgetGroup group = new WidgetGroup(0, 0, 176, 166);
		group.setBackground(new ResourceTexture(DeepTech.loadGui("fluid_reservoir")));

		LabelWidget title = new LabelWidget(8, 8, MachineBlocks.SN_FLUID_RESERVOIR.get().getName());
		title.setColor(0xFF5D5F60);
		group.addWidget(title);

		// 9 个 8B 储罐:比例填充式流体槽,支持拿着桶点击槽位灌入/抽取。
		// LDLib TankWidget 的桶点击是对整个 IFluidTransfer 做 fill/drain(不区分罐索引),
		// 因此每个槽位必须绑定一个只暴露该罐的 SingleTankFluidTransfer,点击才精确命中对应罐。
		for (int i = 0; i < TANK_COUNT; i++) {
			group.addWidget(new ProportionalTankWidget(new SingleTankFluidTransfer(tanks[i]), 0, 8 + i * 18, 26, 16, 40, true, true)
					.setBackground(new ResourceTexture(DeepTech.loadGui("elements/tank_back"))));
		}

		addPlayerInventory(group, player);
		return group;
	}

	private void addPlayerInventory(WidgetGroup group, Player player) {
		Container inventory = player.getInventory();

		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 9; col++) {
				SlotWidget slot = new SlotWidget();
				slot.initTemplate();
				slot.setContainerSlot(inventory, col + row * 9 + 9);
				slot.isPlayerContainer = true;
				slot.setSelfPosition(new Position(7 + col * 18, 81 + row * 18));
				slot.setBackground((ResourceTexture) null);
				group.addWidget(slot);
			}
		}

		for (int col = 0; col < 9; col++) {
			SlotWidget slot = new SlotWidget();
			slot.initTemplate();
			slot.setContainerSlot(inventory, col);
			slot.isPlayerContainer = true;
			slot.setSelfPosition(new Position(7 + col * 18, 139));
			slot.setBackground((ResourceTexture) null);
			group.addWidget(slot);
		}
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
}