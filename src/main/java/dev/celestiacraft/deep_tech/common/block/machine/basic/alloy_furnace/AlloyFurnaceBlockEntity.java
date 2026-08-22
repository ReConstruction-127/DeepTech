package dev.celestiacraft.deep_tech.common.block.machine.basic.alloy_furnace;

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
import dev.celestiacraft.deep_tech.api.gui.widget.VerticalProgressBarWidget;
import dev.celestiacraft.deep_tech.common.recipe.alloy.AlloyRecipe;
import dev.celestiacraft.deep_tech.common.register.DTRecipes;
import dev.celestiacraft.deep_tech.common.register.block.MachineBlocks;
import dev.celestiacraft.deep_tech.config.common.machine.basic.AlloyFurnaceConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class AlloyFurnaceBlockEntity extends MachineBlockEntity<AlloyFurnaceBlockEntity> implements IUIHolder.BlockEntityUI {
	public AlloyFurnaceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public int getMachineMaxEnergy() {
		return AlloyFurnaceConfig.MAX_ENERGY.get();
	}

	@Override
	public int getMaxReceive() {
		return AlloyFurnaceConfig.MAX_RECEIVE.get();
	}

	@Override
	public int getItemInputSlotCount() {
		return 2;
	}

	@Override
	public int getItemOutputSlotCount() {
		return 1;
	}

	@Override
	public void serverTick(Level level, BlockPos pos, BlockState state, AlloyFurnaceBlockEntity entity) {
		if (level.isClientSide()) {
			return;
		}

		AlloyRecipe recipe = entity.findRecipe(entity, state);
		if (recipe == null) {
			return;
		}

		ItemStack output = recipe.getOutput();
		int energyCost = recipe.getEnergyCost();

		int outputSlot = entity.getItemOutputSlotIndex(0);
		ItemStack currentOutput = entity.getItemHandler().getStackInSlot(outputSlot);
		boolean canOutput = currentOutput.isEmpty()
				|| (ItemStack.isSameItemSameTags(currentOutput, output)
				&& currentOutput.getCount() + output.getCount() <= currentOutput.getMaxStackSize());

		boolean hasEnergy = entity.getEnergy() >= energyCost;
		boolean isWorking = canOutput && hasEnergy;

		// 更新方块点燃状态
		if (state.getValue(AlloyFurnaceBlock.LIT) != isWorking) {
			level.setBlock(pos, state.setValue(AlloyFurnaceBlock.LIT, isWorking), 3);
		}

		if (isWorking) {
			entity.setEnergy(getEnergy() - energyCost);
			entity.setProgress(getProgress() + 1);

			entity.setSyncCounter(entity.getSyncCounter() + 1);
			if (entity.getSyncCounter() % 5 == 0) {
				entity.sync();
			}

			// 进度完成
			if (entity.getProgress() >= entity.getMaxProgress()) {
				// 重新匹配一次, 从实际匹配到的槽位消耗对应数量
				int[] slots = recipe.matchSlots(entity.getInventory());
				if (slots != null) {
					for (int i = 0; i < recipe.getInputs().size(); i++) {
						entity.getItemHandler().getStackInSlot(slots[i]).shrink(recipe.getInputs().get(i).getCount());
					}
					if (currentOutput.isEmpty()) {
						entity.getItemHandler().setStackInSlot(outputSlot, output.copy());
					} else {
						currentOutput.grow(output.getCount());
					}
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

	public AlloyRecipe findRecipe(AlloyFurnaceBlockEntity entity, BlockState state) {
		AlloyRecipe recipe = level.getRecipeManager()
				.getRecipeFor(DTRecipes.ALLOY.getRecipeType(), entity.getInventory(), level)
				.orElse(null);

		// 无配方熄灭并重置进度
		if (recipe == null) {
			if (state.getValue(AlloyFurnaceBlock.LIT)) {
				level.setBlockAndUpdate(worldPosition, state.setValue(AlloyFurnaceBlock.LIT, false));
			}
			if (entity.getProgress() > 0) {
				entity.setProgress(0);
				entity.setChanged();
				entity.sync();
				entity.setSyncCounter(0);
			}
			entity.setMaxProgress(100);
			return null;
		}

		entity.setMaxProgress(recipe.getProcessingTime());
		return recipe;
	}

	@Override
	public ModularUI createUI(Player player) {
		ModularUI ui = new ModularUI(176, 166, this, player);
		ui.widget(createUIWidget(player));
		return ui;
	}

	private WidgetGroup createUIWidget(Player player) {
		WidgetGroup group = new WidgetGroup(0, 0, 176, 166);
		group.setBackground(new ResourceTexture(DeepTech.loadGui("alloy_furnace")));

		LabelWidget title = new LabelWidget(8, 8, MachineBlocks.ALLOY_FURNACE.get().getName());
		title.setColor(0xFF5D5F60);
		group.addWidget(title);

		group.addWidget(new EnergyBarWidget(
				18,
				25,
				this::getEnergyStored,
				getMaxEnergyStored()
		));

		group.addWidget(new VerticalProgressBarWidget(
				78,
				40,
				14,
				14,
				this::getProgress,
				this::getMaxProgress,
				new ResourceTexture(DeepTech.loadGui("elements/progress_alloy_back")),
				new ResourceTexture(DeepTech.loadGui("elements/progress_alloy_front"))
		));

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