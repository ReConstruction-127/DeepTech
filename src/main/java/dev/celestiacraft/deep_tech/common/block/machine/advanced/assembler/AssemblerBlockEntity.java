package dev.celestiacraft.deep_tech.common.block.machine.advanced.assembler;

import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.SlotWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Position;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.api.block.machine.MachineBlockEntity;
import dev.celestiacraft.deep_tech.api.fluid.SingleTankFluidTransfer;
import dev.celestiacraft.deep_tech.api.gui.widget.EnergyBarWidget;
import dev.celestiacraft.deep_tech.api.gui.widget.ProgressBarWidget;
import dev.celestiacraft.deep_tech.api.gui.widget.ProportionalTankWidget;
import dev.celestiacraft.deep_tech.common.block.machine.advanced.assembler.capability.AssemblerCapability;
import dev.celestiacraft.deep_tech.common.inventory.SimpleMachineInventory;
import dev.celestiacraft.deep_tech.common.recipe.assembling.AssemblingRecipe;
import dev.celestiacraft.deep_tech.common.recipe.cultivation.CultivationFluidInput;
import dev.celestiacraft.deep_tech.common.register.DTRecipes;
import dev.celestiacraft.deep_tech.common.register.block.MachineBlocks;
import dev.celestiacraft.deep_tech.config.common.machine.advanced.AssemblerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 组装机:
 * <ul>
 *   <li>槽位: 16 物品输入 + 1 催化剂槽(不消耗) + 4 物品输出; 流体罐: 2 输入 + 1 输出</li>
 *   <li>配方: deep_tech:assembling, 物品无序匹配 + 流体按种类匹配, 催化剂槽物品只要求存在</li>
 *   <li>能力(capability)由独立类 {@link AssemblerCapability} 统一提供</li>
 * </ul>
 */
public class AssemblerBlockEntity extends MachineBlockEntity<AssemblerBlockEntity> implements IUIHolder.BlockEntityUI {
	private static final int TANK_SIZE = 16;
	private static final int TANK_HEIGHT = 70;
	// 流体槽位置手动配置: 按罐索引一一对应 (x, y), 不再自动逐槽推算
	private static final int[] FLUID_INPUT_TANK_X = {25, 43};
	private static final int[] FLUID_INPUT_TANK_Y = {26, 26};
	private static final int[] FLUID_OUTPUT_TANK_X = {187};
	private static final int[] FLUID_OUTPUT_TANK_Y = {26};
	private static final int INPUT_COLUMNS = 4;
	private static final int INPUT_ROWS = 4;

	/** 催化剂槽位于 16 个输入槽之后、输出槽之前 */
	public static final int CATALYST_SLOT = 16;

	private final AssemblerCapability caps = new AssemblerCapability(this);

	/** 只暴露 16 个输入槽的容器视图, 用于配方物品匹配 */
	private final Container inputContainer = new Container() {
		@Override
		public int getContainerSize() {
			return getItemInputSlotCount();
		}

		@Override
		public boolean isEmpty() {
			for (int i = 0; i < getItemInputSlotCount(); i++) {
				if (!getItemHandler().getStackInSlot(i).isEmpty()) {
					return false;
				}
			}
			return true;
		}

		@Override
		public ItemStack getItem(int index) {
			return getItemHandler().getStackInSlot(index);
		}

		@Override
		public ItemStack removeItem(int index, int count) {
			return ItemStack.EMPTY;
		}

		@Override
		public ItemStack removeItemNoUpdate(int index) {
			return ItemStack.EMPTY;
		}

		@Override
		public void setItem(int index, ItemStack stack) {
		}

		@Override
		public void setChanged() {
		}

		@Override
		public boolean stillValid(net.minecraft.world.entity.player.Player player) {
			return true;
		}

		@Override
		public void clearContent() {
		}
	};

	public AssemblerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// ---------------- 能量/流体配置 ----------------

	@Override
	public int getMachineMaxEnergy() {
		return AssemblerConfig.MAX_ENERGY.get();
	}

	@Override
	public int getMaxReceive() {
		return AssemblerConfig.MAX_RECEIVE.get();
	}

	@Override
	public int getFluidInputTankCount() {
		return 2;
	}

	@Override
	public int getFluidOutputTankCount() {
		return 1;
	}

	@Override
	public int getMachineTankCapacity(int tank) {
		return AssemblerConfig.FLUID_CAPACITY.get();
	}

	// ---------------- 物品槽配置 ----------------
	// 布局: slot 0-15 输入, slot 16 催化剂, slot 17-20 输出

	@Override
	public int getItemInputSlotCount() {
		return 16;
	}

	@Override
	public int getItemOutputSlotCount() {
		return 4;
	}

	@Override
	public int getMaxMachineSlot() {
		return getItemInputSlotCount() + 1 + getItemOutputSlotCount();
	}

	@Override
	public int getItemOutputSlotIndex(int index) {
		return getItemInputSlotCount() + 1 + index;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack) {
		// 输入槽 + 催化剂槽可放入, 输出槽不可放入
		return slot >= 0 && slot <= CATALYST_SLOT;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack) {
		// 催化剂槽 + 输出槽可取出, 输入槽不可取出
		return slot == CATALYST_SLOT || (slot >= getItemOutputSlotIndex(0) && slot < getMaxMachineSlot());
	}

	// ---------------- Capability(独立类) ----------------

	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, Direction side) {
		return caps.get(capability, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		caps.invalidate();
	}

	// ---------------- 工作逻辑 ----------------

	@Override
	public void serverTick(Level level, BlockPos pos, BlockState state, AssemblerBlockEntity entity) {
		if (level.isClientSide()) {
			return;
		}

		AssemblingRecipe recipe = entity.findRecipe(level);
		if (recipe == null) {
			if (state.getValue(AssemblerBlock.LIT)) {
				level.setBlock(pos, state.setValue(AssemblerBlock.LIT, false), 3);
			}
			if (entity.getProgress() > 0) {
				entity.setProgress(0);
				entity.setChanged();
				entity.sync();
				entity.setSyncCounter(0);
			}
			entity.setMaxProgress(100);
			return;
		}

		entity.setMaxProgress(recipe.getProcessingTime());

		boolean canOutputItems = entity.canStoreAllItemOutputs(recipe);
		boolean canOutputFluids = entity.canStoreAllFluidOutputs(recipe);
		boolean hasEnergy = entity.getEnergy() >= recipe.getEnergyCost();
		boolean isWorking = canOutputItems && canOutputFluids && hasEnergy;

		if (state.getValue(AssemblerBlock.LIT) != isWorking) {
			level.setBlock(pos, state.setValue(AssemblerBlock.LIT, isWorking), 3);
		}

		if (isWorking) {
			entity.setEnergy(entity.getEnergy() - recipe.getEnergyCost());
			entity.setProgress(entity.getProgress() + 1);

			entity.setSyncCounter(entity.getSyncCounter() + 1);
			if (entity.getSyncCounter() % 5 == 0) {
				entity.sync();
			}

			if (entity.getProgress() >= entity.getMaxProgress()) {
				int[] slots = recipe.matchSlots(entity.inputContainer);
				if (slots != null) {
					for (int i = 0; i < recipe.getItemInputs().size(); i++) {
						entity.getItemHandler().getStackInSlot(slots[i]).shrink(recipe.getItemInputs().get(i).getCount());
					}
					// 催化剂不消耗
					entity.consumeFluidInputs(recipe);
					entity.produceItemOutputs(recipe);
					entity.produceFluidOutputs(recipe);
				}
				entity.setProgress(0);
				entity.setSyncCounter(0);
				entity.setChanged();
				entity.sync();
			}
		} else {
			entity.setSyncCounter(0);
		}
	}

	private AssemblingRecipe findRecipe(Level level) {
		List<FluidStack> available = getInputTankFluids();
		for (AssemblingRecipe recipe : level.getRecipeManager().getAllRecipesFor(DTRecipes.ASSEMBLING.getRecipeType())) {
			if (recipe.matchSlots(inputContainer) != null
					&& recipe.matchesFluids(available)
					&& recipe.matchesCatalyst(getItemHandler().getStackInSlot(CATALYST_SLOT))) {
				return recipe;
			}
		}
		return null;
	}

	private List<FluidStack> getInputTankFluids() {
		List<FluidStack> fluids = new ArrayList<>();
		for (int i = 0; i < getFluidInputTankCount(); i++) {
			fluids.add(getFluidHandler().getFluidInTank(getFluidInputTankIndex(i)));
		}
		return fluids;
	}

	// ---------------- 产出检查与执行 ----------------

	private boolean canStoreAllItemOutputs(AssemblingRecipe recipe) {
		for (ItemStack output : recipe.getItemOutputs()) {
			if (!findOutputSpace(output, true)) {
				return false;
			}
		}
		return true;
	}

	private boolean canStoreAllFluidOutputs(AssemblingRecipe recipe) {
		for (int i = 0; i < recipe.getFluidOutputs().size(); i++) {
			FluidStack output = recipe.getFluidOutputs().get(i);
			int tank = getFluidOutputTankIndex(i);
			if (getFluidHandler().fillTank(tank, output, IFluidHandler.FluidAction.SIMULATE, false) < output.getAmount()) {
				return false;
			}
		}
		return true;
	}

	private void produceItemOutputs(AssemblingRecipe recipe) {
		for (ItemStack output : recipe.getItemOutputs()) {
			findOutputSpace(output, false);
		}
	}

	private void produceFluidOutputs(AssemblingRecipe recipe) {
		for (int i = 0; i < recipe.getFluidOutputs().size(); i++) {
			FluidStack output = recipe.getFluidOutputs().get(i);
			getFluidHandler().fillTank(getFluidOutputTankIndex(i), output, IFluidHandler.FluidAction.EXECUTE, false);
		}
	}

	private void consumeFluidInputs(AssemblingRecipe recipe) {
		boolean[] consumed = new boolean[getFluidInputTankCount()];
		for (CultivationFluidInput input : recipe.getFluidInputs()) {
			for (int i = 0; i < getFluidInputTankCount(); i++) {
				if (consumed[i]) {
					continue;
				}
				int tank = getFluidInputTankIndex(i);
				FluidStack stored = getFluidHandler().getFluidInTank(tank);
				if (input.matches(stored)) {
					getFluidHandler().drainTank(tank, input.amount(), IFluidHandler.FluidAction.EXECUTE, false);
					consumed[i] = true;
					break;
				}
			}
		}
	}

	private boolean findOutputSpace(ItemStack stack, boolean simulate) {
		if (stack.isEmpty()) {
			return true;
		}
		ItemStackHandler handler = getItemHandler();
		ItemStack remaining = stack.copy();
		for (int i = 0; i < getItemOutputSlotCount(); i++) {
			int slot = getItemOutputSlotIndex(i);
			ItemStack current = handler.getStackInSlot(slot);
			if (current.isEmpty()) {
				if (!simulate) {
					handler.setStackInSlot(slot, remaining);
				}
				return true;
			}
			if (ItemStack.isSameItemSameTags(current, remaining)
					&& current.getCount() + remaining.getCount() <= current.getMaxStackSize()) {
				if (!simulate) {
					current.grow(remaining.getCount());
				}
				return true;
			}
		}
		return false;
	}

	// ---------------- GUI ----------------

	@Override
	public ModularUI createUI(Player player) {
		WidgetGroup group = createUIWidget(player);
		return new ModularUI(214, 195, this, player).widget(group);
	}

	private WidgetGroup createUIWidget(Player player) {
		WidgetGroup group = new WidgetGroup(0, 0, 214, 195);
		group.setBackground(new ResourceTexture(DeepTech.loadGui("assembler")));

		LabelWidget title = new LabelWidget(8, 8, MachineBlocks.ASSEMBLER.get().getName());
		title.setColor(0xFF97CCD6);
		group.addWidget(title);

		group.addWidget(new EnergyBarWidget(8, 25, this::getEnergyStored, getMaxEnergyStored()));

		// 2 输入罐(左列) + 1 输出罐(右上), 支持桶点击灌入/抽取; 位置手动配置见 FLUID_*_TANK_X/Y
		for (int i = 0; i < getFluidInputTankCount() && i < FLUID_INPUT_TANK_X.length; i++) {
			int tank = getFluidInputTankIndex(i);
			group.addWidget(new ProportionalTankWidget(
					new SingleTankFluidTransfer(getFluidHandler().getTankHandler(tank)),
					0,
					FLUID_INPUT_TANK_X[i],
					FLUID_INPUT_TANK_Y[i],
					TANK_SIZE,
					TANK_HEIGHT,
					true,
					true
			).setBackground(new ResourceTexture(DeepTech.loadGui("elements/tank_back"))));
		}
		for (int i = 0; i < getFluidOutputTankCount() && i < FLUID_OUTPUT_TANK_X.length; i++) {
			int tank = getFluidOutputTankIndex(i);
			group.addWidget(new ProportionalTankWidget(
					new SingleTankFluidTransfer(getFluidHandler().getTankHandler(tank)),
					0,
					FLUID_OUTPUT_TANK_X[i],
					FLUID_OUTPUT_TANK_Y[i],
					TANK_SIZE,
					TANK_HEIGHT,
					true,
					true
			).setBackground(new ResourceTexture(DeepTech.loadGui("elements/tank_back"))));
		}

		group.addWidget(new ProgressBarWidget(
				133, 71, 16, 16,
				this::getProgress,
				this::getMaxProgress,
				new ResourceTexture(DeepTech.loadGui("elements/progress_assembler_back")),
				new ResourceTexture(DeepTech.loadGui("elements/progress_assembler_front"))
		));

		SimpleMachineInventory container = new SimpleMachineInventory(getItemHandler());

		// 16 物品输入槽 (4x4)
		for (int row = 0; row < INPUT_ROWS; row++) {
			for (int col = 0; col < INPUT_COLUMNS; col++) {
				group.addWidget(createSlot(
						container,
						getItemInputSlotIndex(col + row * INPUT_COLUMNS),
						60 + col * 18,
						25 + row * 18,
						true,
						true
				));
			}
		}

		// 催化剂槽 (不消耗, 可放可取)
		group.addWidget(createSlot(container, CATALYST_SLOT, 150, 25, true, true));

		// 4 物品输出槽 (2x2)
		int[] outputX = {150, 168};
		int[] outputY = {60, 78};
		for (int i = 0; i < getItemOutputSlotCount(); i++) {
			group.addWidget(createSlot(
					container,
					getItemOutputSlotIndex(i),
					outputX[i % 2],
					outputY[i / 2],
					false,
					true
			));
		}

		addPlayerSlots(group, player);
		return group;
	}

	private void addPlayerSlots(WidgetGroup group, Player player) {
		Container inventory = player.getInventory();
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 9; col++) {
				SlotWidget widget = createSlot(inventory, col + row * 9 + 9, 27 + col * 18, 112 + row * 18, true, true);
				widget.setLocationInfo(true, false);
				group.addWidget(widget);
			}
		}
		for (int col = 0; col < 9; col++) {
			SlotWidget widget = createSlot(inventory, col, 27 + col * 18, 170, true, true);
			widget.setLocationInfo(true, true);
			group.addWidget(widget);
		}
	}

	private static SlotWidget createSlot(Container container, int index, int x, int y, boolean canPut, boolean canTake) {
		SlotWidget widget = new SlotWidget();
		widget.initTemplate();
		widget.setContainerSlot(container, index);
		widget.setSelfPosition(new Position(x, y));
		widget.setBackground((ResourceTexture) null);
		widget.setCanPutItems(canPut);
		widget.setCanTakeItems(canTake);
		return widget;
	}
}