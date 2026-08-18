package dev.celestiacraft.deep_tech.compat.jei.category;

import dev.celestiacraft.deep_tech.api.client.texture.DTTextures;
import dev.celestiacraft.deep_tech.common.recipe.cultivation.CultivationFluidInput;
import dev.celestiacraft.deep_tech.common.recipe.cultivation.CultivationRecipe;
import dev.celestiacraft.deep_tech.common.register.block.MachineBlocks;
import dev.celestiacraft.deep_tech.compat.jei.api.DTJeiRecipeType;
import dev.celestiacraft.libs.api.recipe.ingredient.item.IngredientWithCount;
import dev.celestiacraft.libs.compat.jei.api.SimpleJeiCategory;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class CultivationCategory {
	private static final int[] ITEM_INPUT_X = {8, 26};
	private static final int ITEM_INPUT_Y = 20;
	private static final int[] FLUID_INPUT_X = {8, 26};
	private static final int FLUID_INPUT_Y = 38;
	private static final int[] ITEM_OUTPUT_X = {65, 83};
	private static final int[] ITEM_OUTPUT_Y = {8, 26};
	private static final int[] FLUID_OUTPUT_X = {65, 83};
	private static final int FLUID_OUTPUT_Y = 44;

	public static SimpleJeiCategory<CultivationRecipe> builder(IGuiHelper helper) {
		IDrawable drawable = helper.getSlotDrawable();

		return SimpleJeiCategory.builder(DTJeiRecipeType.CULTIVATION, helper)
				.setTitle(MachineBlocks.SCULK_NURSERY.get().getName())
				// 这个setSize()是必须的
				.setSize(128, 88)
				.setIcon(MachineBlocks.SCULK_NURSERY.asStack())
				.setRecipe((builder, recipe, group) -> {
					List<IngredientWithCount> itemInputs = recipe.getItemInputs();
					int itemInputCount = Math.min(itemInputs.size(), ITEM_INPUT_X.length);
					for (int i = 0; i < itemInputCount; i++) {
						builder.addSlot(RecipeIngredientRole.INPUT, ITEM_INPUT_X[i], ITEM_INPUT_Y)
								.addItemStacks(itemInputs.get(i).toItemStacks());
					}

					List<CultivationFluidInput> fluidInputs = recipe.getFluidInputs();
					int fluidInputCount = Math.min(fluidInputs.size(), FLUID_INPUT_X.length);
					for (int i = 0; i < fluidInputCount; i++) {
						CultivationFluidInput input = fluidInputs.get(i);
						builder.addSlot(RecipeIngredientRole.INPUT, FLUID_INPUT_X[i], FLUID_INPUT_Y)
								.addFluidStack(input.fluid(), input.amount());
					}

					List<net.minecraft.world.item.ItemStack> itemOutputs = recipe.getItemOutputs();
					for (int i = 0; i < itemOutputs.size() && i < ITEM_OUTPUT_X.length * ITEM_OUTPUT_Y.length; i++) {
						builder.addSlot(RecipeIngredientRole.OUTPUT, ITEM_OUTPUT_X[i % ITEM_OUTPUT_X.length], ITEM_OUTPUT_Y[i / ITEM_OUTPUT_X.length])
								.addItemStack(itemOutputs.get(i));
					}

					List<FluidStack> fluidOutputs = recipe.getFluidOutputs();
					int fluidOutputCount = Math.min(fluidOutputs.size(), FLUID_OUTPUT_X.length);
					for (int i = 0; i < fluidOutputCount; i++) {
						builder.addSlot(RecipeIngredientRole.OUTPUT, FLUID_OUTPUT_X[i], FLUID_OUTPUT_Y)
								.addFluidStack(fluidOutputs.get(i).getFluid(), fluidOutputs.get(i).getAmount());
					}
				})
				.setDraw((recipe, view, graphics, mouseX, mouseY) -> {
					// 输入槽
					for (int x : ITEM_INPUT_X) {
						drawable.draw(graphics, x - 1, ITEM_INPUT_Y - 1);
					}
					for (int x : FLUID_INPUT_X) {
						drawable.draw(graphics, x - 1, FLUID_INPUT_Y - 1);
					}
					// 输出槽
					for (int y : ITEM_OUTPUT_Y) {
						for (int x : ITEM_OUTPUT_X) {
							drawable.draw(graphics, x - 1, y - 1);
						}
					}
					for (int x : FLUID_OUTPUT_X) {
						drawable.draw(graphics, x - 1, FLUID_OUTPUT_Y - 1);
					}
					DTTextures.PROGRESS_COLLECTOR.render(graphics, 49, 20);

					Font font = Minecraft.getInstance().font;

					Component energyText = Component.literal("⚡ " + recipe.getEnergyCost() + " FE / tick");
					graphics.drawString(font, energyText, 8, 64, 0xFF0095e0, true);

					Component timeText = Component.literal("⏱ " + recipe.getProcessingTime() + " tick");
					graphics.drawString(font, timeText, 8, 73, 0xFFe08500, true);

					if (recipe.getItemOutputChance() < 1.0F) {
						int percent = Math.round(recipe.getItemOutputChance() * 100);
						Component chanceText = Component.literal("⚗ 物品产出概率 " + percent + "%");
						graphics.drawString(font, chanceText, 8, 82, 0xFF38b764, true);
					}
				})
				.build();
	}
}