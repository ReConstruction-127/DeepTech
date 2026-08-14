package dev.celestiacraft.deep_tech.common.recipe.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * 配方 JSON 解析工具类
 * <p>
 * 用于在 RecipeSerializer 中统一解析配方 JSON 中的公共字段
 */
public class RecipeResultUtil {
	private RecipeResultUtil() {
	}

	/**
	 * 从配方的 result 对象中解析输出物品
	 * <p>
	 * JSON 格式: {@code {"item": "minecraft:stone", "count": 1}}, count 缺省为 1
	 *
	 * @param result 配方的 result JSON 对象
	 * @return 解析得到的输出物品
	 * @throws JsonSyntaxException 当 item 缺失, ID 非法, 物品不存在或 count 小于 1 时抛出
	 */
	public static ItemStack itemStackFromJson(JsonObject result) {
		String itemName = GsonHelper.getAsString(result, "item");
		ResourceLocation itemId = ResourceLocation.tryParse(itemName);
		if (itemId == null) {
			throw new JsonSyntaxException("Invalid item id: '" + itemName + "'");
		}
		Item item = ForgeRegistries.ITEMS.getValue(itemId);
		if (item == null) {
			throw new JsonSyntaxException("Unknown item: '" + itemName + "'");
		}
		int count = GsonHelper.getAsInt(result, "count", 1);
		if (count < 1) {
			throw new JsonSyntaxException("Invalid output count: " + count);
		}
		return new ItemStack(item, count);
	}
}