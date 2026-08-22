package dev.celestiacraft.deep_tech.common.block.machine.basic.furnace;

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
import dev.celestiacraft.deep_tech.common.register.block.MachineBlocks;
import dev.celestiacraft.deep_tech.config.common.machine.basic.SculkFurnaceConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SculkFurnaceBlockEntity extends MachineBlockEntity<SculkFurnaceBlockEntity> implements IUIHolder.BlockEntityUI {
	public SculkFurnaceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public int getMachineMaxEnergy() {
		return SculkFurnaceConfig.MAX_ENERGY.get();
	}

	@Override
	public int getMaxReceive() {
		return SculkFurnaceConfig.MAX_RECEIVE.get();
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
	public void serverTick(Level level, BlockPos pos, BlockState state, SculkFurnaceBlockEntity entity) {
		if (level.isClientSide()) {
			return;
		}

		// 使用原版熔炉配方
		RecipeType<SmeltingRecipe> recipeType = RecipeType.SMELTING;

		SmeltingRecipe recipe = level.getRecipeManager()
				.getRecipeFor(recipeType, entity.getInventory(), level)
				.orElse(null);

		// 无配方或无法处理
		if (recipe == null) {
			if (state.getValue(SculkFurnaceBlock.LIT)) {
				level.setBlock(pos, state.setValue(SculkFurnaceBlock.LIT, false), 3);
			}
			if (entity.getProgress() > 0) {
				entity.setProgress(0);
				entity.setChanged(); // 状态变化, 保存一次
				entity.sync();       // 通知客户端进度归零
				entity.setSyncCounter(0);
			}
			entity.setMaxProgress(100);
			return;
		}

		// 固定参数: 处理时间 100 tick(5 秒), 能量消耗 20 FE/tick
		int processingTime = 100;
		int energyCost = 20;
		entity.setMaxProgress(processingTime);

		ItemStack output = recipe.getResultItem(level.registryAccess());

		ItemStack currentOutput = entity.getItemHandler().getStackInSlot(1);
		boolean canOutput = currentOutput.isEmpty()
				|| (ItemStack.isSameItemSameTags(currentOutput, output)
				&& currentOutput.getCount() + output.getCount() <= currentOutput.getMaxStackSize());

		boolean hasEnergy = entity.getEnergy() >= energyCost;
		boolean isWorking = canOutput && hasEnergy;

		// 更新方块光照状态
		if (state.getValue(SculkFurnaceBlock.LIT) != isWorking) {
			level.setBlock(pos, state.setValue(SculkFurnaceBlock.LIT, isWorking), 3);
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
		group.setBackground(new ResourceTexture(DeepTech.loadGui("sculk_furnace")));

		LabelWidget title = new LabelWidget(
				8,
				8,
				MachineBlocks.SCULK_FURNACE.get().getName()
		);
		title.setColor(0xFF5D5F60);
		group.addWidget(title);

		group.addWidget(new EnergyBarWidget(
				18,
				25,
				this::getEnergyStored,
				getMaxEnergyStored()
		));

		group.addWidget(new VerticalProgressBarWidget(
				68, 40, 14, 14,
				this::getProgress,
				this::getMaxProgress,
				new ResourceTexture(DeepTech.loadGui("elements/progress_furnace_back")),
				new ResourceTexture(DeepTech.loadGui("elements/progress_furnace_front"))
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