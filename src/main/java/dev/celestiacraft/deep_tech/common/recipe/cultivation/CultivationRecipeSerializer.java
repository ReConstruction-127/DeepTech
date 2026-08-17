package dev.celestiacraft.deep_tech.common.recipe.cultivation;

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
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CultivationRecipeSerializer implements RecipeSerializer<CultivationRecipe> {
	@Override
	public @NotNull CultivationRecipe fromJson(@NotNull ResourceLocation id, JsonObject json) {
		List<IngredientWithCount> itemInputs = new ArrayList<>();
		JsonArray itemInputsArray = GsonHelper.getAsJsonArray(json, "item_inputs", new JsonArray());
		for (JsonElement element : itemInputsArray) {
			JsonObject inputObject = element.getAsJsonObject();
			Ingredient ingredient = Ingredient.fromJson(inputObject);
			int count = GsonHelper.getAsInt(inputObject, "count", 1);
			itemInputs.add(new IngredientWithCount(ingredient, count));
		}

		List<CultivationFluidInput> fluidInputs = new ArrayList<>();
		JsonArray fluidInputsArray = GsonHelper.getAsJsonArray(json, "fluid_inputs", new JsonArray());
		for (JsonElement element : fluidInputsArray) {
			JsonObject inputObject = element.getAsJsonObject();
			fluidInputs.add(new CultivationFluidInput(
					parseFluid(inputObject),
					GsonHelper.getAsInt(inputObject, "amount", 1)
			));
		}

		List<ItemStack> itemOutputs = new ArrayList<>();
		JsonArray itemOutputsArray = GsonHelper.getAsJsonArray(json, "item_outputs", new JsonArray());
		for (JsonElement element : itemOutputsArray) {
			itemOutputs.add(RecipeResultUtil.itemStackFromJson(element.getAsJsonObject()));
		}

		List<FluidStack> fluidOutputs = new ArrayList<>();
		JsonArray fluidOutputsArray = GsonHelper.getAsJsonArray(json, "fluid_outputs", new JsonArray());
		for (JsonElement element : fluidOutputsArray) {
			JsonObject outputObject = element.getAsJsonObject();
			fluidOutputs.add(new FluidStack(
					parseFluid(outputObject),
					GsonHelper.getAsInt(outputObject, "amount", 1)
			));
		}

		int energyCost = GsonHelper.getAsInt(json, "energy_cost", 50);
		int processingTime = GsonHelper.getAsInt(json, "processing_time", 100);
		float itemOutputChance = GsonHelper.getAsFloat(json, "item_output_chance", 1.0f);

		return new CultivationRecipe(id, itemInputs, fluidInputs, itemOutputs, fluidOutputs, energyCost, processingTime, itemOutputChance);
	}

	private static Fluid parseFluid(JsonObject object) {
		String fluidName = GsonHelper.getAsString(object, "fluid");
		ResourceLocation fluidId = ResourceLocation.tryParse(fluidName);
		if (fluidId == null) {
			throw new JsonSyntaxException("Invalid fluid id: '" + fluidName + "'");
		}
		Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidId);
		if (fluid == null) {
			throw new JsonSyntaxException("Unknown fluid: '" + fluidName + "'");
		}
		return fluid;
	}

	@Override
	public CultivationRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
		List<IngredientWithCount> itemInputs = new ArrayList<>();
		int itemInputCount = buf.readVarInt();
		for (int i = 0; i < itemInputCount; i++) {
			itemInputs.add(new IngredientWithCount(
					Ingredient.fromNetwork(buf),
					buf.readVarInt()
			));
		}

		List<CultivationFluidInput> fluidInputs = new ArrayList<>();
		int fluidInputCount = buf.readVarInt();
		for (int i = 0; i < fluidInputCount; i++) {
			fluidInputs.add(new CultivationFluidInput(
					readFluid(buf),
					buf.readVarInt()
			));
		}

		List<ItemStack> itemOutputs = new ArrayList<>();
		int itemOutputCount = buf.readVarInt();
		for (int i = 0; i < itemOutputCount; i++) {
			itemOutputs.add(buf.readItem());
		}

		List<FluidStack> fluidOutputs = new ArrayList<>();
		int fluidOutputCount = buf.readVarInt();
		for (int i = 0; i < fluidOutputCount; i++) {
			fluidOutputs.add(FluidStack.readFromPacket(buf));
		}

		int energyCost = buf.readInt();
		int processingTime = buf.readInt();
		float itemOutputChance = buf.readFloat();

		return new CultivationRecipe(id, itemInputs, fluidInputs, itemOutputs, fluidOutputs, energyCost, processingTime, itemOutputChance);
	}

	@Override
	public void toNetwork(@NotNull FriendlyByteBuf buf, CultivationRecipe recipe) {
		buf.writeVarInt(recipe.getItemInputs().size());
		for (IngredientWithCount input : recipe.getItemInputs()) {
			input.getIngredient().toNetwork(buf);
			buf.writeVarInt(input.getCount());
		}

		buf.writeVarInt(recipe.getFluidInputs().size());
		for (CultivationFluidInput input : recipe.getFluidInputs()) {
			writeFluid(buf, input.fluid());
			buf.writeVarInt(input.amount());
		}

		buf.writeVarInt(recipe.getItemOutputs().size());
		for (ItemStack output : recipe.getItemOutputs()) {
			buf.writeItem(output);
		}

		buf.writeVarInt(recipe.getFluidOutputs().size());
		for (FluidStack output : recipe.getFluidOutputs()) {
			output.writeToPacket(buf);
		}

		buf.writeInt(recipe.getEnergyCost());
		buf.writeInt(recipe.getProcessingTime());
		buf.writeFloat(recipe.getItemOutputChance());
	}

	private static Fluid readFluid(FriendlyByteBuf buf) {
		ResourceLocation id = ResourceLocation.tryParse(buf.readUtf());
		if (id == null) {
			throw new JsonSyntaxException("Invalid fluid id in packet");
		}
		Fluid fluid = ForgeRegistries.FLUIDS.getValue(id);
		if (fluid == null) {
			throw new JsonSyntaxException("Unknown fluid in packet: '" + id + "'");
		}
		return fluid;
	}

	private static void writeFluid(FriendlyByteBuf buf, Fluid fluid) {
		buf.writeUtf(ForgeRegistries.FLUIDS.getKey(fluid).toString());
	}
}