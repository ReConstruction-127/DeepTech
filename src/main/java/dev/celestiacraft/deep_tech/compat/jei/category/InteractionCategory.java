package dev.celestiacraft.deep_tech.compat.jei.category;

import dev.celestiacraft.deep_tech.api.client.texture.DTTextures;
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

import java.text.DecimalFormat;
import java.util.List;

public class InteractionCategory {
	private static final ThreadLocal<DecimalFormat> PERCENT_FORMAT = ThreadLocal.withInitial(() -> {
		return new DecimalFormat("0.0%");
	});

	// 槽位布局
	private static final int INPUT_X = 8;
	private static final int INPUT_Y = 20;

	// 9 个输出槽（3x3 网格）
	private static final int OUTPUT_START_X = 50;
	private static final int OUTPUT_START_Y = 6;
	private static final int OUTPUT_SLOT_SIZE = 18;
	private static final int OUTPUT_COLS = 3;

	// 方块输入/输出（右侧）
	private static final int BLOCK_INPUT_X = 116;
	private static final int BLOCK_INPUT_Y = 6;
	private static final int BLOCK_OUTPUT_X = 116;
	private static final int BLOCK_OUTPUT_Y = 42;

	public static SimpleJeiCategory<InteractionRecipe> builder(IGuiHelper helper) {
		IDrawable slotDrawable = helper.getSlotDrawable();

		return SimpleJeiCategory.builder(DTJeiRecipeType.INTERACTION, helper)
				.setTitle(Component.literal("Interaction Crafting"))
				.setSize(150, 72)
				.setIcon(new ItemStack(Blocks.REINFORCED_DEEPSLATE)) // 用强化深板岩作为图标
				.setRecipe((builder, recipe, group) -> {
					// 输入槽（触发物品）
					builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X, INPUT_Y)
							.addIngredients(recipe.getTriggerItem());

					// 输出槽（所有可能的产物）
					List<WeightedResult> results = recipe.getResults();
					int totalWeight = results.stream()
							.mapToInt((result) -> {
								return result.weight;
							}).sum();

					for (int i = 0; i < results.size() && i < 9; i++) {
						int row = i / OUTPUT_COLS;
						int col = i % OUTPUT_COLS;
						int x = OUTPUT_START_X + col * OUTPUT_SLOT_SIZE;
						int y = OUTPUT_START_Y + row * OUTPUT_SLOT_SIZE;

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
						int extraX = BLOCK_OUTPUT_X + OUTPUT_SLOT_SIZE + 4;
						if (extraX < 150) {
							builder.addSlot(RecipeIngredientRole.OUTPUT, extraX, BLOCK_OUTPUT_Y)
									.addItemStack(recipe.getExtraEffect().getExtraDrops().get(0));
						}
					}
				})
				.setDraw((recipe, view, graphics, mouseX, mouseY) -> {
					// 绘制槽位背景
					slotDrawable.draw(graphics, INPUT_X - 1, INPUT_Y - 1); // 输入槽

					// 绘制所有输出槽背景
					List<WeightedResult> results = recipe.getResults();
					int totalWeight = results.stream()
							.mapToInt((result) -> {
								return result.weight;
							}).sum();

					for (int i = 0; i < results.size() && i < 9; i++) {
						int row = i / OUTPUT_COLS;
						int col = i % OUTPUT_COLS;
						int x = OUTPUT_START_X + col * OUTPUT_SLOT_SIZE - 1;
						int y = OUTPUT_START_Y + row * OUTPUT_SLOT_SIZE - 1;
						slotDrawable.draw(graphics, x, y);
					}

					// 方块输入/输出槽背景
					slotDrawable.draw(graphics, BLOCK_INPUT_X - 1, BLOCK_INPUT_Y - 1);
					if (recipe.getExtraEffect() != null &&
							recipe.getExtraEffect().getState() != null &&
							!recipe.getExtraEffect().getState().isAir()) {
						slotDrawable.draw(graphics, BLOCK_OUTPUT_X - 1, BLOCK_OUTPUT_Y - 1);
					}

					Font font = Minecraft.getInstance().font;

					// 绘制权重/概率文字（在输出槽下方）
					for (int i = 0; i < results.size() && i < 9; i++) {
						WeightedResult result = results.get(i);
						int row = i / OUTPUT_COLS;
						int col = i % OUTPUT_COLS;
						int x = OUTPUT_START_X + col * OUTPUT_SLOT_SIZE;
						int y = OUTPUT_START_Y + row * OUTPUT_SLOT_SIZE + OUTPUT_SLOT_SIZE + 2;

						float probability = (float) result.weight / totalWeight;
						String probText = PERCENT_FORMAT.get().format(probability);
						graphics.drawString(font, probText, x, y, 0xFF808080, true);
					}

					// 绘制交互类型提示（左键/右键）
					String typeText;
					switch (recipe.getInteractionType()) {
						case LEFT_CLICK -> typeText = "Left Click";
						case RIGHT_CLICK -> typeText = "Right Click";
						default -> typeText = "Click";
					}
					graphics.drawString(font, typeText, INPUT_X, INPUT_Y + OUTPUT_SLOT_SIZE + 2, 0xFFFFFF, true);

					// 是否消耗提示
					if (recipe.isConsumeTrigger()) {
						graphics.drawString(font, "Consumes", INPUT_X, INPUT_Y + OUTPUT_SLOT_SIZE + 14, 0xFF5555, true);
					} else {
						graphics.drawString(font, "Does not consume", INPUT_X, INPUT_Y + OUTPUT_SLOT_SIZE + 14, 0x55FF55, true);
					}

					// 额外效果提示
					if (recipe.getExtraEffect() != null) {
						double chance = recipe.getExtraEffect().getChance();
						if (chance > 0) {
							String extraText = "Extra: " + PERCENT_FORMAT.get().format(chance);
							graphics.drawString(font, extraText, BLOCK_INPUT_X - 4, BLOCK_INPUT_Y + OUTPUT_SLOT_SIZE + 2, 0xFFAA00, true);
						}
					}

					DTTextures.ICON_CRUSHER.render(graphics, 88, 12);
				})
				.build();
	}
}