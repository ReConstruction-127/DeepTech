package dev.celestiacraft.deep_tech.compat.jei;

import com.lowdragmc.lowdraglib.gui.modular.ModularUIGuiContainer;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.common.recipe.alloy.AlloyRecipe;
import dev.celestiacraft.deep_tech.common.recipe.crushing.CrushingRecipe;
import dev.celestiacraft.deep_tech.common.register.DTRecipes;
import dev.celestiacraft.deep_tech.common.register.block.MachineBlocks;
import dev.celestiacraft.deep_tech.compat.jei.api.DTJeiRecipeType;
import dev.celestiacraft.deep_tech.compat.jei.category.AlloyCategory;
import dev.celestiacraft.deep_tech.compat.jei.category.CrushingCategory;
import dev.celestiacraft.deep_tech.compat.jei.handler.MachineGuiHandler;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@JeiPlugin
public class DeepTechJEIPlugin implements IModPlugin {
	@Override
	public @NotNull ResourceLocation getPluginUid() {
		return DeepTech.loadResource("jei_plugin");
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		IGuiHelper helper = registration.getJeiHelpers().getGuiHelper();
		registration.addRecipeCategories(
				CrushingCategory.builder(helper),
				AlloyCategory.builder(helper)
		);
	}

	@Override
	public void registerRecipes(@NotNull IRecipeRegistration registration) {
		Level level = Minecraft.getInstance().level;
		if (level == null) {
			return;
		}

		RecipeManager manager = level.getRecipeManager();

		List<CrushingRecipe> crushing = manager.getAllRecipesFor(DTRecipes.CRUSHING.getRecipeType());
		List<AlloyRecipe> alloy = manager.getAllRecipesFor(DTRecipes.ALLOY.getRecipeType());

		registration.addRecipes(DTJeiRecipeType.CRUSHING, crushing);
		registration.addRecipes(DTJeiRecipeType.ALLOY, alloy);
	}

	@Override
	public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration) {
		addCatalystsForVanillaRecipe(registration);

		registration.addRecipeCatalyst(
				MachineBlocks.CRUSHER.get(),
				DTJeiRecipeType.CRUSHING
		);

		registration.addRecipeCatalyst(
				MachineBlocks.ALLOY_FURNACE.get(),
				DTJeiRecipeType.ALLOY
		);
	}

	private void addCatalystsForVanillaRecipe(@NotNull IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(
				MachineBlocks.SCULK_FURNACE.asItem(),
				RecipeTypes.SMELTING,
				RecipeTypes.BLASTING,
				RecipeTypes.SMOKING
		);
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		registration.addGuiContainerHandler(ModularUIGuiContainer.class, MachineGuiHandler.INSTANCE);
	}
}
