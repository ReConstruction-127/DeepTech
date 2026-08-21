package dev.celestiacraft.deep_tech.common.block.machine.basic.exp_generator;

import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Position;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.api.block.machine.MachineBlockEntity;
import dev.celestiacraft.deep_tech.api.fluid.SingleTankFluidTransfer;
import dev.celestiacraft.deep_tech.api.gui.MachineItemSlots;
import dev.celestiacraft.deep_tech.api.gui.widget.EnergyBarWidget;
import dev.celestiacraft.deep_tech.api.gui.widget.ProportionalTankWidget;
import dev.celestiacraft.deep_tech.common.register.DTFluids;
import dev.celestiacraft.deep_tech.common.register.block.MachineBlocks;
import dev.celestiacraft.deep_tech.config.common.machine.EXPGeneratorConfig;
import dev.celestiacraft.deep_tech.tags.DeepTechFluidTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
	public EXPGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
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
		return 0;
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
		// 只接受经验类流体,其他流体一律拒绝填充
		return isFluidInputTank(tank) && stack.getFluid().is(DeepTechFluidTags.EXPERIENCE);
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
		if (level.isClientSide()) {
			return;
		}

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
						FluidStack playerFluid = new FluidStack(DTFluids.LIQUID_EXPERIENCE.get(), expToTake);
						entity.getFluidHandler().fillTank(experienceTank, playerFluid, IFluidHandler.FluidAction.EXECUTE, false);
					}
				});

		boolean generatedPower = false;
		if (entity.getEnergy() < entity.getMachineMaxEnergy() / 2) {
			int mbPerTick = EXPGeneratorConfig.MB_PER_TICK.get();
			FluidStack drainStack = entity.getFluidHandler().drainTank(experienceTank, mbPerTick, IFluidHandler.FluidAction.SIMULATE, false);

			if (!drainStack.isEmpty()
					&& drainStack.getAmount() >= mbPerTick
					&& drainStack.getFluid().is(DeepTechFluidTags.EXPERIENCE)) {
				entity.getFluidHandler().drainTank(experienceTank, mbPerTick, IFluidHandler.FluidAction.EXECUTE, false);
				int fePerMb = EXPGeneratorConfig.FE_PER_MB.get();
				int generated = fePerMb * mbPerTick;
				entity.setEnergy(Math.min(entity.getEnergy() + generated, entity.getMachineMaxEnergy()));
				entity.setChanged();
				entity.sync();
				generatedPower = true;
			}
		}

		isWorking = isWorking || generatedPower;

		// 3. 更新方块状态
		if (state.getValue(EXPGeneratorBlock.LIT) != isWorking) {
			level.setBlockAndUpdate(pos, state.setValue(EXPGeneratorBlock.LIT, isWorking));
		}

		if (!isWorking && entity.getProgress() > 0) {
			entity.setProgress(0);
			entity.setChanged();
		}
		if (level.getGameTime() % 2 == 0 && getEnergy() > 0) {
			for (Direction dir : Direction.values()) {
				BlockEntity target = level.getBlockEntity(pos.relative(dir));
				if (target == null) {
					continue;
				}
				target.getCapability(ForgeCapabilities.ENERGY, dir.getOpposite())
						.ifPresent(storage -> {
							int sent = storage.receiveEnergy(Math.min(getEnergy(), getMaxExtract()), false);
							setEnergy(getEnergy() - sent);
							if (sent > 0) {
								setChanged();
								sync();
							}
						});
			}
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
		group.setBackground(new ResourceTexture(DeepTech.loadGui("exp_generator")));

		LabelWidget title = new LabelWidget(8, 8, MachineBlocks.EXP_GENERATOR.get().getName());
		title.setColor(0xFF5D5F60);
		group.addWidget(title);

		group.addWidget(new EnergyBarWidget(
				99,
				25,
				this::getEnergyStored,
				getMachineMaxEnergy()
		));

		// 经验槽:比例填充式流体槽,支持拿着桶点击槽位灌入/抽取
		group.addWidget(new ProportionalTankWidget(
				new SingleTankFluidTransfer(getFluidHandler().getTankHandler(getExperienceTank())),
				0,
				42,
				26,
				16,
				40,
				true,
				true
		).setBackground(new ResourceTexture(DeepTech.loadGui("elements/tank_back"))));

		// 根据配置动态生成物品槽位: 输入/输出槽数量为 0 时不会创建任何 widget
		MachineItemSlots.add(
				group,
				this,
				getItemHandler(),
				new Position(41, 38),
				new Position(97, 38)
		);

		addPlayerInventory(group, player);
		return group;
	}

	public int getFluidAmount() {
		return getFluidHandler().getFluidInTank(getExperienceTank()).getAmount();
	}

	public int getFluidCapacity() {
		return getFluidHandler().getTankCapacity(getExperienceTank());
	}
}
