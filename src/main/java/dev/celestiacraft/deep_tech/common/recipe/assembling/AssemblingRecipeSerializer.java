package dev.celestiacraft.deep_tech.common.recipe.assembling;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import dev.celestiacraft.deep_tech.common.recipe.cultivation.CultivationFluidInput;
import dev.celestiacraft.deep_tech.common.recipe.utils.RecipeResultUtil;
import dev.celestiacraft.libs.api.recipe.ingredient.item.IngredientWithCount;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AssemblingRecipeSerializer implements RecipeSerializer<AssemblingRecipe> {
	@Override
	public @NotNull AssemblingRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
		JsonArray inputsArray = GsonHelper.getAsJsonArray(json, "item_inputs");
		if (inputsArray.isEmpty()) {
			throw new JsonSyntaxException("Assembling recipe " + id + " must have at least one item input");
		}
		List<IngredientWithCount> itemInputs = new ArrayList<>();
		for (JsonElement element : inputsArray) {
			int count = 1;
			if (element.isJsonObject()) {
				count = GsonHelper.getAsInt(element.getAsJsonObject(), "count", 1);
			}
			itemInputs.add(new IngredientWithCount(Ingredient.fromJson(element), count));
		}

		List<CultivationFluidInput> fluidInputs = new ArrayList<>();
		if (json.has("fluid_inputs")) {
			for (JsonElement element : GsonHelper.getAsJsonArray(json, "fluid_inputs")) {
				JsonObject obj = GsonHelper.convertToJsonObject(element, "fluid_inputs entry");
				FluidStack stack = RecipeResultUtil.fluidStackFromJson(obj);
				fluidInputs.add(new CultivationFluidInput(stack.getFluid(), stack.getAmount()));
			}
		}

		Ingredient catalyst = null;
		if (json.has("catalyst")) {
			catalyst = Ingredient.fromJson(json.get("catalyst"));
		}

		JsonArray outputsArray = GsonHelper.getAsJsonArray(json, "item_outputs");
		if (outputsArray.isEmpty()) {
			throw new JsonSyntaxException("Assembling recipe " + id + " must have at least one item output");
		}
		List<ItemStack> itemOutputs = new ArrayList<>();
		for (JsonElement element : outputsArray) {
			itemOutputs.add(RecipeResultUtil.itemStackFromJson(GsonHelper.convertToJsonObject(element, "item_outputs entry")));
		}

		List<FluidStack> fluidOutputs = new ArrayList<>();
		if (json.has("fluid_outputs")) {
			for (JsonElement element : GsonHelper.getAsJsonArray(json, "fluid_outputs")) {
				fluidOutputs.add(RecipeResultUtil.fluidStackFromJson(GsonHelper.convertToJsonObject(element, "fluid_outputs entry")));
			}
		}

		int energyCost = GsonHelper.getAsInt(json, "energy_cost", 50);
		int processingTime = GsonHelper.getAsInt(json, "processing_time", 100);

		return new AssemblingRecipe(id, itemInputs, fluidInputs, catalyst, itemOutputs, fluidOutputs, energyCost, processingTime);
	}

	@Override
	public AssemblingRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buffer) {
		int inputCount = buffer.readVarInt();
		List<IngredientWithCount> itemInputs = new ArrayList<>(inputCount);
		for (int i = 0; i < inputCount; i++) {
			Ingredient ingredient = Ingredient.fromNetwork(buffer);
			int count = buffer.readVarInt();
			itemInputs.add(new IngredientWithCount(ingredient, count));
		}
		int fluidInputCount = buffer.readVarInt();
		List<CultivationFluidInput> fluidInputs = new ArrayList<>(fluidInputCount);
		for (int i = 0; i < fluidInputCount; i++) {
			fluidInputs.add(new CultivationFluidInput(readFluid(buffer), buffer.readInt()));
		}
		boolean hasCatalyst = buffer.readBoolean();
		Ingredient catalyst = hasCatalyst ? Ingredient.fromNetwork(buffer) : null;
		int outputCount = buffer.readVarInt();
		List<ItemStack> itemOutputs = new ArrayList<>(outputCount);
		for (int i = 0; i < outputCount; i++) {
			itemOutputs.add(buffer.readItem());
		}
		int fluidOutputCount = buffer.readVarInt();
		List<FluidStack> fluidOutputs = new ArrayList<>(fluidOutputCount);
		for (int i = 0; i < fluidOutputCount; i++) {
			fluidOutputs.add(new FluidStack(readFluid(buffer), buffer.readInt()));
		}
		int energyCost = buffer.readInt();
		int processingTime = buffer.readInt();

		return new AssemblingRecipe(id, itemInputs, fluidInputs, catalyst, itemOutputs, fluidOutputs, energyCost, processingTime);
	}

	@Override
	public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull AssemblingRecipe recipe) {
		buf.writeVarInt(recipe.getItemInputs().size());
		for (IngredientWithCount input : recipe.getItemInputs()) {
			input.getIngredient().toNetwork(buf);
			buf.writeVarInt(input.getCount());
		}
		buf.writeVarInt(recipe.getFluidInputs().size());
		for (CultivationFluidInput input : recipe.getFluidInputs()) {
			writeFluid(buf, input.fluid());
			buf.writeInt(input.amount());
		}
		Ingredient catalyst = recipe.getCatalyst();
		buf.writeBoolean(catalyst != null);
		if (catalyst != null) {
			catalyst.toNetwork(buf);
		}
		buf.writeVarInt(recipe.getItemOutputs().size());
		for (ItemStack output : recipe.getItemOutputs()) {
			buf.writeItem(output);
		}
		buf.writeVarInt(recipe.getFluidOutputs().size());
		for (FluidStack output : recipe.getFluidOutputs()) {
			writeFluid(buf, output.getFluid());
			buf.writeInt(output.getAmount());
		}
		buf.writeInt(recipe.getEnergyCost());
		buf.writeInt(recipe.getProcessingTime());
	}

	private static void writeFluid(FriendlyByteBuf buf, net.minecraft.world.level.material.Fluid fluid) {
		buf.writeUtf(ForgeRegistries.FLUIDS.getKey(fluid).toString());
	}

	private static net.minecraft.world.level.material.Fluid readFluid(FriendlyByteBuf buf) {
		ResourceLocation id = ResourceLocation.tryParse(buf.readUtf());
		return id == null ? null : ForgeRegistries.FLUIDS.getValue(id);
	}
}