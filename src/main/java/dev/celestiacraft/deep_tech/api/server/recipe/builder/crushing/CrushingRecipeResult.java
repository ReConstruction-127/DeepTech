package dev.celestiacraft.deep_tech.api.server.recipe.builder.crushing;

import com.google.gson.JsonObject;
import dev.celestiacraft.deep_tech.common.register.DTRecipes;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CrushingRecipeResult implements FinishedRecipe {
	private final ResourceLocation id;
	private final Ingredient input;
	private final ItemStack output;
	private final int energyCost;
	private final int processingTime;

	private final Advancement.Builder advancement;
	private final ResourceLocation advancementId;

	public CrushingRecipeResult(
			ResourceLocation id,
			Ingredient input,
			ItemStack output,
			int energyCost,
			int processingTime,
			Advancement.Builder advancement,
			ResourceLocation advancementId
	) {
		this.id = id;
		this.input = input;
		this.output = output;
		this.energyCost = energyCost;
		this.processingTime = processingTime;
		this.advancement = advancement;
		this.advancementId = advancementId;
	}

	@Override
	public void serializeRecipeData(JsonObject json) {
		json.add("input", input.toJson());

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
		return DTRecipes.CRUSHING.getSerializer();
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