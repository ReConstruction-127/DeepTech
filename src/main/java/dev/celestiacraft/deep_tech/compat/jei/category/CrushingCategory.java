package dev.celestiacraft.deep_tech.compat.jei.category;

import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.api.client.texture.DTTextures;
import dev.celestiacraft.deep_tech.common.recipe.crushing.CrushingRecipe;
import dev.celestiacraft.deep_tech.common.register.DTBlocks;
import dev.celestiacraft.deep_tech.compat.jei.api.DTJeiRecipeType;
import dev.celestiacraft.libs.compat.jei.api.SimpleJeiCategory;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class CrushingCategory {
	public static SimpleJeiCategory<CrushingRecipe> builder(IGuiHelper helper) {
		IDrawable slotDrawable = helper.getSlotDrawable();

		return SimpleJeiCategory.builder(DTJeiRecipeType.CRUSHING, helper)
				.setTitle(DTBlocks.MACHINE_CRUSHER.get().getName())
				// 这个setSize()是必须的
				.setSize(128, 64)
				.setIcon(DTBlocks.MACHINE_CRUSHER.asStack())
				.setRecipe((builder, recipe, group) -> {
					builder.addSlot(RecipeIngredientRole.INPUT, 10, 20)
							.addIngredients(recipe.getInput());

					builder.addSlot(RecipeIngredientRole.OUTPUT, 62, 20)
							.addItemStack(recipe.getOutput());
				})
				.setDraw((recipe, view, graphics, mouseX, mouseY) -> {

					slotDrawable.draw(graphics, 9, 19);   // 输入槽
					slotDrawable.draw(graphics, 61, 19);   // 输出槽

					// 进度条（16x8）
					DTTextures.PROGRESS_FRONT.render(graphics, 36, 24);

					// 图标（32x32）m
					DTTextures.ICON_CRUSHER.render(graphics, 88, 12);

					Font font = Minecraft.getInstance().font;

					Component energyText = Component.literal("⚡ " + recipe.getEnergyCost() + " FE / tick");
					graphics.drawString(font, energyText, 10, 44, 0xFF0095e0, true);

					Component timeText = Component.literal("⏱ " + recipe.getProcessingTime() + " tick");
					graphics.drawString(font, timeText, 10, 56, 0xFFe08500, true);
				})
				.build();
	}
}