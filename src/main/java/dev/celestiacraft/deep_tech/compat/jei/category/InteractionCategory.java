package dev.celestiacraft.deep_tech.compat.jei.category;

import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.api.client.texture.DTTextures;
import dev.celestiacraft.deep_tech.common.recipe.interaction.ChanceResult;
import dev.celestiacraft.deep_tech.common.recipe.interaction.InteractionRecipe;
import dev.celestiacraft.deep_tech.common.recipe.interaction.WeightedResult;
import dev.celestiacraft.deep_tech.compat.jei.api.DTJeiRecipeType;
import dev.celestiacraft.libs.compat.jei.api.SimpleJeiCategory;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import java.text.NumberFormat;
import java.util.List;

public class InteractionCategory {
	private static final ThreadLocal<DecimalFormat> PERCENT_FORMAT = ThreadLocal.withInitial(() -> {
		return new DecimalFormat("0.0%");
	});
	// ✅ 新（只在需要时显示小数）
	private static final NumberFormat PERCENT_FORMAT = NumberFormat.getPercentInstance();

	static {
		PERCENT_FORMAT.setMaximumFractionDigits(0);
		PERCENT_FORMAT.setMinimumFractionDigits(0);
	}

	// 槽位布局
	private static final int INPUT_X = 8;
	private static final int INPUT_Y = 20;

	// ✅ 输出槽改为横向一行
	private static final int OUTPUT_START_X = 42;
	private static final int OUTPUT_START_Y = 3;   // 与输入槽同一行
	private static final int OUTPUT_SLOT_SIZE = 18;
	// 不再需要 OUTPUT_COLS，直接根据 results.size() 动态计算

	// 方块输入/输出（右侧）
	private static final int BLOCK_INPUT_X = 42;
	private static final int BLOCK_INPUT_Y = 36;
	private static final int BLOCK_OUTPUT_X = 65;
	private static final int BLOCK_OUTPUT_Y = 36;

	public static SimpleJeiCategory<InteractionRecipe> builder(IGuiHelper helper) {
		IDrawable slotDrawable = helper.getSlotDrawable();

		return SimpleJeiCategory.builder(DTJeiRecipeType.INTERACTION, helper)
				.setTitle(Component.literal("Interaction Crafting"))
				.setSize(110, 72)
				.setIcon(new ItemStack(Blocks.REINFORCED_DEEPSLATE)) // 用强化深板岩作为图标
				.setRecipe((builder, recipe, group) -> {


					// 输入槽（触发物品）
					builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X, INPUT_Y)
							.addIngredients(recipe.getTriggerItem());

					// 输出槽（所有可能的产物）
					List<ChanceResult> results = recipe.getResults();
					for (int i = 0; i < results.size(); i++) {
						int x = OUTPUT_START_X + i * OUTPUT_SLOT_SIZE;
						int y = OUTPUT_START_Y;
						builder.addSlot(RecipeIngredientRole.OUTPUT, x, y)
								.addItemStack(results.get(i).stack)
								.setSlotName(String.valueOf(i));
					}

					// 方块输入（目标方块）
					builder.addSlot(RecipeIngredientRole.INPUT, BLOCK_INPUT_X, BLOCK_INPUT_Y)
							.addItemStack(new ItemStack(recipe.getTargetBlockState().getBlock()));

					// 方块输出（转化后的方块，如果有额外效果且转化方块不为空）
					if (recipe.getExtraEffect() != null &&
							recipe.getExtraEffect().getState() != null &&
							!recipe.getExtraEffect().getState().isAir()) {
						builder.addSlot(RecipeIngredientRole.OUTPUT, BLOCK_OUTPUT_X, BLOCK_OUTPUT_Y)
								.addItemStack(new ItemStack(recipe.getExtraEffect().getState().getBlock()));
					}

					// 额外掉落物（如果配置了额外掉落）
					if (recipe.getExtraEffect() != null &&
							recipe.getExtraEffect().getExtraDrops() != null &&
							!recipe.getExtraEffect().getExtraDrops().isEmpty()) {
						// 如果有多个额外掉落，显示在方块输出旁边，这里简单起见只显示第一个
						// 或者可以加更多槽，但受限于空间，我们只显示一个代表
						int extraX = BLOCK_OUTPUT_X + OUTPUT_SLOT_SIZE + 3;
						if (extraX < 150) {
							builder.addSlot(RecipeIngredientRole.OUTPUT, extraX, BLOCK_OUTPUT_Y + 20)
									.addItemStack(recipe.getExtraEffect().getExtraDrops().get(0));
						}
					}
				})
				.setDraw((recipe, view, graphics, mouseX, mouseY) -> {

					// 绘制槽位背景
					slotDrawable.draw(graphics, INPUT_X - 1, INPUT_Y - 1); // 输入槽

					DTTextures.INTERACTION_BACKGROUND.render(graphics, 7, 2);

					// 绘制所有输出槽背景
					List<ChanceResult> results = recipe.getResults();
					for (int i = 0; i < results.size(); i++) {
						int x = OUTPUT_START_X + i * OUTPUT_SLOT_SIZE - 1;
						int y = OUTPUT_START_Y - 1;
						slotDrawable.draw(graphics, x, y);
					}

					// 方块输入/输出槽背景
//					slotDrawable.draw(graphics, BLOCK_INPUT_X - 1, BLOCK_INPUT_Y - 1);
//					if (recipe.getExtraEffect() != null &&
//							recipe.getExtraEffect().getState() != null &&
//							!recipe.getExtraEffect().getState().isAir()) {
//						slotDrawable.draw(graphics, BLOCK_OUTPUT_X - 1, BLOCK_OUTPUT_Y - 1);
//					}

					Font font = Minecraft.getInstance().font;

					// 绘制权重/概率文字（在输出槽下方）
					for (int i = 0; i < results.size() && i < 9; i++) {
						ChanceResult result = results.get(i);
						int x = OUTPUT_START_X + i * OUTPUT_SLOT_SIZE;
						int y = OUTPUT_START_Y + OUTPUT_SLOT_SIZE - 2;
						String probText = PERCENT_FORMAT.format(result.chance);
						graphics.drawString(font, probText, x, y, 0xFF808080, true);
					}

					// 绘制交互类型提示（左键/右键）
					switch (recipe.getInteractionType()) {
						case LEFT_CLICK -> DTTextures.LEFT_CLICK.render(graphics, INPUT_X - 1, INPUT_Y - 20);
						case RIGHT_CLICK -> DTTextures.RIGHT_CLICK.render(graphics, INPUT_X - 1, INPUT_Y - 20);
						case ANY -> {
							// ANY 时不绘制任何图标
						}
					}

					// 是否消耗提示
					if (!recipe.isConsumeTrigger()) {
						DTTextures.RECYCLE.render(graphics, INPUT_X - 1, INPUT_Y + 18);
					}

					// 额外效果提示
					if (recipe.getExtraEffect() != null) {
						double chance = recipe.getExtraEffect().getChance();
						if (chance > 0) {
							String extraText =PERCENT_FORMAT.format(chance);
							graphics.drawString(font, extraText, BLOCK_OUTPUT_X + 16, BLOCK_INPUT_Y + 4, 0xFFAA00, true);
						}
					}


				})
				.build();
	}
}