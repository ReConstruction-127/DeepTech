package dev.celestiacraft.deep_tech.common.block.machine.advanced.sculk_nursery;

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
import dev.celestiacraft.deep_tech.api.gui.widget.ProportionalTankWidget;
import dev.celestiacraft.deep_tech.api.gui.widget.VerticalProgressBarWidget;
import dev.celestiacraft.deep_tech.common.block.machine.advanced.sculk_nursery.capability.SculkNurseryCapability;
import dev.celestiacraft.deep_tech.common.inventory.SimpleMachineInventory;
import dev.celestiacraft.deep_tech.common.recipe.cultivation.CultivationRecipe;
import dev.celestiacraft.deep_tech.common.register.DTRecipes;
import dev.celestiacraft.deep_tech.common.register.block.MachineBlocks;
import dev.celestiacraft.deep_tech.config.common.machine.advanced.SculkNurseryConfig;
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
 * 幽匿培育室:
 * <ul>
 *   <li>槽位: 2 物品输入 + 4 物品输出; 流体罐: 2 输入 + 2 输出</li>
 *   <li>配方: deep_tech:cultivation, 物品无序匹配 + 流体按种类匹配, 每 tick 按配方消耗能量</li>
 *   <li>产出: 物品依次填入 4 个输出槽, 流体按顺序填入 2 个输出罐</li>
 *   <li>能力(capability)由独立类 {@link SculkNurseryCapability} 统一提供</li>
 * </ul>
 */
public class SculkNurseryBlockEntity extends MachineBlockEntity<SculkNurseryBlockEntity> implements IUIHolder.BlockEntityUI {
	private static final int TANK_SIZE = 16;
	private static final int TANK_HEIGHT = 29;
	// 流体槽位置手动配置: 按罐索引一一对应 (x, y), 不再自动逐槽推算
	private static final int[] FLUID_INPUT_TANK_X = {40, 58};
	private static final int[] FLUID_INPUT_TANK_Y = {30, 30};
	private static final int[] FLUID_OUTPUT_TANK_X = {120, 138};
	private static final int[] FLUID_OUTPUT_TANK_Y = {30, 30};

	private final SculkNurseryCapability caps = new SculkNurseryCapability(this);

	public SculkNurseryBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// ---------------- 能量/流体配置 ----------------

	@Override
	public int getMachineMaxEnergy() {
		return SculkNurseryConfig.MAX_ENERGY.get();
	}

	@Override
	public int getMaxReceive() {
		return SculkNurseryConfig.MAX_RECEIVE.get();
	}

	@Override
	public int getFluidInputTankCount() {
		return 2;
	}

	@Override
	public int getFluidOutputTankCount() {
		return 2;
	}

	@Override
	public int getMachineTankCapacity(int tank) {
		return SculkNurseryConfig.FLUID_CAPACITY.get();
	}

	// ---------------- 物品槽配置 ----------------

	@Override
	public int getItemInputSlotCount() {
		return 2;
	}

	@Override
	public int getItemOutputSlotCount() {
		return 4;
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
	public void serverTick(Level level, BlockPos pos, BlockState state, SculkNurseryBlockEntity entity) {
		if (level.isClientSide()) {
			return;
		}

		CultivationRecipe recipe = entity.findRecipe(level);
		if (recipe == null) {
			if (state.getValue(SculkNurseryBlock.LIT)) {
				level.setBlock(pos, state.setValue(SculkNurseryBlock.LIT, false), 3);
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

		if (state.getValue(SculkNurseryBlock.LIT) != isWorking) {
			level.setBlock(pos, state.setValue(SculkNurseryBlock.LIT, isWorking), 3);
		}

		if (isWorking) {
			entity.setEnergy(entity.getEnergy() - recipe.getEnergyCost());
			entity.setProgress(entity.getProgress() + 1);

			entity.setSyncCounter(entity.getSyncCounter() + 1);
			if (entity.getSyncCounter() % 5 == 0) {
				entity.sync();
			}

			if (entity.getProgress() >= entity.getMaxProgress()) {
				int[] slots = recipe.matchSlots(entity.getInventory());
				if (slots != null) {
					for (int i = 0; i < recipe.getItemInputs().size(); i++) {
						entity.getItemHandler().getStackInSlot(slots[i]).shrink(recipe.getItemInputs().get(i).getCount());
					}
				}
				entity.consumeFluidInputs(recipe);
				// 物品输出按配方概率掷骰, 流体输出必定产出
				if (level.random.nextFloat() < recipe.getItemOutputChance()) {
					entity.produceItemOutputs(recipe);
				}
				entity.produceFluidOutputs(recipe);

				entity.setProgress(0);
				entity.setSyncCounter(0);
				entity.setChanged();
				entity.sync();
			}
		} else {
			entity.setSyncCounter(0);
		}
	}

	/** 查找可用的培育配方: 物品槽匹配 + 输入罐中流体足够 */
	private CultivationRecipe findRecipe(Level level) {
		List<FluidStack> available = getInputTankFluids();
		for (CultivationRecipe recipe : level.getRecipeManager().getAllRecipesFor(DTRecipes.CULTIVATION.getRecipeType())) {
			if (recipe.matchSlots(getInventory()) != null && recipe.matchesFluids(available)) {
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

	/** 所有物品输出都能在 4 个输出槽找到空间 */
	private boolean canStoreAllItemOutputs(CultivationRecipe recipe) {
		for (ItemStack output : recipe.getItemOutputs()) {
			if (!findOutputSpace(output, true)) {
				return false;
			}
		}
		return true;
	}

	/** 所有流体输出都能填进对应输出罐 */
	private boolean canStoreAllFluidOutputs(CultivationRecipe recipe) {
		for (int i = 0; i < recipe.getFluidOutputs().size(); i++) {
			FluidStack output = recipe.getFluidOutputs().get(i);
			int tank = getFluidOutputTankIndex(i);
			if (getFluidHandler().fillTank(tank, output, IFluidHandler.FluidAction.SIMULATE, false) < output.getAmount()) {
				return false;
			}
		}
		return true;
	}

	/** 把配方物品输出依次放入输出槽 */
	private void produceItemOutputs(CultivationRecipe recipe) {
		for (ItemStack output : recipe.getItemOutputs()) {
			findOutputSpace(output, false);
		}
	}

	/** 按顺序把配方流体输出填进输出罐 */
	private void produceFluidOutputs(CultivationRecipe recipe) {
		for (int i = 0; i < recipe.getFluidOutputs().size(); i++) {
			FluidStack output = recipe.getFluidOutputs().get(i);
			getFluidHandler().fillTank(getFluidOutputTankIndex(i), output, IFluidHandler.FluidAction.EXECUTE, false);
		}
	}

	/** 从输入罐中扣除配方所需流体(按流体种类查找, 顺序无关) */
	private void consumeFluidInputs(CultivationRecipe recipe) {
		boolean[] consumed = new boolean[getFluidInputTankCount()];
		for (dev.celestiacraft.deep_tech.common.recipe.cultivation.CultivationFluidInput input : recipe.getFluidInputs()) {
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

	/** 将物品放入输出槽: 优先堆叠同物品槽, 其次空槽 */
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
		return new ModularUI(194, 187, this, player).widget(group);
	}

	private WidgetGroup createUIWidget(Player player) {
		WidgetGroup group = new WidgetGroup(0, 0, 194, 187);
		group.setBackground(new ResourceTexture(DeepTech.loadGui("sculk_nursery")));

		LabelWidget title = new LabelWidget(8, 8, MachineBlocks.SCULK_NURSERY.get().getName());
		title.setColor(0xFF97CCD6);
		group.addWidget(title);

		group.addWidget(new EnergyBarWidget(16, 29, this::getEnergyStored, getMaxEnergyStored()));

		// 2 输入罐(左) + 2 输出罐(右): 比例填充式流体槽, 支持拿着桶点击槽位灌入/抽取 (同幽匿发电机/幽匿网络缓存器)
		// LDLib TankWidget 的桶点击是对整个 IFluidTransfer 做 fill/drain(不区分罐索引),
		// 因此每个槽位必须绑定一个只暴露该罐的 SingleTankFluidTransfer, 点击才精确命中对应罐.
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

		group.addWidget(new VerticalProgressBarWidget(
				89, 33, 16, 16,
				this::getProgress,
				this::getMaxProgress,
				new ResourceTexture(DeepTech.loadGui("elements/progress_nursery_back")),
				new ResourceTexture(DeepTech.loadGui("elements/progress_nursery_front"))
		));

		SimpleMachineInventory container = new SimpleMachineInventory(getItemHandler());

		// 2 物品输入槽 (可放可取, shift 可取回背包)
		for (int i = 0; i < getItemInputSlotCount(); i++) {
			group.addWidget(createSlot(container, getItemInputSlotIndex(i), 39 + i * 18, 60, true, true));
		}

		// 4 物品输出槽 (可取不可放, shift 取回背包)
		for (int i = 0; i < getItemOutputSlotCount(); i++) {
			group.addWidget(createSlot(container, getItemOutputSlotIndex(i), 101 + i * 18, 60, false, true));
		}

		addPlayerSlots(group, player);
		return group;
	}

	private void addPlayerSlots(WidgetGroup group, Player player) {
		Container inventory = player.getInventory();
		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 9; col++) {
				SlotWidget widget = createSlot(inventory, col + row * 9 + 9, 7 + col * 18, 100 + row * 18, true, true);
				// isPlayerContainer 必须为 true, shift-click 时 LDLib 才把这里当作玩家背包目标槽
				widget.setLocationInfo(true, false);
				group.addWidget(widget);
			}
		}
		for (int col = 0; col < 9; col++) {
			SlotWidget widget = createSlot(inventory, col, 7 + col * 18, 158, true, true);
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