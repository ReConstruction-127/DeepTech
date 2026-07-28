package dev.celestiacraft.deep_tech.common.block.machine.crusher;

import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.SlotWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Position;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.api.block.MachineBlockEntity;
import dev.celestiacraft.deep_tech.common.gui.EnergyBarWidget;
import dev.celestiacraft.deep_tech.common.gui.ProgressBarWidget;
import dev.celestiacraft.deep_tech.common.inventory.SimpleMachineInventory;
import dev.celestiacraft.deep_tech.common.recipe.crushing.CrushingRecipe;
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

public class CrusherBlockEntity extends MachineBlockEntity<CrusherBlockEntity> implements IUIHolder.BlockEntityUI {
	private static final int ELEMENTS_TEXTURE_SIZE = 256;
	private static final ResourceLocation ELEMENTS_TEXTURE = DeepTech.loadResource("textures/gui/elements/elements.png");

	public CrusherBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void serverTick(Level level, BlockPos pos, BlockState state, CrusherBlockEntity entity) {
		if (level.isClientSide()) {
			return;
		}

		SimpleMachineInventory inventoryWrapper = new SimpleMachineInventory(entity.inventory);
		RecipeType<CrushingRecipe> recipeType = DTRecipes.CRUSHING.getRecipeType();
		CrushingRecipe recipe = level.getRecipeManager()
				.getRecipeFor(recipeType, inventoryWrapper, level)
				.orElse(null);

		if (recipe == null) {
			if (state.getValue(CrusherBlock.LIT)) {
				level.setBlock(pos, state.setValue(CrusherBlock.LIT, false), 3);
			}
			if (entity.progress > 0) {
				entity.progress = 0;
				entity.setChanged();
			}
			entity.maxProgress = 100;
			return;
		}

		// ✅ 直接设置 maxProgress（同步到客户端）
		entity.maxProgress = recipe.getProcessingTime();

		ItemStack output = recipe.getOutput();
		int energyCost = recipe.getEnergyCost();

		ItemStack currentOutput = entity.inventory.getStackInSlot(1);
		boolean canOutput = currentOutput.isEmpty()
				|| (ItemStack.isSameItemSameTags(currentOutput, output)
				&& currentOutput.getCount() + output.getCount() <= currentOutput.getMaxStackSize());

		boolean hasEnergy = entity.energy >= energyCost;

		boolean isWorking = canOutput && hasEnergy;
		if (state.getValue(CrusherBlock.LIT) != isWorking) {
			level.setBlock(pos, state.setValue(CrusherBlock.LIT, isWorking), 3);
		}

		if (isWorking) {
			entity.energy -= energyCost;
			entity.progress++;
			entity.setChanged();

			if (level.getGameTime() % 5 == 0) {
				entity.sync();
			}

			// ✅ 使用 maxProgress 判断
			if (entity.progress >= entity.maxProgress) {
				entity.inventory.getStackInSlot(0).shrink(1);
				if (currentOutput.isEmpty()) {
					entity.inventory.setStackInSlot(1, output.copy());
				} else {
					currentOutput.grow(output.getCount());
				}
				entity.progress = 0;
				entity.setChanged();
				entity.sync();
			}
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
		group.setBackground(new ResourceTexture("deep_tech:textures/gui/crusher.png"));

		LabelWidget title = new LabelWidget(
				8,
				8,
				Component.translatable("block.deep_tech.machine_crusher")
		);

		title.setColor(0xFF5D5F60);

		group.addWidget(title);

		group.addWidget(new EnergyBarWidget(
				18,
				25,
				this::getEnergyStored,
				getMaxEnergyStored()
		));

		// 在 createUIWidget 中，添加进度条（在输入和输出槽之间）
		group.addWidget(new ProgressBarWidget(
				68,                 // x 坐标
				39,                 // y 坐标
				16,                 // 宽度
				16,                 // 高度
				this::getProgress,  // 当前进度b
				this::getMaxProgress,
				new ResourceTexture("deep_tech:textures/gui/elements/progress_back.png"),
				new ResourceTexture("deep_tech:textures/gui/elements/progress_front.png")
		));

		SimpleMachineInventory container = new SimpleMachineInventory(inventory);

		SlotWidget input = new SlotWidget();

		input.setContainerSlot(container, 0);

		input.setSelfPosition(new Position(41, 38));

//		input.setBackground(SlotWidget.ITEM_SLOT_TEXTURE);

		input.setCanTakeItems(true);
		input.setCanPutItems(true);

		group.addWidget(input);


		SlotWidget output = new SlotWidget();

		output.setContainerSlot(container, 1);

		output.setSelfPosition(new Position(97, 38));

//		output.setBackground(SlotWidget.ITEM_SLOT_TEXTURE);

		output.setCanTakeItems(true);
		output.setCanPutItems(false);

		group.addWidget(output);
		addPlayerInventory(group, player);

		return group;
	}

	private static ResourceTexture elementsTexture(int u, int v, int width, int height) {
		return new ResourceTexture(
				ELEMENTS_TEXTURE,
				(float) u / ELEMENTS_TEXTURE_SIZE,
				(float) v / ELEMENTS_TEXTURE_SIZE,
				(float) width / ELEMENTS_TEXTURE_SIZE,
				(float) height / ELEMENTS_TEXTURE_SIZE
		);
	}

	private void addPlayerInventory(WidgetGroup group, Player player) {
		Container inventory = player.getInventory();

		// 主背包 3×9

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


		// 快捷栏 9格

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