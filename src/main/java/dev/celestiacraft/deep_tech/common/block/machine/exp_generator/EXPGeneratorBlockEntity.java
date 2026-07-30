package dev.celestiacraft.deep_tech.common.block.machine.exp_generator;

import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.SlotWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Position;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.api.block.MachineBlockEntity;
import dev.celestiacraft.deep_tech.api.gui.EnergyBarWidget;
import dev.celestiacraft.deep_tech.api.gui.FluidBarWidget;
import dev.celestiacraft.deep_tech.common.inventory.SimpleMachineInventory;
import dev.celestiacraft.deep_tech.common.register.DTFluids;
import dev.celestiacraft.deep_tech.common.register.block.MachineBlocks;
import dev.celestiacraft.deep_tech.config.common.machine.EXPGeneratorConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

public class EXPGeneratorBlockEntity extends MachineBlockEntity<EXPGeneratorBlockEntity> implements IUIHolder.BlockEntityUI {
	private final SimpleMachineInventory inventoryWrapper;
	private int syncCounter = 0;

	// ✅ 延迟初始化
	private FluidTank fluidTank;
	private LazyOptional<IFluidHandler> fluidCap;

	public EXPGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		inventoryWrapper = new SimpleMachineInventory(getInventory());
	}

	@Override
	public int getMaxExtract() {
		return EXPGeneratorConfig.MAX_EXTRACT.get();
	}

	@Override
	public int getMachineMaxEnergy() {
		return EXPGeneratorConfig.MAX_ENERGY.get();
	}

	@Override
	public int getMaxReceive() {
		return 0;
	}

	@Override
	public int getItemInputSlotCount() {
		return 1;
	}

	@Override
	public int getItemOutputSlotCount() {
		return 0;
	}

	// ----- 延迟初始化 -----
	private void initFluidTank() {
		if (fluidTank == null) {
			int capacity = EXPGeneratorConfig.FLUID_CAPACITY.get();
			fluidTank = new FluidTank(capacity) {
				@Override
				protected void onContentsChanged() {
					setChanged();
					if (level != null && !level.isClientSide) {
						sync();
					}
				}
			};
			fluidCap = LazyOptional.of(() -> fluidTank);
		}
	}

	// ----- 流体能力暴露 -----
	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
		initFluidTank();
		if (cap == ForgeCapabilities.FLUID_HANDLER) {
			return fluidCap.cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		if (fluidCap != null) {
			fluidCap.invalidate();
		}
	}

	// ----- 持久化 -----
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag) {
		super.saveAdditional(tag);
		if (fluidTank != null) {
			CompoundTag fluidTag = new CompoundTag();
			fluidTank.writeToNBT(fluidTag);
			tag.put("FluidTank", fluidTag);
		}
	}

	@Override
	public void load(@NotNull CompoundTag tag) {
		super.load(tag);
		if (tag.contains("FluidTank")) {
			initFluidTank();
			fluidTank.readFromNBT(tag.getCompound("FluidTank"));
		}
	}

	@Override
	public void onLoad() {
		super.onLoad();
		initFluidTank();
		if (level != null && !level.isClientSide) {
			sync();
		}
	}

	// ----- 核心逻辑 -----
	@Override
	public void serverTick(Level level, BlockPos pos, BlockState state, EXPGeneratorBlockEntity entity) {
		if (level.isClientSide()) return;
		initFluidTank();

		boolean isWorking = false;

		// ===== 1. 玩家经验汲取 =====
		AABB aboveBox = new AABB(
				pos.getX(),
				pos.getY() + 1,
				pos.getZ(),
				pos.getX() + 1,
				pos.getY() + 2,
				pos.getZ() + 1
		);
		int expToTake = EXPGeneratorConfig.PLAYER_EXP_PER_TICK.get();

		boolean canAcceptFluid = entity.fluidTank.getFluidAmount() < entity.fluidTank.getCapacity();

		level.getEntitiesOfClass(
						Player.class,
						aboveBox,
						(player) -> {
							return !player.isSpectator();
						})
				.stream()
				.findFirst()
				.ifPresent((player) -> {
					if (canAcceptFluid && player.totalExperience >= expToTake) {
						player.giveExperiencePoints(-expToTake);
						FluidStack playerFluid = new FluidStack(DTFluids.LIQUID_EXPERIENCE.get(), expToTake);
						int filled = entity.fluidTank.fill(playerFluid, IFluidHandler.FluidAction.EXECUTE);
						if (filled > 0) {
							entity.setChanged();
							entity.sync();
						}
					}
				});

		// ===== 2. 消耗流体 → FE =====
		boolean generatedPower = false;
		if (entity.energy < entity.getMachineMaxEnergy()) {
			int mbPerTick = EXPGeneratorConfig.MB_PER_TICK.get();
			FluidStack drainStack = entity.fluidTank.drain(mbPerTick, IFluidHandler.FluidAction.SIMULATE);
			if (!drainStack.isEmpty() && drainStack.getAmount() >= mbPerTick) {
				entity.fluidTank.drain(mbPerTick, IFluidHandler.FluidAction.EXECUTE);
				int fePerMb = EXPGeneratorConfig.FE_PER_MB.get();
				int generated = fePerMb * mbPerTick;
				entity.energy = Math.min(entity.energy + generated, entity.getMachineMaxEnergy());
				entity.setChanged();
				entity.sync();
				generatedPower = true;
			}
		}

		isWorking = isWorking || generatedPower;

		// 3. 更新方块状态
		if (state.getValue(EXPGeneratorBlock.LIT) != isWorking) {
			level.setBlock(pos, state.setValue(EXPGeneratorBlock.LIT, isWorking), 3);
		}

		if (!isWorking && entity.progress > 0) {
			entity.progress = 0;
			entity.setChanged();
		}
	}

	// ----- GUI -----
	@Override
	public ModularUI createUI(Player player) {
		ModularUI ui = new ModularUI(176, 166, this, player);
		ui.widget(createUIWidget(player));
		return ui;
	}

	private WidgetGroup createUIWidget(Player player) {
		initFluidTank();
		WidgetGroup group = new WidgetGroup(0, 0, 176, 166);
		group.setBackground(new ResourceTexture(DeepTech.loadGui("exp_generator")));

		LabelWidget title = new LabelWidget(8, 8, MachineBlocks.EXP_GENERATOR.get().getName());
		title.setColor(0xFF5D5F60);
		group.addWidget(title);

		group.addWidget(new EnergyBarWidget(
				18, 25,
				this::getEnergyStored,
				getMachineMaxEnergy()
		));

		group.addWidget(new FluidBarWidget(
				36, 25, 14, 42,
				() -> fluidTank.getFluidAmount(),
				EXPGeneratorConfig.FLUID_CAPACITY.get(),
				new ResourceTexture(DeepTech.loadGui("elements/energy_back")),
				new ResourceTexture(DeepTech.loadGui("elements/energy_front"))
		));

		SimpleMachineInventory container = new SimpleMachineInventory(getInventory());
		SlotWidget inputSlot = new SlotWidget();
		inputSlot.setContainerSlot(container, 0);
		inputSlot.setSelfPosition(new Position(41, 38));
		inputSlot.setBackground((ResourceTexture) null);
		inputSlot.setCanTakeItems(true);
		inputSlot.setCanPutItems(true);
		group.addWidget(inputSlot);

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

	public int getFluidAmount() {
		return fluidTank != null ? fluidTank.getFluidAmount() : 0;
	}

	public int getFluidCapacity() {
		return fluidTank != null ? fluidTank.getCapacity() : 0;
	}
}