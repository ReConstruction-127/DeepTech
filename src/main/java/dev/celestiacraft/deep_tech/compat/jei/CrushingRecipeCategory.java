//package dev.celestiacraft.deep_tech.compat.jei;
//
//import dev.celestiacraft.deep_tech.DeepTech;
//import dev.celestiacraft.deep_tech.common.recipe.crushing.CrushingRecipe;
//import dev.celestiacraft.deep_tech.common.register.DTBlocks;
//import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
//import mezz.jei.api.gui.drawable.IDrawable;
//import mezz.jei.api.helpers.IGuiHelper;
//import mezz.jei.api.recipe.IFocusGroup;
//import mezz.jei.api.recipe.RecipeIngredientRole;
//import mezz.jei.api.recipe.RecipeType;
//import mezz.jei.api.recipe.category.IRecipeCategory;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.GuiGraphics;
//import net.minecraft.network.chat.Component;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.item.ItemStack;
//
//public class CrushingRecipeCategory implements IRecipeCategory<CrushingRecipe> {
//
//    public static final RecipeType<CrushingRecipe> RECIPE_TYPE =
//            RecipeType.create(DeepTech.MODID, "crushing", CrushingRecipe.class);
//
//    private final IDrawable icon;
//    private final IDrawable slotDrawable;
//    private final IDrawable progressFront;
//    private CrushingRecipe currentRecipe;
//
//    public CrushingRecipeCategory(IGuiHelper guiHelper) {
//        this.icon = guiHelper.createDrawableItemStack(
//                new ItemStack(DTBlocks.MACHINE_CRUSHER.get())
//        );
//
//        this.slotDrawable = guiHelper.getSlotDrawable();
//
//        ResourceLocation frontTex = new ResourceLocation(
//                DeepTech.MODID,
//                "textures/gui/elements/progress_front.png"
//        );
//
//        this.progressFront = guiHelper.drawableBuilder(
//                        new ResourceLocation(
//                                DeepTech.MODID,
//                                "textures/gui/elements/progress_front.png"
//                        ),
//                        0,
//                        0,
//                        16,
//                        16
//                )
//                .setTextureSize(16, 16)
//                .build();
//    }
//
//    @Override
//    public RecipeType<CrushingRecipe> getRecipeType() {
//        return RECIPE_TYPE;
//    }
//
//    @Override
//    public Component getTitle() {
//        return Component.translatable("block.deep_tech.machine_crusher");
//    }
//
//    @Override
//    public IDrawable getBackground() {
//        return new IDrawable() {
//
//            @Override
//            public int getWidth() {
//                return 160;
//            }
//
//            @Override
//            public int getHeight() {
//                return 80;
//            }
//
//            @Override
//            public void draw(GuiGraphics graphics, int mouseX, int mouseY) {
//
//                // 输入槽
//                slotDrawable.draw(graphics, 9, 19);
//
//                // 输出槽
//                slotDrawable.draw(graphics, 109, 19);
//
//
//                // 进度条
//                progressFront.draw(graphics, 52, 21);
//
//
//                // 文字
//                if (currentRecipe != null) {
//
//                    var font = Minecraft.getInstance().font;
//
//                    graphics.drawString(
//                            font,
//                            "⚡ " + currentRecipe.getEnergyCost() + " FE / tick",
//                            10,
//                            60,
//                            0xFF0095e0,
//                            true
//                    );
//
//
//                    graphics.drawString(
//                            font,
//                            "⏱ " + currentRecipe.getProcessingTime() + " tick",
//                            10,
//                            72,
//                            0xFFe08500,
//                            true
//                    );
//                }
//            }
//        };
//    }
//
//
//    @Override
//    public IDrawable getIcon() {
//        return icon;
//    }
//
//    @Override
//    public void setRecipe(
//            IRecipeLayoutBuilder builder,
//            CrushingRecipe recipe,
//            IFocusGroup focuses
//    ) {
//
//        this.currentRecipe = recipe;
//
//
//        builder.addSlot(
//                        RecipeIngredientRole.INPUT,
//                        10,
//                        20
//                )
//                .addIngredients(recipe.getInput());
//
//
//        builder.addSlot(
//                        RecipeIngredientRole.OUTPUT,
//                        110,
//                        20
//                )
//                .addItemStack(recipe.getOutput());
//    }
//
//}