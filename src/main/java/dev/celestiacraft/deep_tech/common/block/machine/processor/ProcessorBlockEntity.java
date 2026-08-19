package dev.celestiacraft.deep_tech.common.block.machine.processor;

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
import dev.celestiacraft.deep_tech.api.gui.widget.ProgressBarWidget;
import dev.celestiacraft.deep_tech.api.gui.widget.VerticalProgressBarWidget;
import dev.celestiacraft.deep_tech.common.recipe.processor.ProcessorRecipe;
import dev.celestiacraft.deep_tech.common.register.DTRecipes;
import dev.celestiacraft.deep_tech.common.register.block.MachineBlocks;
import dev.celestiacraft.deep_tech.config.common.machine.ProcessorConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

public class ProcessorBlockEntity extends MachineBlockEntity<ProcessorBlockEntity> implements IUIHolder.BlockEntityUI {
	public ProcessorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public int getMachineMaxEnergy() {
		return ProcessorConfig.MAX_ENERGY.get();
	}

	@Override
	public int getMaxReceive() {
		return ProcessorConfig.MAX_RECEIVE.get();
	}

	@Override
	public int getItemInputSlotCount() {
		return 2;
	}

	@Override
	public int getItemOutputSlotCount() {
		return 2;
	}

	// ---------------- 工作逻辑 ----------------

	@Override
	public void serverTick(Level level, BlockPos pos, BlockState state, ProcessorBlockEntity entity) {
		if (level.isClientSide()) {
			return;
		}

		ProcessorRecipe recipe = entity.findRecipe(level);
		if (recipe == null) {
			if (state.getValue(ProcessorBlock.LIT)) {
				level.setBlock(pos, state.setValue(ProcessorBlock.LIT, false), 3);
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

		boolean canOutput = entity.canStoreAllOutputs(recipe);
		boolean hasEnergy = entity.getEnergy() >= recipe.getEnergyCost();
		boolean isWorking = canOutput && hasEnergy;

		if (state.getValue(ProcessorBlock.LIT) != isWorking) {
			level.setBlock(pos, state.setValue(ProcessorBlock.LIT, isWorking), 3);
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
					entity.produceOutputs(recipe);
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

	private ProcessorRecipe findRecipe(Level level) {
		return level.getRecipeManager()
				.getRecipeFor(DTRecipes.PROCESSING.getRecipeType(), getInventory(), level)
				.orElse(null);
	}

	// ---------------- 产出检查与执行 ----------------

	/** 所有物品输出都能在 2 个输出槽找到空间 */
	private boolean canStoreAllOutputs(ProcessorRecipe recipe) {
		for (ItemStack output : recipe.getItemOutputs()) {
			if (!findOutputSpace(output, true)) {
				return false;
			}
		}
		return true;
	}

	/** 把配方物品输出依次放入输出槽 */
	private void produceOutputs(ProcessorRecipe recipe) {
		for (ItemStack output : recipe.getItemOutputs()) {
			findOutputSpace(output, false);
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
		ModularUI ui = new ModularUI(176, 166, this, player);
		ui.widget(createUIWidget(player));
		return ui;
	}

	private WidgetGroup createUIWidget(Player player) {
		WidgetGroup group = new WidgetGroup(0, 0, 176, 166);
		group.setBackground(new ResourceTexture(DeepTech.loadGui("processor")));

		LabelWidget title = new LabelWidget(8, 8, MachineBlocks.PROCESSOR.get().getName());
		title.setColor(0xFF97CCD6);
		group.addWidget(title);

		group.addWidget(new EnergyBarWidget(
				18,
				25,
				this::getEnergyStored,
				getMaxEnergyStored()
		));

		group.addWidget(new ProgressBarWidget(
				80,
				40,
				14,
				14,
				this::getProgress,
				this::getMaxProgress,
				new ResourceTexture(DeepTech.loadGui("elements/progress_processor_back")),
				new ResourceTexture(DeepTech.loadGui("elements/progress_processor_front"))
		));

		// 2 输入槽 + 2 输出槽 (shift-click 支持同上)
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
}