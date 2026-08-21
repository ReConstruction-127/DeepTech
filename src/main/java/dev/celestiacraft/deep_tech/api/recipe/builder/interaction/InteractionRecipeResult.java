package dev.celestiacraft.deep_tech.api.recipe.builder.interaction;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.celestiacraft.deep_tech.common.recipe.interaction.ChanceResult;
import dev.celestiacraft.deep_tech.common.recipe.interaction.InteractionType;
import dev.celestiacraft.deep_tech.common.register.DTRecipes;
import lombok.AllArgsConstructor;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@AllArgsConstructor
public class InteractionRecipeResult implements FinishedRecipe {
	private final ResourceLocation id;
	private final Ingredient triggerItem;
	private final BlockState targetState;
	private final List<ChanceResult> results;
	private final ExtraEffect extraEffect;
	private final boolean consumeTrigger;
	private final InteractionType interactionType;
	private final Advancement.Builder advancement;
	private final ResourceLocation advancementId;

	@Override
	public void serializeRecipeData(JsonObject json) {
		json.add("trigger_item", triggerItem.toJson());
		json.addProperty("target_block", ForgeRegistries.BLOCKS.getKey(targetState.getBlock()).toString());

		JsonArray resultsArray = new JsonArray();
		for (ChanceResult wr : results) {
			JsonObject obj = new JsonObject();
			obj.addProperty("item", ForgeRegistries.ITEMS.getKey(wr.stack.getItem()).toString());
			if (wr.stack.getCount() != 1) obj.addProperty("count", wr.stack.getCount());
// ✅ 写入 chance(不再写入 weight)
			obj.addProperty("chance", wr.chance);
			resultsArray.add(obj);
		}
		json.add("results", resultsArray);

		if (extraEffect != null) {
			JsonObject extraObj = new JsonObject();
			extraObj.addProperty("chance", extraEffect.getChance());
			extraObj.addProperty("to_block", ForgeRegistries.BLOCKS.getKey(extraEffect.getState().getBlock()).toString());

			if (!extraEffect.getExtraDrops().isEmpty()) {
				JsonArray dropsArray = new JsonArray();

				for (ItemStack drop : extraEffect.getExtraDrops()) {
					JsonObject dropObj = new JsonObject();
					dropObj.addProperty("item", ForgeRegistries.ITEMS.getKey(drop.getItem()).toString());
					if (drop.getCount() != 1) dropObj.addProperty("count", drop.getCount());
					dropsArray.add(dropObj);
				}
				extraObj.add("drops", dropsArray);
			}
			json.add("extra_effect", extraObj);
		}

		if (consumeTrigger) {
			json.addProperty("consume_trigger", true);
		}

		// 写入交互类型(如果非 ANY)
		if (interactionType != InteractionType.ANY) {
			json.addProperty("interaction_type", interactionType.name().toLowerCase());
		}
	}

	@Override
	public @NotNull ResourceLocation getId() {
		return id;
	}

	@Override
	public @NotNull RecipeSerializer<?> getType() {
		return DTRecipes.INTERACTION.getSerializer();
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