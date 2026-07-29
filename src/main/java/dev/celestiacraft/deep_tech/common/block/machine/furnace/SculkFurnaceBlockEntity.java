package dev.celestiacraft.deep_tech.common.block.machine.furnace;

import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.SlotWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Position;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.api.block.MachineBlockEntity;
import dev.celestiacraft.deep_tech.common.block.machine.furnace.SculkFurnaceBlock;
import dev.celestiacraft.deep_tech.common.gui.EnergyBarWidget;
import dev.celestiacraft.deep_tech.common.gui.ProgressBarWidget;
import dev.celestiacraft.deep_tech.common.inventory.SimpleMachineInventory;
import dev.celestiacraft.deep_tech.common.recipe.crushing.CrushingRecipe;
import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import dev.celestiacraft.deep_tech.common.register.DTRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SculkFurnaceBlockEntity extends MachineBlockEntity<SculkFurnaceBlockEntity> implements IUIHolder.BlockEntityUI {

	// 复用 inventoryWrapper，避免每 tick 创建
	private final SimpleMachineInventory inventoryWrapper;
	// 用于控制 sync 频率的计数器
	private int syncCounter = 0;

	public SculkFurnaceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.inventoryWrapper = new SimpleMachineInventory(this.inventory);
	}

	public SculkFurnaceBlockEntity(BlockPos pos, BlockState state) {
		this(DTBlockEntities.SCULK_FURNACE.get(), pos, state);
	}

	public static SculkFurnaceBlockEntity create(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		return new SculkFurnaceBlockEntity(type, pos, state);
	}

	@Override
	public void serverTick(Level level, BlockPos pos, BlockState state, SculkFurnaceBlockEntity entity) {
		if (level.isClientSide()) return;

		// 查询配方（复用 inventoryWrapper）
		CrushingRecipe recipe = level.getRecipeManager()
				.getRecipeFor(DTRecipes.CRUSHING.getRecipeType(), inventoryWrapper, level)
				.orElse(null);

		// 无配方或无法处理
		if (recipe == null) {
			if (state.getValue(SculkFurnaceBlock.LIT)) {
				level.setBlock(pos, state.setValue(SculkFurnaceBlock.LIT, false), 3);
			}
			if (entity.progress > 0) {
				entity.progress = 0;
				entity.setChanged(); // 状态变化，保存一次
				entity.sync();       // 通知客户端进度归零
				entity.syncCounter = 0;
			}
			entity.maxProgress = 100;
			return;
		}

		entity.maxProgress = recipe.getProcessingTime();

		ItemStack output = recipe.getOutput();
		int energyCost = recipe.getEnergyCost();

		ItemStack currentOutput = entity.inventory.getStackInSlot(1);
		boolean canOutput = currentOutput.isEmpty()
				|| (ItemStack.isSameItemSameTags(currentOutput, output)
				&& currentOutput.getCount() + output.getCount() <= currentOutput.getMaxStackSize());

		boolean hasEnergy = entity.energy >= energyCost;
		boolean isWorking = canOutput && hasEnergy;

		// 更新方块光照状态
		if (state.getValue(SculkFurnaceBlock.LIT) != isWorking) {
			level.setBlock(pos, state.setValue(SculkFurnaceBlock.LIT, isWorking), 3);
		}

		if (isWorking) {
			entity.energy -= energyCost;
			entity.progress++;

			// 每 5 tick 同步一次进度到客户端（不触发磁盘保存）
			if (++entity.syncCounter % 5 == 0) {
				entity.sync();   // 假设 sync() 只发包，不调用 setChanged()
			}

			// 进度完成
			if (entity.progress >= entity.maxProgress) {
				entity.inventory.getStackInSlot(0).shrink(1);
				if (currentOutput.isEmpty()) {
					entity.inventory.setStackInSlot(1, output.copy());
				} else {
					currentOutput.grow(output.getCount());
				}
				entity.progress = 0;
				entity.syncCounter = 0;
				// ✅ 完成时调用一次 setChanged 和 sync
				entity.setChanged();
				entity.sync();
			}
		} else {
			// 如果机器停止工作（能量不足或输出满），重置同步计数器
			entity.syncCounter = 0;
		}
	}

	@Override
	public int getMaxProgress() {
		return maxProgress;
	}

	@Override
	public ModularUI createUI(Player player) {
		ModularUI ui = new ModularUI(176, 166, this, player);
		ui.widget(createUIWidget(player));
		return ui;
	}

	private WidgetGroup createUIWidget(Player player) {
		WidgetGroup group = new WidgetGroup(0, 0, 176, 166);
		group.setBackground(new ResourceTexture("deep_tech:textures/gui/sculk_furnace.png"));

		LabelWidget title = new LabelWidget(
				8,
				8,
				Component.translatable("block.deep_tech.machine_sculk_furnace")
		);
		title.setColor(0xFF5D5F60);
		group.addWidget(title);

		group.addWidget(new EnergyBarWidget(
				18,
				25,
				this::getEnergyStored,
				getMaxEnergyStored()
		));

		group.addWidget(new ProgressBarWidget(
				68, 39, 16, 16,
				this::getProgress,
				this::getMaxProgress,
				new ResourceTexture(DeepTech.MODID + ":textures/gui/elements/progress_furnace_back.png"),
				new ResourceTexture(DeepTech.MODID + ":textures/gui/elements/progress_furnace_front.png")
		));

		SimpleMachineInventory container = new SimpleMachineInventory(inventory);

		SlotWidget input = new SlotWidget();
		input.setContainerSlot(container, 0);
		input.setSelfPosition(new Position(41, 38));
		input.setBackground((ResourceTexture) null);
		input.setCanTakeItems(true);
		input.setCanPutItems(true);
		group.addWidget(input);

		SlotWidget output = new SlotWidget();
		output.setContainerSlot(container, 1);
		output.setSelfPosition(new Position(97, 38));
		output.setBackground((ResourceTexture) null);
		output.setCanTakeItems(true);
		output.setCanPutItems(false);
		group.addWidget(output);

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
}