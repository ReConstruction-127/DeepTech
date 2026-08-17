package dev.celestiacraft.deep_tech.api.recipe.builder.cultivation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.celestiacraft.deep_tech.common.register.DTRecipes;
import dev.celestiacraft.deep_tech.common.recipe.cultivation.CultivationFluidInput;
import dev.celestiacraft.libs.api.recipe.ingredient.item.IngredientWithCount;
import lombok.AllArgsConstructor;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@AllArgsConstructor
public class CultivationRecipeResult implements FinishedRecipe {
	private final ResourceLocation id;
	private final List<IngredientWithCount> itemInputs;
	private final List<CultivationFluidInput> fluidInputs;
	private final List<ItemStack> itemOutputs;
	private final List<FluidStack> fluidOutputs;
	private final int energyCost;
	private final int processingTime;
	private final float itemOutputChance;

	private final Advancement.Builder advancement;
	private final ResourceLocation advancementId;

	@Override
	public void serializeRecipeData(@NotNull JsonObject json) {
		JsonArray itemInputsArray = new JsonArray();
		for (IngredientWithCount input : itemInputs) {
			JsonElement element = input.getIngredient().toJson();
			if (input.getCount() != 1) {
				if (!element.isJsonObject()) {
					throw new IllegalStateException("Cannot add count to a multi-value ingredient");
				}
				element.getAsJsonObject().addProperty("count", input.getCount());
			}
			itemInputsArray.add(element);
		}
		json.add("item_inputs", itemInputsArray);

		JsonArray fluidInputsArray = new JsonArray();
		for (CultivationFluidInput input : fluidInputs) {
			JsonObject inputObject = new JsonObject();
			inputObject.addProperty("fluid", ForgeRegistries.FLUIDS.getKey(input.fluid()).toString());
			inputObject.addProperty("amount", input.amount());
			fluidInputsArray.add(inputObject);
		}
		json.add("fluid_inputs", fluidInputsArray);

		JsonArray itemOutputsArray = new JsonArray();
		for (ItemStack output : itemOutputs) {
			JsonObject outputObject = new JsonObject();
			outputObject.addProperty("item", output.getItem().builtInRegistryHolder().key().location().toString());
			if (output.getCount() != 1) {
				outputObject.addProperty("count", output.getCount());
			}
			itemOutputsArray.add(outputObject);
		}
		json.add("item_outputs", itemOutputsArray);

		JsonArray fluidOutputsArray = new JsonArray();
		for (FluidStack output : fluidOutputs) {
			JsonObject outputObject = new JsonObject();
			outputObject.addProperty("fluid", ForgeRegistries.FLUIDS.getKey(output.getFluid()).toString());
			outputObject.addProperty("amount", output.getAmount());
			fluidOutputsArray.add(outputObject);
		}
		json.add("fluid_outputs", fluidOutputsArray);

		if (energyCost != 50) {
			json.addProperty("energy_cost", energyCost);
		}

		if (processingTime != 100) {
			json.addProperty("processing_time", processingTime);
		}

		if (itemOutputChance < 1.0f) {
			json.addProperty("item_output_chance", itemOutputChance);
		}
	}

	@Override
	public @NotNull ResourceLocation getId() {
		return id;
	}

	@Override
	public @NotNull RecipeSerializer<?> getType() {
		return DTRecipes.CULTIVATION.getSerializer();
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