package dev.celestiacraft.deep_tech.common.recipe.alloy;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import dev.celestiacraft.deep_tech.api.ingredient.IngredientWithCount;
import dev.celestiacraft.deep_tech.common.recipe.utils.RecipeResultUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AlloySerializer implements RecipeSerializer<AlloyRecipe> {
	@Override
	public @NotNull AlloyRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
		JsonArray inputsArray = GsonHelper.getAsJsonArray(json, "inputs");
		if (inputsArray.isEmpty()) {
			throw new JsonSyntaxException("Alloy recipe " + id + " must have at least one input");
		}
		List<IngredientWithCount> inputs = new ArrayList<>();
		for (JsonElement element : inputsArray) {
			int count = 1;
			if (element.isJsonObject()) {
				count = GsonHelper.getAsInt(element.getAsJsonObject(), "count", 1);
			}
			inputs.add(new IngredientWithCount(Ingredient.fromJson(element), count));
		}

		JsonObject result = GsonHelper.getAsJsonObject(json, "result");
		ItemStack output = RecipeResultUtil.itemStackFromJson(result);

		int energyCost = GsonHelper.getAsInt(json, "energy_cost", 50);
		int processingTime = GsonHelper.getAsInt(json, "processing_time", 100);

		return new AlloyRecipe(id, inputs, output, energyCost, processingTime);
	}

	@Override
	public AlloyRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buffer) {
		int inputCount = buffer.readVarInt();
		List<IngredientWithCount> inputs = new ArrayList<>(inputCount);
		for (int i = 0; i < inputCount; i++) {
			Ingredient ingredient = Ingredient.fromNetwork(buffer);
			int count = buffer.readVarInt();
			inputs.add(new IngredientWithCount(ingredient, count));
		}
		ItemStack output = buffer.readItem();
		int energyCost = buffer.readInt();
		int processingTime = buffer.readInt();

		return new AlloyRecipe(id, inputs, output, energyCost, processingTime);
	}

	@Override
	public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull AlloyRecipe recipe) {
		buf.writeVarInt(recipe.getInputs().size());
		for (IngredientWithCount input : recipe.getInputs()) {
			input.getIngredient().toNetwork(buf);
			buf.writeVarInt(input.getCount());
		}
		buf.writeItem(recipe.getOutput());
		buf.writeInt(recipe.getEnergyCost());
		buf.writeInt(recipe.getProcessingTime());
	}
}