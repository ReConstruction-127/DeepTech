package dev.celestiacraft.deep_tech.common.block.machine.collector;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * 未定义采集配方时的默认掉落: 模拟"最高挖掘等级 / 不带精准采集"的挖掘结果。
 * 依次尝试钻石镐/斧/锹/锄/剑(附魔时运 III, 无精准采集), 取第一个有掉落的工具结果。
 */
public class HarvestDropHelper {
	private HarvestDropHelper() {
	}

	public static List<ItemStack> defaultDrops(ServerLevel level, BlockPos pos, BlockState state) {
		ItemStack[] tools = {
				Items.DIAMOND_PICKAXE.getDefaultInstance(),
				Items.DIAMOND_AXE.getDefaultInstance(),
				Items.DIAMOND_SHOVEL.getDefaultInstance(),
				Items.DIAMOND_HOE.getDefaultInstance(),
				Items.DIAMOND_SWORD.getDefaultInstance()
		};
		for (ItemStack tool : tools) {
			tool.enchant(Enchantments.BLOCK_FORTUNE, 3);
			List<ItemStack> drops = Block.getDrops(state, level, pos, null, null, tool);
			if (!drops.isEmpty()) {
				return drops;
			}
		}
		return Block.getDrops(state, level, pos, null, null, ItemStack.EMPTY);
	}
}