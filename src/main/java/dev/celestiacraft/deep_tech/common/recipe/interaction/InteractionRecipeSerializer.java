package dev.celestiacraft.deep_tech.common.recipe.interaction;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.celestiacraft.deep_tech.common.recipe.utils.RecipeResultUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class InteractionRecipeSerializer implements RecipeSerializer<InteractionRecipe> {
	@Override
	public @NotNull InteractionRecipe fromJson(@NotNull ResourceLocation id, JsonObject json) {
		Ingredient trigger = Ingredient.fromJson(json.get("trigger_item"));

		String blockName = GsonHelper.getAsString(json, "target_block");
		ResourceLocation blockId = ResourceLocation.tryParse(blockName);
		Block block = blockId != null ? ForgeRegistries.BLOCKS.getValue(blockId) : null;
		BlockState targetState = (block == null ? Blocks.AIR : block).defaultBlockState();

		List<ChanceResult> results = new ArrayList<>();
		JsonArray resultsArray = GsonHelper.getAsJsonArray(json, "results");
		for (var elem : resultsArray) {
			JsonObject obj = elem.getAsJsonObject();
			ItemStack stack = RecipeResultUtil.itemStackFromJson(obj);
			// ✅ 读取 chance，默认 1.0（100%）
			double chance = GsonHelper.getAsDouble(obj, "chance", 1.0);
			results.add(new ChanceResult(stack, chance));
		}

		ExtraEffect extraEffect = null;
		if (json.has("extra_effect")) {
			JsonObject extraObj = json.getAsJsonObject("extra_effect");
			float chance = GsonHelper.getAsFloat(extraObj, "chance", 0.1f);
			String toBlockName = GsonHelper.getAsString(extraObj, "to_block", "minecraft:air");
			ResourceLocation toBlockId = ResourceLocation.tryParse(toBlockName);
			Block toBlock = toBlockId != null ? ForgeRegistries.BLOCKS.getValue(toBlockId) : null;
			BlockState toState = (toBlock == null ? Blocks.AIR : toBlock).defaultBlockState();
			List<ItemStack> extraDrops = new ArrayList<>();

			if (extraObj.has("drops")) {
				JsonArray dropsArray = extraObj.getAsJsonArray("drops");

				for (JsonElement element : dropsArray) {
					extraDrops.add(RecipeResultUtil.itemStackFromJson(element.getAsJsonObject()));
				}
			}
			extraEffect = new ExtraEffect(chance, toState, extraDrops);
		}

		boolean consume = GsonHelper.getAsBoolean(json, "consume_trigger", false);

		// 解析交互类型（默认为 ANY）
		InteractionType type = InteractionType.ANY;
		if (json.has("interaction_type")) {
			String typeStr = GsonHelper.getAsString(json, "interaction_type");
			try {
				type = InteractionType.valueOf(typeStr.toUpperCase());
			} catch (IllegalArgumentException ignored) {
			}
		}

		return new InteractionRecipe(id, trigger, targetState, results, extraEffect, consume, type);
	}

	@Override
	public InteractionRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
		Ingredient trigger = Ingredient.fromNetwork(buf);
		ResourceLocation targetBlockId = buf.readResourceLocation();
		Block targetBlock = ForgeRegistries.BLOCKS.getValue(targetBlockId);
		BlockState targetState = (targetBlock == null ? Blocks.AIR : targetBlock).defaultBlockState();

		int resultCount = buf.readInt();
		List<ChanceResult> results = new ArrayList<>();

		for (int i = 0; i < resultCount; i++) {
			ItemStack stack = buf.readItem();
			int weight = buf.readInt();
			results.add(new ChanceResult(stack, weight));
		}

		boolean hasExtra = buf.readBoolean();
		ExtraEffect extra = null;

		if (hasExtra) {
			float chance = buf.readFloat();
			ResourceLocation toBlockId = buf.readResourceLocation();
			Block toBlock = ForgeRegistries.BLOCKS.getValue(toBlockId);
			BlockState toState = (toBlock == null ? Blocks.AIR : toBlock).defaultBlockState();
			int dropCount = buf.readInt();
			List<ItemStack> extraDrops = new ArrayList<>();
			for (int i = 0; i < dropCount; i++) {
				extraDrops.add(buf.readItem());
			}
			extra = new ExtraEffect(chance, toState, extraDrops);
		}

		boolean consume = buf.readBoolean();
		InteractionType type = buf.readEnum(InteractionType.class);

		return new InteractionRecipe(id, trigger, targetState, results, extra, consume, type);
	}

	@Override
	public void toNetwork(@NotNull FriendlyByteBuf buf, InteractionRecipe recipe) {
		recipe.getTriggerItem().toNetwork(buf);
		ResourceLocation targetBlockId = ForgeRegistries.BLOCKS.getKey(recipe.getTargetBlockState().getBlock());
		buf.writeResourceLocation(targetBlockId);

		List<ChanceResult> results = recipe.getResults();
		buf.writeInt(results.size());
		for (var cr : results) {
			buf.writeItem(cr.stack);
			buf.writeDouble(cr.chance);
		}

		ExtraEffect extra = recipe.getExtraEffect();
		buf.writeBoolean(extra != null);

		if (extra != null) {
			buf.writeDouble(extra.getChance());
			ResourceLocation toBlockId = ForgeRegistries.BLOCKS.getKey(extra.getState().getBlock());
			buf.writeResourceLocation(toBlockId);
			buf.writeInt(extra.getExtraDrops().size());
			for (ItemStack drop : extra.getExtraDrops()) {
				buf.writeItem(drop);
			}
		}

		buf.writeBoolean(recipe.isConsumeTrigger());
		buf.writeEnum(recipe.getInteractionType());
	}
}