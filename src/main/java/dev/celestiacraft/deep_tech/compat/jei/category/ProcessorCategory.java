package dev.celestiacraft.deep_tech.compat.jei.category;

import dev.celestiacraft.deep_tech.api.client.texture.DTTextures;
import dev.celestiacraft.deep_tech.common.recipe.processor.ProcessorRecipe;
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

import java.util.List;

public class ProcessorCategory {
	private static final int[] INPUT_X = {10, 28};
	private static final int INPUT_Y = 20;
	private static final int[] OUTPUT_X = {69, 87};
	private static final int OUTPUT_Y = 20;

	public static SimpleJeiCategory<ProcessorRecipe> builder(IGuiHelper helper) {
		IDrawable drawable = helper.getSlotDrawable();

		return SimpleJeiCategory.builder(DTJeiRecipeType.PROCESSING, helper)
				.setTitle(MachineBlocks.PROCESSOR.get().getName())
				// 这个setSize()是必须的
				.setSize(140, 64)
				.setIcon(MachineBlocks.PROCESSOR.asStack())
				.setRecipe((builder, recipe, group) -> {
					List<IngredientWithCount> inputs = recipe.getItemInputs();
					int inputCount = Math.min(inputs.size(), INPUT_X.length);
					for (int i = 0; i < inputCount; i++) {
						builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X[i], INPUT_Y)
								.addItemStacks(inputs.get(i).toItemStacks());
					}

					List<net.minecraft.world.item.ItemStack> outputs = recipe.getItemOutputs();
					int outputCount = Math.min(outputs.size(), OUTPUT_X.length);
					for (int i = 0; i < outputCount; i++) {
						builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X[i], OUTPUT_Y)
								.addItemStack(outputs.get(i));
					}
				})
				.setDraw((recipe, view, graphics, mouseX, mouseY) -> {
					// 输入槽
					for (int x : INPUT_X) {
						drawable.draw(graphics, x - 1, INPUT_Y - 1);
					}
					// 输出槽
					for (int x : OUTPUT_X) {
						drawable.draw(graphics, x - 1, OUTPUT_Y - 1);
					}
					DTTextures.PROGRESS_PROCESSOR.render(graphics, 49, 24);
					DTTextures.ICON_PROCESSOR.render(graphics, 108, 12);

					Font font = Minecraft.getInstance().font;

					Component energyText = Component.literal("⚡ " + recipe.getEnergyCost() + " FE / tick");
					graphics.drawString(font, energyText, 10, 44, 0xFF0095e0, true);

					Component timeText = Component.literal("⏱ " + recipe.getProcessingTime() + " tick");
					graphics.drawString(font, timeText, 10, 56, 0xFFe08500, true);
				})
				.build();
	}
}