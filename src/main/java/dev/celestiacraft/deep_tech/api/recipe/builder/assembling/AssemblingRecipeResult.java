package dev.celestiacraft.deep_tech.api.recipe.builder.assembling;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.celestiacraft.deep_tech.common.recipe.cultivation.CultivationFluidInput;
import dev.celestiacraft.deep_tech.common.register.DTRecipes;
import dev.celestiacraft.libs.api.recipe.ingredient.item.IngredientWithCount;
import lombok.AllArgsConstructor;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@AllArgsConstructor
public class AssemblingRecipeResult implements FinishedRecipe {
	private final ResourceLocation id;
	private final List<IngredientWithCount> itemInputs;
	private final List<CultivationFluidInput> fluidInputs;
	private final Ingredient catalyst;
	private final List<ItemStack> itemOutputs;
	private final List<FluidStack> fluidOutputs;
	private final int energyCost;
	private final int processingTime;

	private final Advancement.Builder advancement;
	private final ResourceLocation advancementId;

	@Override
	public void serializeRecipeData(@NotNull JsonObject json) {
		JsonArray inputsArray = new JsonArray();
		for (IngredientWithCount input : itemInputs) {
			JsonElement element = input.getIngredient().toJson();
			if (input.getCount() != 1) {
				if (!element.isJsonObject()) {
					throw new IllegalStateException("Cannot add count to a multi-value ingredient");
				}
				element.getAsJsonObject().addProperty("count", input.getCount());
			}
			inputsArray.add(element);
		}
		json.add("item_inputs", inputsArray);

		if (!fluidInputs.isEmpty()) {
			JsonArray fluidsArray = new JsonArray();
			for (CultivationFluidInput input : fluidInputs) {
				JsonObject obj = new JsonObject();
				obj.addProperty("fluid", input.fluid().builtInRegistryHolder().key().location().toString());
				obj.addProperty("amount", input.amount());
				fluidsArray.add(obj);
			}
			json.add("fluid_inputs", fluidsArray);
		}

		if (catalyst != null) {
			json.add("catalyst", catalyst.toJson());
		}

		JsonArray outputsArray = new JsonArray();
		for (ItemStack output : itemOutputs) {
			JsonObject result = new JsonObject();
			result.addProperty("item", output.getItem().builtInRegistryHolder().key().location().toString());
			if (output.getCount() != 1) {
				result.addProperty("count", output.getCount());
			}
			outputsArray.add(result);
		}
		json.add("item_outputs", outputsArray);

		if (!fluidOutputs.isEmpty()) {
			JsonArray fluidsArray = new JsonArray();
			for (FluidStack output : fluidOutputs) {
				JsonObject obj = new JsonObject();
				obj.addProperty("fluid", output.getFluid().builtInRegistryHolder().key().location().toString());
				obj.addProperty("amount", output.getAmount());
				fluidsArray.add(obj);
			}
			json.add("fluid_outputs", fluidsArray);
		}

		if (energyCost != 50) {
			json.addProperty("energy_cost", energyCost);
		}

		if (processingTime != 100) {
			json.addProperty("processing_time", processingTime);
		}
	}

	@Override
	public @NotNull ResourceLocation getId() {
		return id;
	}

	@Override
	public @NotNull RecipeSerializer<?> getType() {
		return DTRecipes.ASSEMBLING.getSerializer();
	}

	@Nullable
	@Override
	public JsonObject serializeAdvancement() {
		return advancement.serializeToJson();
	}

	@Nullable
	@Override
	public ResourceLocation getAdvancementId() {
		return advancementId;
	}
}