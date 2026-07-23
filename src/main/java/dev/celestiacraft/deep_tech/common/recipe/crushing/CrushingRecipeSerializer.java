package dev.celestiacraft.deep_tech.common.recipe.crushing;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class CrushingRecipeSerializer implements RecipeSerializer<CrushingRecipe> {
	@Override
	public @NotNull CrushingRecipe fromJson(@NotNull ResourceLocation id, JsonObject json) {
		Ingredient input = Ingredient.fromJson(json.get("input"));
		JsonObject result = GsonHelper.getAsJsonObject(json, "result");
		String item = GsonHelper.getAsString(result, "item");
		ItemStack output = new ItemStack(
				ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(item)),
				GsonHelper.getAsInt(result, "count", 1)
		);

		int energyCost = GsonHelper.getAsInt(json, "energy_cost", 50);
		int processingTime = GsonHelper.getAsInt(json, "processing_time", 100);

		return new CrushingRecipe(id, input, output, energyCost, processingTime);
	}

	@Override
	public CrushingRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buffer) {
		Ingredient input = Ingredient.fromNetwork(buffer);
		ItemStack output = buffer.readItem();
		int energyCost = buffer.readInt();
		int processingTime = buffer.readInt();

		return new CrushingRecipe(
				id,
				input,
				output,
				energyCost,
				processingTime
		);
	}

	@Override
	public void toNetwork(@NotNull FriendlyByteBuf buffer, CrushingRecipe recipe) {
		recipe.getInput().toNetwork(buffer);
		buffer.writeItem(recipe.getOutput());
		buffer.writeInt(recipe.getEnergyCost());
		buffer.writeInt(recipe.getProcessingTime());
	}
}