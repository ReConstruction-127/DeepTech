package dev.celestiacraft.deep_tech.compat.jei.category;

import dev.celestiacraft.deep_tech.api.client.texture.DTTextures;
import dev.celestiacraft.deep_tech.common.recipe.alloy.AlloyRecipe;
import dev.celestiacraft.deep_tech.api.ingredient.IngredientWithCount;
import dev.celestiacraft.deep_tech.common.register.block.MachineBlocks;
import dev.celestiacraft.deep_tech.compat.jei.api.DTJeiRecipeType;
import dev.celestiacraft.libs.compat.jei.api.SimpleJeiCategory;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

import java.util.List;

public class AlloyCategory {
	// 3 个输入槽的位置
	private static final int[] INPUT_X = {8, 26, 44};
	private static final int INPUT_Y = 20;
	private static final int OUTPUT_X = 84;
	private static final int OUTPUT_Y = 20;

	public static SimpleJeiCategory<AlloyRecipe> builder(IGuiHelper helper) {
		IDrawable drawable = helper.getSlotDrawable();

		return SimpleJeiCategory.builder(DTJeiRecipeType.ALLOY, helper)
				.setTitle(MachineBlocks.ALLOY_FURNACE.get().getName())
				.setSize(128, 72)
				.setIcon(MachineBlocks.ALLOY_FURNACE.asStack())
				.setRecipe((builder, recipe, group) -> {
					List<IngredientWithCount> inputs = recipe.getInputs();
					int slotCount = Math.min(inputs.size(), INPUT_X.length);
					for (int i = 0; i < slotCount; i++) {
						builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X[i], INPUT_Y)
								.addIngredients(inputs.get(i).getIngredient());
					}

					builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, OUTPUT_Y)
							.addItemStack(recipe.getOutput());
				})
				.setDraw((recipe, view, graphics, mouseX, mouseY) -> {
					// 输入槽
					drawable.draw(graphics, INPUT_X[0] - 1, INPUT_Y - 1);
					drawable.draw(graphics, INPUT_X[1] - 1, INPUT_Y - 1);
					drawable.draw(graphics, INPUT_X[2] - 1, INPUT_Y - 1);
					// 输出槽
					drawable.draw(graphics, OUTPUT_X - 1, OUTPUT_Y - 1);
					DTTextures.PROGRESS_FRONT.render(graphics, 66, 24);

					Font font = Minecraft.getInstance().font;

					List<IngredientWithCount> inputs = recipe.getInputs();
					int slotCount = Math.min(inputs.size(), INPUT_X.length);
					for (int i = 0; i < slotCount; i++) {
						int count = inputs.get(i).getCount();
						graphics.drawString(font, "x" + count, INPUT_X[i] + 2, 39, 0xFF404040, true);
					}

					Component energyText = Component.literal("⚡ " + recipe.getEnergyCost() + " FE / tick");
					graphics.drawString(font, energyText, 8, 52, 0xFF0095e0, true);

					Component timeText = Component.literal("⏱ " + recipe.getProcessingTime() + " tick");
					graphics.drawString(font, timeText, 8, 62, 0xFFe08500, true);
				})
				.build();
	}
}