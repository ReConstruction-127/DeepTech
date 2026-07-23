package dev.celestiacraft.deep_tech.compat.jei;

import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.common.recipe.crushing.CrushingRecipe;
import dev.celestiacraft.deep_tech.common.register.DTBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class CrushingRecipeCategory implements IRecipeCategory<CrushingRecipe> {

    public static final RecipeType<CrushingRecipe> RECIPE_TYPE =
            RecipeType.create(DeepTech.MODID, "crushing", CrushingRecipe.class);

    private final IDrawable icon;
    private final IDrawable slotDrawable;
    private final IDrawable progressFront;  // ✅ 静态进度条图标
    private CrushingRecipe currentRecipe;

    public CrushingRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(DTBlocks.MACHINE_CRUSHER.get()));
        this.slotDrawable = guiHelper.getSlotDrawable();

        // ✅ 加载进度条前景纹理（16x16）
        ResourceLocation frontTex = new ResourceLocation(DeepTech.MODID, "textures/gui/elements/progress_front.png");
        this.progressFront = guiHelper.createDrawable(frontTex, 0, 0, 16, 16);
    }

    @Override
    public RecipeType<CrushingRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.deep_tech.machine_crusher");
    }

    @Override
    public IDrawable getBackground() {
        return new IDrawable() {
            @Override
            public int getWidth() {
                return 160;
            }

            @Override
            public int getHeight() {
                return 80;
            }

            @Override
            public void draw(GuiGraphics graphics, int mouseX, int mouseY) {
                // ========== 1. 输入槽（位置：10, 20） ==========
                slotDrawable.draw(graphics, 10, 20);

                // ========== 2. 输出槽（位置：110, 20） ==========
                slotDrawable.draw(graphics, 110, 20);

                // ========== 3. 静态进度条（输入输出槽正中间） ==========
                // 输入槽 x=10, 输出槽 x=110, 中心是 60
                // 纹理宽度16，所以 x = 60 - 8 = 52
                // 槽位 y=20, 高度18, 中心是 29, 纹理高度16，所以 y = 29 - 8 = 21
                progressFront.draw(graphics, 52, 21);

                // ========== 4. 能量和时间文字（带阴影） ==========
                if (currentRecipe != null) {
                    var font = Minecraft.getInstance().font;

                    // 能量：红色，带阴影
                    Component energyText = Component.literal("⚡ " + currentRecipe.getEnergyCost() + " FE");
                    graphics.drawString(font, energyText, 10, 60, 0xFF0095e0, true);

                    // 时间：青色，带阴影
                    Component timeText = Component.literal("⏱ " + currentRecipe.getProcessingTime() + " tick");
                    graphics.drawString(font, timeText, 10, 72, 0xFFe08500, true);
                }
            }
        };
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CrushingRecipe recipe, IFocusGroup focuses) {
        this.currentRecipe = recipe;

        builder.addSlot(RecipeIngredientRole.INPUT, 10, 20)
                .addIngredients(recipe.getInput());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 110, 20)
                .addItemStack(recipe.getOutput());
    }
}