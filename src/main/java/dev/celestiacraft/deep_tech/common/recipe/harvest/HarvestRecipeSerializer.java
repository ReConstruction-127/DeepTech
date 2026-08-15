package dev.celestiacraft.deep_tech.common.recipe.harvest;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import dev.celestiacraft.deep_tech.common.recipe.utils.RecipeResultUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HarvestRecipeSerializer implements RecipeSerializer<HarvestRecipe> {
	@Override
	public @NotNull HarvestRecipe fromJson(@NotNull ResourceLocation id, JsonObject json) {
		JsonObject inputJson = GsonHelper.getAsJsonObject(json, "input");
		HarvestInput input = parseInput(inputJson);

		List<HarvestResult> results = new ArrayList<>();
		JsonArray array = GsonHelper.getAsJsonArray(json, "results");
		for (JsonElement element : array) {
			JsonObject resultJson = element.getAsJsonObject();
			ItemStack stack = RecipeResultUtil.itemStackFromJson(resultJson);
			double chance = GsonHelper.getAsDouble(resultJson, "chance", 1.0);
			if (chance <= 0.0 || chance > 1.0) {
				throw new JsonSyntaxException("Invalid chance: " + chance);
			}
			results.add(new HarvestResult(stack, chance));
		}
		if (results.isEmpty()) {
			throw new JsonSyntaxException("Harvest recipe must have at least one result: " + id);
		}
		return new HarvestRecipe(id, input, results);
	}

	private HarvestInput parseInput(JsonObject inputJson) {
		if (inputJson.has("block")) {
			String blockId = GsonHelper.getAsString(inputJson, "block");
			ResourceLocation location = ResourceLocation.tryParse(blockId);
			if (location == null) {
				throw new JsonSyntaxException("Invalid block id: '" + blockId + "'");
			}
			Block block = ForgeRegistries.BLOCKS.getValue(location);
			if (block == null) {
				throw new JsonSyntaxException("Unknown block: '" + blockId + "'");
			}
			return HarvestInput.of(block);
		}
		if (inputJson.has("block_tag")) {
			String tagId = GsonHelper.getAsString(inputJson, "block_tag");
			ResourceLocation location = ResourceLocation.tryParse(tagId);
			if (location == null) {
				throw new JsonSyntaxException("Invalid block tag id: '" + tagId + "'");
			}
			return HarvestInput.ofTag(BlockTags.create(location));
		}
		throw new JsonSyntaxException("Harvest recipe input must contain 'block' or 'block_tag'");
	}

	@Override
	public HarvestRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
		boolean isTag = buf.readBoolean();
		String inputId = buf.readUtf();
		HarvestInput input;
		if (isTag) {
			input = HarvestInput.ofTag(BlockTags.create(ResourceLocation.tryParse(inputId)));
		} else {
			input = HarvestInput.of(ForgeRegistries.BLOCKS.getValue(ResourceLocation.tryParse(inputId)));
		}
		int size = buf.readVarInt();
		List<HarvestResult> results = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			results.add(new HarvestResult(buf.readItem(), buf.readDouble()));
		}
		return new HarvestRecipe(id, input, results);
	}

	@Override
	public void toNetwork(@NotNull FriendlyByteBuf buf, HarvestRecipe recipe) {
		buf.writeBoolean(recipe.getInput().isTag());
		buf.writeUtf(recipe.getInput().serializeId());
		buf.writeVarInt(recipe.getResults().size());
		for (HarvestResult result : recipe.getResults()) {
			buf.writeItem(result.stack);
			buf.writeDouble(result.chance);
		}
	}
}