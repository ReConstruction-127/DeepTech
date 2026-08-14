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
import dev.celestiacraft.deep_tech.common.block.machine.sculk_network.reservoir.capability.SNFluidReservoirTank;
import dev.celestiacraft.deep_tech.common.block.machine.sculk_network.reservoir.capability.SNFluidReservoirTankHandler;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SNFluidReservoirBlockEntity extends BasicBlockEntity implements IUIHolder.BlockEntityUI {
	// 9 个 8B 储罐 (8 * 1000 = 8000 mB),总容量 72B = 72000 mB
	public static final int TANK_COUNT = 9;
	public static final int TANK_CAPACITY = 8 * 1000;

	private final FluidTank[] tanks = new FluidTank[TANK_COUNT];

	private final IFluidHandler tankHandler = new SNFluidReservoirTankHandler(tanks);
	private final LazyOptional<IFluidHandler> tankCap = LazyOptional.of(() -> tankHandler);

	public SNFluidReservoirBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		for (int i = 0; i < TANK_COUNT; i++) {
			tanks[i] = new SNFluidReservoirTank(TANK_CAPACITY, this);
		}
	}

	public IFluidHandler getTank() {
		return tankHandler;
	}

	/**
	 * 储罐内容变化回调:由 SNFluidReservoirTank 调用, 标记脏数据并同步客户端
	 */
	public void onTankContentChanged() {
		markDirtyAndUpdate();
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

	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
		if (capability == ForgeCapabilities.FLUID_HANDLER) {
			return tankCap.cast();
		}
		return super.getCapability(capability, direction);
	}

	@Override
	protected void onCapsInvalidated() {
		super.onCapsInvalidated();
		tankCap.invalidate();
	}

	@Override
	protected void write(CompoundTag tag) {
		for (int i = 0; i < TANK_COUNT; i++) {
			tag.put("Tank" + i, tanks[i].writeToNBT(new CompoundTag()));
		}
	}

	@Override
	protected void read(CompoundTag tag) {
		for (int i = 0; i < TANK_COUNT; i++) {
			tanks[i].readFromNBT(tag.getCompound("Tank" + i));
		}
	}

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

		// 9 个 8B 储罐:比例填充式流体槽,支持拿着桶点击槽位灌入/抽取. 
		// LDLib TankWidget 的桶点击是对整个 IFluidTransfer 做 fill/drain(不区分罐索引),
		// 因此每个槽位必须绑定一个只暴露该罐的 SingleTankFluidTransfer,点击才精确命中对应罐. 
		for (int i = 0; i < TANK_COUNT; i++) {
			group.addWidget(new ProportionalTankWidget(new SingleTankFluidTransfer(
					tanks[i]),
					0,
					8 + i * 18,
					26,
					16,
					40,
					true,
					true
			).setBackground(new ResourceTexture(DeepTech.loadGui("elements/tank_back"))));
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
}