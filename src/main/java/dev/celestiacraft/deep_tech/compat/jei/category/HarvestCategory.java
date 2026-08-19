package dev.celestiacraft.deep_tech.compat.jei.category;

import dev.celestiacraft.deep_tech.api.client.texture.DTTextures;
import dev.celestiacraft.deep_tech.common.recipe.harvest.HarvestInput;
import dev.celestiacraft.deep_tech.common.recipe.harvest.HarvestRecipe;
import dev.celestiacraft.deep_tech.common.register.block.MachineBlocks;
import dev.celestiacraft.deep_tech.compat.jei.api.DTJeiRecipeType;
import dev.celestiacraft.libs.compat.jei.api.SimpleJeiCategory;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

public class HarvestCategory {
	public static SimpleJeiCategory<HarvestRecipe> builder(IGuiHelper helper) {
		IDrawable drawable = helper.getSlotDrawable();
		Font font = Minecraft.getInstance().font;

		return SimpleJeiCategory.builder(DTJeiRecipeType.HARVEST, helper)
				.setTitle(MachineBlocks.SCULK_COLLECTOR.get().getName())
				// 这个setSize()是必须的
				.setSize(128, 40)
				.setIcon(MachineBlocks.SCULK_COLLECTOR.asStack())
				.setRecipe((builder, recipe, group) -> {
					Level level = Minecraft.getInstance().level;

					builder.addSlot(RecipeIngredientRole.INPUT, 12, 12)
							.addIngredients(recipe.getInput().toJeiIngredient(level));

					builder.addSlot(RecipeIngredientRole.OUTPUT, 62, 12)
							.addItemStacks(HarvestInput.toJeiOutputs(recipe.getResults()));
				})
				.setDraw((recipe, view, graphics, mouseX, mouseY) -> {
					// 输入槽
					drawable.draw(graphics, 11, 11);
					// 输出槽
					drawable.draw(graphics, 61, 11);

					DTTextures.PROGRESS_COLLECTOR.render(graphics, 39, 15);
					DTTextures.ICON_COLLECTOR.render(graphics, 92, 4);

				})
				.build();
	}
}