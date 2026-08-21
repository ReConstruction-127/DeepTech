package dev.celestiacraft.deep_tech.common.block.machine.advanced.collector;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * 未定义采集配方时的默认掉落: 直接按方块战利品表产出, 模拟"最高挖掘等级 / 不带精准采集"。
 * 使用信物: 钻石镐(时运 III, 无精准), 所以玻璃/冰等需精准采集的方块天然不掉落(与原版一致)。
 */
public class HarvestDropHelper {
	private HarvestDropHelper() {
	}

	public static List<ItemStack> defaultDrops(ServerLevel level, BlockPos pos, BlockState state) {
		ItemStack tool = Items.DIAMOND_PICKAXE.getDefaultInstance();
		tool.enchant(Enchantments.BLOCK_FORTUNE, 3);

		LootTable table = level.getServer().getLootData().getLootTable(state.getBlock().getLootTable());
		if (table == LootTable.EMPTY) {
			return List.of();
		}

		LootParams.Builder params = new LootParams.Builder(level)
				.withParameter(LootContextParams.BLOCK_STATE, state)
				.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
				.withParameter(LootContextParams.TOOL, tool)
				.withOptionalParameter(LootContextParams.BLOCK_ENTITY, level.getBlockEntity(pos));

		return table.getRandomItems(params.create(LootContextParamSets.BLOCK));
	}
}