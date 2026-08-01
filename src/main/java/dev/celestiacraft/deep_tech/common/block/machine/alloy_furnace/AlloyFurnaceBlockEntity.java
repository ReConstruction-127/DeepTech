package dev.celestiacraft.deep_tech.common.block.machine.alloy_furnace;

import dev.celestiacraft.deep_tech.api.block.machine.MachineBlockEntity;
import dev.celestiacraft.deep_tech.common.inventory.SimpleMachineInventory;
import dev.celestiacraft.deep_tech.common.recipe.alloy.AlloyRecipe;
import dev.celestiacraft.deep_tech.common.register.DTRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class AlloyFurnaceBlockEntity extends MachineBlockEntity<AlloyFurnaceBlockEntity> {
	private final SimpleMachineInventory inventoryWrapper;

	public AlloyFurnaceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		inventoryWrapper = new SimpleMachineInventory(getItemHandler());
	}

	@Override
	public int getMachineMaxEnergy() {
		return 50000;
	}

	@Override
	public int getMaxReceive() {
		return 1000;
	}

	@Override
	public int getItemInputSlotCount() {
		return 3;
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
				entity.getItemHandler().getStackInSlot(0).shrink(1);
				if (currentOutput.isEmpty()) {
					entity.getItemHandler().setStackInSlot(outputSlot, output.copy());
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

	public AlloyRecipe findRecipe(AlloyFurnaceBlockEntity entity, BlockState state) {
		AlloyRecipe recipe = level.getRecipeManager()
				.getRecipeFor(DTRecipes.ALLOY.getRecipeType(), entity.inventoryWrapper, level)
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
}