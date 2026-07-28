package dev.celestiacraft.deep_tech.compat.jei;

import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.common.recipe.crushing.CrushingRecipe;
import dev.celestiacraft.deep_tech.common.register.DTBlocks;
import dev.celestiacraft.deep_tech.common.register.DTRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@JeiPlugin
public class DeepTechJEIPlugin implements IModPlugin {



    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return new ResourceLocation(DeepTech.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new CrushingRecipeCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        var level = Minecraft.getInstance().level;
        if (level == null) return;

        // ✅ 直接获取所有 CrushingRecipe，不需要遍历 Map
        // registerRecipes 中
        List<CrushingRecipe> recipes = level.getRecipeManager()
                .getAllRecipesFor(DTRecipes.CRUSHING.getRecipeType());

        registration.addRecipes(CrushingRecipeCategory.RECIPE_TYPE, recipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(
                new ItemStack(DTBlocks.MACHINE_CRUSHER.get()),
                CrushingRecipeCategory.RECIPE_TYPE
        );
    }
}