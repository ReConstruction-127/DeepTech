package dev.celestiacraft.deep_tech.common.block.machine.exp_generator;

import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.ProgressTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.misc.FluidStorage;
import com.lowdragmc.lowdraglib.side.fluid.IFluidStorage;
import com.lowdragmc.lowdraglib.side.fluid.IFluidTransfer;
import com.lowdragmc.lowdraglib.utils.Position;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.api.block.MachineBlockEntity;
import dev.celestiacraft.deep_tech.api.gui.EnergyBarWidget;
import dev.celestiacraft.deep_tech.api.gui.FluidTankWidget;
import dev.celestiacraft.deep_tech.common.inventory.SimpleMachineInventory;
import dev.celestiacraft.deep_tech.common.register.DTFluids;
import dev.celestiacraft.deep_tech.common.register.block.MachineBlocks;
import dev.celestiacraft.deep_tech.config.common.machine.EXPGeneratorConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.List;

public class EXPGeneratorBlockEntity extends MachineBlockEntity<EXPGeneratorBlockEntity> implements IUIHolder.BlockEntityUI {
	private final SimpleMachineInventory inventoryWrapper;
	private int syncCounter = 0;


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

	@Override
	public int getFluidInputTankCount() {
		return 1;
	}

	@Override
	public int getMachineTankCapacity(int tank) {
		return EXPGeneratorConfig.FLUID_CAPACITY.get();
	}

	@Override
	public boolean canFillFluid(int tank, FluidStack stack) {
		return super.canFillFluid(tank, stack) && (
				stack.getFluid() == DTFluids.LIQUID_EXPERIENCE.get()
						|| stack.getFluid() == DTFluids.LIQUID_EXPERIENCE.getSource()
		);
	}

	@Override
	public boolean canDrainFluid(int tank, FluidStack stack) {
		return true;
	}

	private int getExperienceTank() {
		return getFluidInputTankIndex(0);
	}

	// ----- 核心逻辑 -----
	@Override
	public void serverTick(Level level, BlockPos pos, BlockState state, EXPGeneratorBlockEntity entity) {
		if (level.isClientSide()) return;

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
		int experienceTank = entity.getExperienceTank();

		boolean canAcceptFluid = entity.getFluidAmount() < entity.getFluidCapacity();
		List<Player> playerOfClass = level.getEntitiesOfClass(
				Player.class,
				aboveBox,
				(player) -> {
					return !player.isSpectator();
				});

		playerOfClass.stream()
				.findFirst()
				.ifPresent((player) -> {
					if (canAcceptFluid && player.totalExperience >= expToTake) {
						player.giveExperiencePoints(-expToTake);
						FluidStack playerFluid = new FluidStack(DTFluids.LIQUID_EXPERIENCE.getSource(), expToTake);
						entity.getFluidStorage().fillTank(experienceTank, playerFluid, IFluidHandler.FluidAction.EXECUTE, false);
					}
				});

		boolean generatedPower = false;
		if (entity.energy < entity.getMachineMaxEnergy()) {
			int mbPerTick = EXPGeneratorConfig.MB_PER_TICK.get();
			FluidStack drainStack = entity.getFluidStorage().drainTank(experienceTank, mbPerTick, IFluidHandler.FluidAction.SIMULATE, false);

			if (!drainStack.isEmpty() && drainStack.getAmount() >= mbPerTick) {
				entity.getFluidStorage().drainTank(experienceTank, mbPerTick, IFluidHandler.FluidAction.EXECUTE, false);
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


	// GUI
	@Override
	public ModularUI createUI(Player player) {
		ModularUI ui = new ModularUI(176, 166, this, player);
		ui.widget(createUIWidget(player));
		return ui;
	}

	private WidgetGroup createUIWidget(Player player) {
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

		// ===== 流体储罐 =====
		int amount = getFluidAmount();
		int capacity = getFluidCapacity();

// 从实际存储中读取流体类型
		net.minecraftforge.fluids.FluidStack actualFluid =
				getFluidStorage().getFluidInTank(getExperienceTank());

		FluidStorage tank = new FluidStorage(capacity);

		if (amount > 0 && actualFluid != null && !actualFluid.isEmpty()) {
			com.lowdragmc.lowdraglib.side.fluid.FluidStack ldlibFluid =
					com.lowdragmc.lowdraglib.side.fluid.FluidStack.create(
							actualFluid.getFluid(),  // ✅ 实际流体类型
							amount
					);
			if (ldlibFluid != null && !ldlibFluid.isEmpty()) {
				tank.setFluid(ldlibFluid);
				System.out.println("🔍 储罐: 显示 " + actualFluid.getFluid().getFluidType().getDescription().getString() +
						" (" + amount + "/" + capacity + " mB)");
			}
		}

		TankWidget tankWidget = new TankWidget();
		tankWidget.setFluidTank(tank);
		tankWidget.setShowAmount(true);
		tankWidget.setFillDirection(ProgressTexture.FillDirection.DOWN_TO_UP);
		tankWidget.setSelfPosition(new Position(36, 25));
		tankWidget.setSize(18, 54);
		group.addWidget(tankWidget);

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
		return getFluidStorage().getFluidInTank(getExperienceTank()).getAmount();
	}

	public int getFluidCapacity() {
		return getFluidStorage().getTankCapacity(getExperienceTank());
	}
}
