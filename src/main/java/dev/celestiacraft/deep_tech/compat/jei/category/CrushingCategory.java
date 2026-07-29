package dev.celestiacraft.deep_tech.compat.jei.category;

import dev.celestiacraft.deep_tech.DeepTech;
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
import net.minecraft.resources.ResourceLocation;

public class CrushingCategory {
	public static SimpleJeiCategory<CrushingRecipe> builder(IGuiHelper helper) {
		IDrawable slotDrawable = helper.getSlotDrawable();

		// ✅ 直接创建 ResourceTexture（不需要在 DTTextures 注册）
		ResourceLocation progressTexture = new ResourceLocation(
				DeepTech.MODID, "textures/gui/elements/jei/progressbar/crusher.png"
		);
		ResourceLocation iconTexture = new ResourceLocation(
				DeepTech.MODID, "textures/gui/elements/jei/crusher.png"
		);
		IDrawable progressDrawable = helper.createDrawable(progressTexture, 0, 0, 16, 16);
		IDrawable iconDrawable = helper.createDrawable(iconTexture, 0, 0, 32, 32);

		return SimpleJeiCategory.builder(DTJeiRecipeType.CRUSHING, helper)
				.setTitle(DTBlocks.MACHINE_CRUSHER.get().getName())
				// 这个setSize()是必须的
				.setSize(160, 80)
				.setIcon(DTBlocks.MACHINE_CRUSHER.asStack())
				.setRecipe((builder, recipe, group) -> {
					builder.addSlot(RecipeIngredientRole.INPUT, 10, 20)
							.addIngredients(recipe.getInput());

					builder.addSlot(RecipeIngredientRole.OUTPUT, 42, 20)
							.addItemStack(recipe.getOutput());
				})
				.setDraw((recipe, view, graphics, mouseX, mouseY) -> {
					slotDrawable.draw(graphics, 9, 19);
					slotDrawable.draw(graphics, 109, 19);
					// ✅ 用 IDrawable 绘制进度条
					progressDrawable.draw(graphics, 52, 26);

					// ✅ 用 IDrawable 绘制机器图标
					iconDrawable.draw(graphics, 120, 20);

					Font font = Minecraft.getInstance().font;

					Component energyText = Component.literal("⚡ " + recipe.getEnergyCost() + " FE / tick");
					graphics.drawString(font, energyText, 10, 60, 0xFF0095e0, true);

					Component timeText = Component.literal("⏱ " + recipe.getProcessingTime() + " tick");
					graphics.drawString(font, timeText, 10, 72, 0xFFe08500, true);
				})
				.build();
	}
}