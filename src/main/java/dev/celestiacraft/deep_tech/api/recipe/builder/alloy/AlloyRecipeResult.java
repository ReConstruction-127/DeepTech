package dev.celestiacraft.deep_tech.api.recipe.builder.alloy;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.celestiacraft.deep_tech.api.ingredien.IngredientWithCount;
import dev.celestiacraft.deep_tech.common.register.DTRecipes;
import lombok.AllArgsConstructor;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@AllArgsConstructor
public class AlloyRecipeResult implements FinishedRecipe {
	private final ResourceLocation id;
	private final List<IngredientWithCount> inputs;
	private final ItemStack output;
	private final int energyCost;
	private final int processingTime;

	private final Advancement.Builder advancement;
	private final ResourceLocation advancementId;

	@Override
	public void serializeRecipeData(@NotNull JsonObject json) {
		JsonArray inputsArray = new JsonArray();
		for (IngredientWithCount input : inputs) {
			JsonElement element = input.getIngredient().toJson();
			if (input.getCount() != 1) {
				if (!element.isJsonObject()) {
					throw new IllegalStateException("Cannot add count to a multi-value ingredient");
				}
				element.getAsJsonObject().addProperty("count", input.getCount());
			}
			inputsArray.add(element);
		}
		json.add("inputs", inputsArray);

		JsonObject result = new JsonObject();
		result.addProperty("item", output.getItem().builtInRegistryHolder().key().location().toString());

		if (output.getCount() != 1) {
			result.addProperty("count", output.getCount());
		}

		json.add("result", result);

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
		return DTRecipes.ALLOY.getSerializer();
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
