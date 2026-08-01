package dev.celestiacraft.deep_tech.common.recipe.crushing;

import com.google.gson.JsonObject;
import dev.celestiacraft.deep_tech.common.recipe.utils.RecipeResultUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

public class CrushingRecipeSerializer implements RecipeSerializer<CrushingRecipe> {
	@Override
	public @NotNull CrushingRecipe fromJson(@NotNull ResourceLocation id, JsonObject json) {
		Ingredient input = Ingredient.fromJson(json.get("input"));
		JsonObject result = GsonHelper.getAsJsonObject(json, "result");
		ItemStack output = RecipeResultUtil.itemStackFromJson(result);

		int energyCost = GsonHelper.getAsInt(json, "energy_cost", 50);
		int processingTime = GsonHelper.getAsInt(json, "processing_time", 100);

		return new CrushingRecipe(id, input, output, energyCost, processingTime);
	}

	@Override
	public CrushingRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
		Ingredient input = Ingredient.fromNetwork(buf);
		ItemStack output = buf.readItem();
		int energyCost = buf.readInt();
		int processingTime = buf.readInt();

		return new CrushingRecipe(
				id,
				input,
				output,
				energyCost,
				processingTime
		);
	}

	@Override
	public void toNetwork(@NotNull FriendlyByteBuf buf, CrushingRecipe recipe) {
		recipe.getInput().toNetwork(buf);
		buf.writeItem(recipe.getOutput());
		buf.writeInt(recipe.getEnergyCost());
		buf.writeInt(recipe.getProcessingTime());
	}
}