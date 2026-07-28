package dev.celestiacraft.deep_tech.compat.jei.category;

import dev.celestiacraft.deep_tech.common.recipe.crushing.CrushingRecipe;
import dev.celestiacraft.deep_tech.common.register.DTBlocks;
import dev.celestiacraft.deep_tech.compat.jei.api.DTJeiRecipeType;
import dev.celestiacraft.deep_tech.api.client.texture.DTTextures;
import dev.celestiacraft.libs.compat.jei.api.SimpleJeiCategory;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

public class CrushingCategory {
	public static SimpleJeiCategory<CrushingRecipe> builder(IGuiHelper helper) {
		IDrawable slotDrawable = helper.getSlotDrawable();

		return SimpleJeiCategory.builder(DTJeiRecipeType.CRUSHING, helper)
				.setTitle(DTBlocks.MACHINE_CRUSHER.get().getName())
				// 这个setSize()是必须的
				.setSize(160, 80)
				.setIcon(DTBlocks.MACHINE_CRUSHER.asStack())
				.setRecipe((builder, recipe, group) -> {
					builder.addSlot(RecipeIngredientRole.INPUT, 10, 20)
							.addIngredients(recipe.getInput());

					builder.addSlot(RecipeIngredientRole.OUTPUT, 110, 20)
							.addItemStack(recipe.getOutput());
				})
				.setDraw((recipe, view, graphics, mouseX, mouseY) -> {
					slotDrawable.draw(graphics, 9, 19);
					slotDrawable.draw(graphics, 109, 19);
					DTTextures.PROGRESS_FRONT.render(graphics, 52, 21);

					Font font = Minecraft.getInstance().font;

					Component energyText = Component.literal("⚡ " + recipe.getEnergyCost() + " / FE");
					graphics.drawString(font, energyText, 10, 60, 0xFF0095e0, true);

					Component timeText = Component.literal("⏱ " + recipe.getProcessingTime() + " tick");
					graphics.drawString(font, timeText, 10, 72, 0xFFe08500, true);
				})
				.build();
	}
}