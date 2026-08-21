package dev.celestiacraft.deep_tech.api.recipe.builder.harvest;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.celestiacraft.deep_tech.common.recipe.harvest.HarvestOutput;
import dev.celestiacraft.deep_tech.common.register.DTRecipes;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HarvestRecipeResult implements FinishedRecipe {
	private final ResourceLocation id;
	private final HarvestRecipeBuilder builder;
	private final Advancement.Builder advancement;
	private final ResourceLocation advancementId;

	public HarvestRecipeResult(ResourceLocation id, HarvestRecipeBuilder builder, Advancement.Builder advancement, ResourceLocation advancementId) {
		this.id = id;
		this.builder = builder;
		this.advancement = advancement;
		this.advancementId = advancementId;
	}

	@Override
	public void serializeRecipeData(@NotNull JsonObject json) {
		JsonObject input = new JsonObject();

		if (builder.getInputBlock() != null) {
			input.addProperty("block", builder.getInputBlock().builtInRegistryHolder().key().location().toString());
		} else {
			input.addProperty("block_tag", builder.getInputTag().location().toString());
		}
		json.add("input", input);

		JsonArray results = new JsonArray();
		for (HarvestOutput output : builder.getOutputs()) {
			JsonObject result = new JsonObject();
			result.addProperty("item", output.stack.getItem().builtInRegistryHolder().key().location().toString());
			if (output.stack.getCount() != 1) {
				result.addProperty("count", output.stack.getCount());
			}
			if (output.chance != 1.0) {
				result.addProperty("chance", output.chance);
			}
			results.add(result);
		}
		json.add("results", results);
	}

	@Override
	public @NotNull ResourceLocation getId() {
		return id;
	}

	@Override
	public @NotNull RecipeSerializer<?> getType() {
		return DTRecipes.HARVEST.getSerializer();
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