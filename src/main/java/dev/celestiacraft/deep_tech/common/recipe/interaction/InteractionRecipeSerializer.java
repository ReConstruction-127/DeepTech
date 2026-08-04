package dev.celestiacraft.deep_tech.common.recipe.interaction;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.celestiacraft.deep_tech.common.recipe.utils.RecipeResultUtil;
import net.minecraft.core.registries.BuiltInRegistries;
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
		Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName));
		BlockState targetState = (block == null ? Blocks.AIR : block).defaultBlockState();

		List<InteractionRecipe.WeightedResult> results = new ArrayList<>();
		JsonArray resultsArray = GsonHelper.getAsJsonArray(json, "results");
		for (var elem : resultsArray) {
			JsonObject obj = elem.getAsJsonObject();
			ItemStack stack = RecipeResultUtil.itemStackFromJson(obj);
			int weight = GsonHelper.getAsInt(obj, "weight", 1);
			results.add(new InteractionRecipe.WeightedResult(stack, weight));
		}

		InteractionRecipe.ExtraEffect extraEffect = null;
		if (json.has("extra_effect")) {
			JsonObject extraObj = json.getAsJsonObject("extra_effect");
			float chance = GsonHelper.getAsFloat(extraObj, "chance", 0.1f);
			String toBlockName = GsonHelper.getAsString(extraObj, "to_block", "minecraft:air");
			Block toBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(toBlockName));
			BlockState toState = (toBlock == null ? Blocks.AIR : toBlock).defaultBlockState();
			List<ItemStack> extraDrops = new ArrayList<>();
			if (extraObj.has("drops")) {
				JsonArray dropsArray = extraObj.getAsJsonArray("drops");
				for (var dropElem : dropsArray) {
					extraDrops.add(RecipeResultUtil.itemStackFromJson(dropElem.getAsJsonObject()));
				}
			}
			extraEffect = new InteractionRecipe.ExtraEffect(chance, toState, extraDrops);
		}

		boolean consume = GsonHelper.getAsBoolean(json, "consume_trigger", false);

		return new InteractionRecipe(id, trigger, targetState, results, extraEffect, consume);
	}

	@Override
	public InteractionRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buf) {
		Ingredient trigger = Ingredient.fromNetwork(buf);
		// 使用 BuiltInRegistries.BLOCK 读取方块
		Block targetBlock = buf.readById(BuiltInRegistries.BLOCK);
		BlockState targetState = (targetBlock == null ? Blocks.AIR : targetBlock).defaultBlockState();

		int resultCount = buf.readInt();
		List<InteractionRecipe.WeightedResult> results = new ArrayList<>();
		for (int i = 0; i < resultCount; i++) {
			ItemStack stack = buf.readItem();
			int weight = buf.readInt();
			results.add(new InteractionRecipe.WeightedResult(stack, weight));
		}

		boolean hasExtra = buf.readBoolean();
		InteractionRecipe.ExtraEffect extra = null;
		if (hasExtra) {
			float chance = buf.readFloat();
			Block toBlock = buf.readById(BuiltInRegistries.BLOCK);
			BlockState toState = (toBlock == null ? Blocks.AIR : toBlock).defaultBlockState();
			int dropCount = buf.readInt();
			List<ItemStack> extraDrops = new ArrayList<>();
			for (int i = 0; i < dropCount; i++) {
				extraDrops.add(buf.readItem());
			}
			extra = new InteractionRecipe.ExtraEffect(chance, toState, extraDrops);
		}

		boolean consume = buf.readBoolean();
		return new InteractionRecipe(id, trigger, targetState, results, extra, consume);
	}

	@Override
	public void toNetwork(@NotNull FriendlyByteBuf buf, InteractionRecipe recipe) {
		recipe.getTriggerItem().toNetwork(buf);
		// 使用 BuiltInRegistries.BLOCK 写入方块
		buf.writeId(BuiltInRegistries.BLOCK, recipe.getTargetBlockState().getBlock());

		List<InteractionRecipe.WeightedResult> results = recipe.getResults();
		buf.writeInt(results.size());
		for (var wr : results) {
			buf.writeItem(wr.stack);
			buf.writeInt(wr.weight);
		}

		InteractionRecipe.ExtraEffect extra = recipe.getExtraEffect();
		buf.writeBoolean(extra != null);
		if (extra != null) {
			buf.writeFloat(extra.chance);
			buf.writeId(BuiltInRegistries.BLOCK, extra.toState.getBlock());
			buf.writeInt(extra.extraDrops.size());
			for (ItemStack drop : extra.extraDrops) {
				buf.writeItem(drop);
			}
		}

		buf.writeBoolean(recipe.isConsumeTrigger());
	}
}