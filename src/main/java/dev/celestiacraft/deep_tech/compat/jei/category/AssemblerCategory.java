package dev.celestiacraft.deep_tech.compat.jei.category;

import dev.celestiacraft.deep_tech.api.client.texture.DTTextures;
import dev.celestiacraft.deep_tech.common.recipe.assembling.AssemblingRecipe;
import dev.celestiacraft.deep_tech.common.recipe.cultivation.CultivationFluidInput;
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

public class AssemblerCategory {
	private static final int[] INPUT_X = {8, 26, 44, 62};
	private static final int[] INPUT_Y = {0, 18, 36, 54};
	private static final int CATALYST_X = 104;
	private static final int CATALYST_Y = 0;
	private static final int FLUID_INPUT_X = 82;
	private static final int[] FLUID_INPUT_Y = {36, 54};
	private static final int[] OUTPUT_X = {128, 146};
	private static final int[] OUTPUT_Y = {0, 18};
	private static final int FLUID_OUTPUT_X = 128;
	private static final int FLUID_OUTPUT_Y = 54;

	public static SimpleJeiCategory<AssemblingRecipe> builder(IGuiHelper helper) {
		IDrawable drawable = helper.getSlotDrawable();

		return SimpleJeiCategory.builder(DTJeiRecipeType.ASSEMBLING, helper)
				.setTitle(MachineBlocks.ASSEMBLER.get().getName())
				// 这个setSize()是必须的
				.setSize(178, 106)
				.setIcon(MachineBlocks.ASSEMBLER.asStack())
				.setRecipe((builder, recipe, group) -> {
					List<IngredientWithCount> inputs = recipe.getItemInputs();
					for (int i = 0; i < inputs.size() && i < INPUT_X.length * INPUT_Y.length; i++) {
						builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X[i % INPUT_X.length], INPUT_Y[i / INPUT_X.length])
								.addItemStacks(inputs.get(i).toItemStacks());
					}

					if (recipe.getCatalyst() != null && !recipe.getCatalyst().isEmpty()) {
						builder.addSlot(RecipeIngredientRole.CATALYST, CATALYST_X, CATALYST_Y)
								.addIngredients(recipe.getCatalyst());
					}

					List<CultivationFluidInput> fluidInputs = recipe.getFluidInputs();
					int fluidInputCount = Math.min(fluidInputs.size(), FLUID_INPUT_Y.length);
					for (int i = 0; i < fluidInputCount; i++) {
						CultivationFluidInput input = fluidInputs.get(i);
						builder.addSlot(RecipeIngredientRole.INPUT, FLUID_INPUT_X, FLUID_INPUT_Y[i])
								.addFluidStack(input.fluid(), input.amount());
					}

					List<net.minecraft.world.item.ItemStack> itemOutputs = recipe.getItemOutputs();
					for (int i = 0; i < itemOutputs.size() && i < OUTPUT_X.length * OUTPUT_Y.length; i++) {
						builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X[i % OUTPUT_X.length], OUTPUT_Y[i / OUTPUT_X.length])
								.addItemStack(itemOutputs.get(i));
					}

					if (!recipe.getFluidOutputs().isEmpty()) {
						builder.addSlot(RecipeIngredientRole.OUTPUT, FLUID_OUTPUT_X, FLUID_OUTPUT_Y)
								.addFluidStack(recipe.getFluidOutputs().get(0).getFluid(), recipe.getFluidOutputs().get(0).getAmount());
					}
				})
				.setDraw((recipe, view, graphics, mouseX, mouseY) -> {
					// 输入槽
					for (int y : INPUT_Y) {
						for (int x : INPUT_X) {
							drawable.draw(graphics, x - 1, y - 1);
						}
					}
					// 催化剂槽
					drawable.draw(graphics, CATALYST_X - 1, CATALYST_Y - 1);
					// 流体输入槽
					for (int y : FLUID_INPUT_Y) {
						drawable.draw(graphics, FLUID_INPUT_X - 1, y - 1);
					}
					// 输出槽
					for (int y : OUTPUT_Y) {
						for (int x : OUTPUT_X) {
							drawable.draw(graphics, x - 1, y - 1);
						}
					}
					// 流体输出槽
					if (!recipe.getFluidOutputs().isEmpty()) {
						drawable.draw(graphics, FLUID_OUTPUT_X - 1, FLUID_OUTPUT_Y - 1);
					}
					DTTextures.PROGRESS_ASM.render(graphics, 104, 24);
					DTTextures.ICON_ASM.render(graphics, 7, 74);

					Font font = Minecraft.getInstance().font;

					if (!recipe.getCatalyst().isEmpty()) {
						Component catalystText = Component.literal("(不消耗)");
						graphics.drawString(font, catalystText, CATALYST_X - 4, CATALYST_Y, 0xFF00FFFF, true);
					}

					Component energyText = Component.literal("⚡ " + recipe.getEnergyCost() + " FE / tick");
					graphics.drawString(font, energyText, 88, 86, 0xFF0095e0, true);

					Component timeText = Component.literal("⏱ " + recipe.getProcessingTime() + " tick");
					graphics.drawString(font, timeText, 88, 96, 0xFFe08500, true);
				})
				.build();
	}
}