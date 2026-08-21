package dev.celestiacraft.deep_tech.common.recipe.harvest;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * 采集配方的输入:单个方块或方块标签, 数据驱动匹配世界中的方块状态。
 */
public class HarvestInput {
	@Nullable
	private final Block block;
	@Nullable
	private final TagKey<Block> blockTag;

	private HarvestInput(@Nullable Block block, @Nullable TagKey<Block> blockTag) {
		this.block = block;
		this.blockTag = blockTag;
	}

	public static HarvestInput of(Block block) {
		return new HarvestInput(block, null);
	}

	public static HarvestInput ofTag(TagKey<Block> blockTag) {
		return new HarvestInput(null, blockTag);
	}

	public static HarvestInput ofTagId(ResourceLocation blockTag) {
		return new HarvestInput(null, BlockTags.create(blockTag));
	}

	/** 判断一个方块状态是否匹配本输入 */
	public boolean matches(BlockState state, Level level) {
		if (block != null) {
			return state.is(block);
		}
		return blockTag != null && state.is(blockTag);
	}

	public boolean isEmpty() {
		return block == null && blockTag == null;
	}

	@Nullable
	public Block getBlock() {
		return block;
	}

	@Nullable
	public TagKey<Block> getBlockTag() {
		return blockTag;
	}

	/** 用于 JEI 显示: 方块输入转为物品列表 */
	public Ingredient toJeiIngredient(Level level) {
		if (level == null) {
			return Ingredient.EMPTY;
		}

		RegistryAccess access = level.registryAccess();

		if (block != null) {
			return Ingredient.of(block);
		}
		if (blockTag != null) {
			List<ItemStack> stacks = new ArrayList<>();
			access.registryOrThrow(Registries.BLOCK).getTag(blockTag)
					.ifPresent((holders) -> {
						holders.forEach((holder) -> {
							stacks.add(new ItemStack(holder.value()));
						});
					});
			return stacks.isEmpty() ? Ingredient.EMPTY : Ingredient.of(stacks.toArray(new ItemStack[0]));
		}
		return Ingredient.EMPTY;
	}

	/** 用于 JEI 显示: 所有可能产出的堆叠 */
	public static List<ItemStack> toJeiOutputs(List<HarvestOutput> results) {
		List<ItemStack> stacks = new ArrayList<>();
		for (HarvestOutput result : results) {
			stacks.add(result.getStack().copy());
		}
		return stacks;
	}

	/** 用于网络/JSON 序列化: 输入类型标签 */
	public boolean isTag() {
		return blockTag != null;
	}

	public String serializeId() {
		if (block != null) {
			return ForgeRegistries.BLOCKS.getKey(block).toString();
		}
		return blockTag != null ? blockTag.location().toString() : "";
	}
}