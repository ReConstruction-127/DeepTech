package dev.celestiacraft.deep_tech.common.block.machine.crusher;

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
import dev.celestiacraft.deep_tech.common.recipe.crushing.CrushingRecipe;
import dev.celestiacraft.deep_tech.common.register.DTRecipes;
import dev.celestiacraft.deep_tech.common.register.block.MachineBlocks;
import dev.celestiacraft.deep_tech.config.common.machine.CrusherConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CrusherBlockEntity extends MachineBlockEntity<CrusherBlockEntity> implements IUIHolder.BlockEntityUI {
	public CrusherBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public int getMachineMaxEnergy() {
		return CrusherConfig.MAX_ENERGY.get();
	}

	@Override
	public int getMaxReceive() {
		return CrusherConfig.MAX_RECEIVE.get();
	}

	@Override
	public int getItemInputSlotCount() {
		return 1;
	}

	@Override
	public int getItemOutputSlotCount() {
		return 1;
	}

	@Override
	public void serverTick(Level level, BlockPos pos, BlockState state, CrusherBlockEntity entity) {
		if (level.isClientSide()) {
			return;
		}

		CrushingRecipe recipe = level.getRecipeManager()
				.getRecipeFor(DTRecipes.CRUSHING.getRecipeType(), entity.getInventory(), level)
				.orElse(null);

		// 无配方或无法处理
		if (recipe == null) {
			if (state.getValue(CrusherBlock.LIT)) {
				level.setBlock(pos, state.setValue(CrusherBlock.LIT, false), 3);
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

		ItemStack output = recipe.getOutput();
		int energyCost = recipe.getEnergyCost();

		ItemStack currentOutput = entity.getItemHandler().getStackInSlot(1);
		boolean canOutput = currentOutput.isEmpty()
				|| (ItemStack.isSameItemSameTags(currentOutput, output)
				&& currentOutput.getCount() + output.getCount() <= currentOutput.getMaxStackSize());

		boolean hasEnergy = entity.getEnergy() >= energyCost;
		boolean isWorking = canOutput && hasEnergy;

		// 更新方块光照状态
		if (state.getValue(CrusherBlock.LIT) != isWorking) {
			level.setBlock(pos, state.setValue(CrusherBlock.LIT, isWorking), 3);
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
				entity.getItemHandler().getStackInSlot(0).shrink(1);
				if (currentOutput.isEmpty()) {
					entity.getItemHandler().setStackInSlot(1, output.copy());
				} else {
					currentOutput.grow(output.getCount());
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

	@Override
	public ModularUI createUI(Player player) {
		ModularUI ui = new ModularUI(176, 166, this, player);
		ui.widget(createUIWidget(player));
		return ui;
	}

	private WidgetGroup createUIWidget(Player player) {
		WidgetGroup group = new WidgetGroup(0, 0, 176, 166);
		group.setBackground(new ResourceTexture(DeepTech.loadGui("crusher")));

		LabelWidget title = new LabelWidget(
				8,
				8,
				MachineBlocks.CRUSHER.get().getName()
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
				new ResourceTexture(DeepTech.loadGui("elements/progress_crusher_back")),
				new ResourceTexture(DeepTech.loadGui("elements/progress_crusher_front"))
		));

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
}