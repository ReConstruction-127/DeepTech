package dev.celestiacraft.deep_tech.compat.jei.category;

import dev.celestiacraft.deep_tech.api.client.lang.JeiLang;
import dev.celestiacraft.deep_tech.api.client.texture.DTTextures;
import dev.celestiacraft.deep_tech.common.recipe.interaction.ChanceResult;
import dev.celestiacraft.deep_tech.common.recipe.interaction.InteractionRecipe;
import dev.celestiacraft.deep_tech.compat.jei.api.DTJeiRecipeType;
import dev.celestiacraft.libs.compat.jei.api.SimpleJeiCategory;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import java.text.NumberFormat;
import java.util.List;

public class InteractionCategory {
	private static final NumberFormat PERCENT_FORMAT = NumberFormat.getPercentInstance();

	static {
		PERCENT_FORMAT.setMaximumFractionDigits(0);
		PERCENT_FORMAT.setMinimumFractionDigits(0);
	}

	private static final int INPUT_X = 8;
	private static final int INPUT_Y = 20;

	private static final int OUTPUT_START_X = 42;
	private static final int OUTPUT_START_Y = 3;
	private static final int OUTPUT_SLOT_SIZE = 18;

	private static final int BLOCK_INPUT_X = 42;
	private static final int BLOCK_INPUT_Y = 36;
	private static final int BLOCK_OUTPUT_X = 65;
	private static final int BLOCK_OUTPUT_Y = 36;

	public static SimpleJeiCategory<InteractionRecipe> builder(IGuiHelper helper) {
		IDrawable drawable = helper.getSlotDrawable();

		return SimpleJeiCategory.builder(DTJeiRecipeType.INTERACTION, helper)
				.setTitle(JeiLang.setTranCategoryTitle("interaction_crafting"))
				.setSize(110, 72)
				.setIcon(new ItemStack(Blocks.REINFORCED_DEEPSLATE))
				.setRecipe((builder, recipe, group) -> {
					builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X, INPUT_Y)
							.addIngredients(recipe.getTriggerItem());

					List<ChanceResult> results = recipe.getResults();
					for (int i = 0; i < results.size(); i++) {
						int x = OUTPUT_START_X + i * OUTPUT_SLOT_SIZE;
						int y = OUTPUT_START_Y;
						builder.addSlot(RecipeIngredientRole.OUTPUT, x, y)
								.addItemStack(results.get(i).stack)
								.setSlotName(String.valueOf(i));
					}

					builder.addSlot(RecipeIngredientRole.INPUT, BLOCK_INPUT_X, BLOCK_INPUT_Y)
							.addItemStack(new ItemStack(recipe.getTargetBlockState().getBlock()));

					if (recipe.getExtraEffect() != null &&
							recipe.getExtraEffect().getState() != null &&
							!recipe.getExtraEffect().getState().isAir()) {
						builder.addSlot(RecipeIngredientRole.OUTPUT, BLOCK_OUTPUT_X, BLOCK_OUTPUT_Y)
								.addItemStack(new ItemStack(recipe.getExtraEffect().getState().getBlock()));
					}

					if (recipe.getExtraEffect() != null
							&& recipe.getExtraEffect().getExtraDrops() != null
							&& !recipe.getExtraEffect().getExtraDrops().isEmpty()
					) {
						int extraX = BLOCK_OUTPUT_X + OUTPUT_SLOT_SIZE + 3;
						if (extraX < 150) {
							builder.addSlot(RecipeIngredientRole.OUTPUT, extraX, BLOCK_OUTPUT_Y + 20)
									.addItemStack(recipe.getExtraEffect().getExtraDrops().get(0));
						}
					}
				})
				.setDraw((recipe, view, graphics, mouseX, mouseY) -> {
					drawable.draw(graphics, INPUT_X - 1, INPUT_Y - 1); // 输入槽

					DTTextures.INTERACTION_BACKGROUND.render(graphics, 7, 2);

					List<ChanceResult> results = recipe.getResults();
					for (int i = 0; i < results.size(); i++) {
						int x = OUTPUT_START_X + i * OUTPUT_SLOT_SIZE - 1;
						int y = OUTPUT_START_Y - 1;
						drawable.draw(graphics, x, y);
					}

					Font font = Minecraft.getInstance().font;

					for (int i = 0; i < results.size() && i < 9; i++) {
						ChanceResult result = results.get(i);
						int x = OUTPUT_START_X + i * OUTPUT_SLOT_SIZE;
						int y = OUTPUT_START_Y + OUTPUT_SLOT_SIZE - 2;
						String probText = PERCENT_FORMAT.format(result.chance);
						graphics.drawString(font, probText, x, y, 0xFF808080, true);
					}

					switch (recipe.getInteractionType()) {
						case LEFT_CLICK -> DTTextures.LEFT_CLICK.render(graphics, INPUT_X - 1, INPUT_Y - 20);
						case RIGHT_CLICK -> DTTextures.RIGHT_CLICK.render(graphics, INPUT_X - 1, INPUT_Y - 20);
						case ANY -> {

						}
					}

					if (!recipe.isConsumeTrigger()) {
						DTTextures.RECYCLE.render(graphics, INPUT_X - 1, INPUT_Y + 18);
					}

					if (recipe.getExtraEffect() != null) {
						double chance = recipe.getExtraEffect().getChance();
						if (chance > 0) {
							String extraText = PERCENT_FORMAT.format(chance);
							graphics.drawString(font, extraText, BLOCK_OUTPUT_X + 16, BLOCK_INPUT_Y + 4, 0xFFAA00, true);
						}
					}
				})
				.build();
	}
}