package dev.celestiacraft.deep_tech.common.recipe.processor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import dev.celestiacraft.deep_tech.common.recipe.utils.RecipeResultUtil;
import dev.celestiacraft.libs.api.recipe.ingredient.item.IngredientWithCount;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ProcessorRecipeSerializer implements RecipeSerializer<ProcessorRecipe> {
	@Override
	public @NotNull ProcessorRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
		JsonArray inputsArray = GsonHelper.getAsJsonArray(json, "item_inputs");
		if (inputsArray.isEmpty()) {
			throw new JsonSyntaxException("Processor recipe " + id + " must have at least one input");
		}
		List<IngredientWithCount> itemInputs = new ArrayList<>();
		for (JsonElement element : inputsArray) {
			int count = 1;
			if (element.isJsonObject()) {
				count = GsonHelper.getAsInt(element.getAsJsonObject(), "count", 1);
			}
			itemInputs.add(new IngredientWithCount(Ingredient.fromJson(element), count));
		}

		JsonArray outputsArray = GsonHelper.getAsJsonArray(json, "item_outputs");
		if (outputsArray.isEmpty()) {
			throw new JsonSyntaxException("Processor recipe " + id + " must have at least one output");
		}
		List<ItemStack> itemOutputs = new ArrayList<>();
		for (JsonElement element : outputsArray) {
			itemOutputs.add(RecipeResultUtil.itemStackFromJson(GsonHelper.convertToJsonObject(element, "item_outputs entry")));
		}

		int energyCost = GsonHelper.getAsInt(json, "energy_cost", 50);
		int processingTime = GsonHelper.getAsInt(json, "processing_time", 100);

		return new ProcessorRecipe(id, itemInputs, itemOutputs, energyCost, processingTime);
	}

	@Override
	public ProcessorRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buffer) {
		int inputCount = buffer.readVarInt();
		List<IngredientWithCount> itemInputs = new ArrayList<>(inputCount);
		for (int i = 0; i < inputCount; i++) {
			Ingredient ingredient = Ingredient.fromNetwork(buffer);
			int count = buffer.readVarInt();
			itemInputs.add(new IngredientWithCount(ingredient, count));
		}
		int outputCount = buffer.readVarInt();
		List<ItemStack> itemOutputs = new ArrayList<>(outputCount);
		for (int i = 0; i < outputCount; i++) {
			itemOutputs.add(buffer.readItem());
		}
		int energyCost = buffer.readInt();
		int processingTime = buffer.readInt();

		return new ProcessorRecipe(id, itemInputs, itemOutputs, energyCost, processingTime);
	}

	@Override
	public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull ProcessorRecipe recipe) {
		buf.writeVarInt(recipe.getItemInputs().size());
		for (IngredientWithCount input : recipe.getItemInputs()) {
			input.getIngredient().toNetwork(buf);
			buf.writeVarInt(input.getCount());
		}
		buf.writeVarInt(recipe.getItemOutputs().size());
		for (ItemStack output : recipe.getItemOutputs()) {
			buf.writeItem(output);
		}
		buf.writeInt(recipe.getEnergyCost());
		buf.writeInt(recipe.getProcessingTime());
	}
}