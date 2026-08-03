package dev.celestiacraft.deep_tech.common;

import dev.celestiacraft.deep_tech.common.register.DTMaterials;
import dev.celestiacraft.deep_tech.common.register.item.MaterialItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class PlateCraft {
	private static final Random RANDOM = new Random();

	public static boolean process(Level level, BlockPos pos, Player player) {
		BlockState state = level.getBlockState(pos);
		if (!state.is(Blocks.REINFORCED_DEEPSLATE)) {
			return false;
		}
		ItemStack held = player.getMainHandItem();
		if (!held.is(Items.IRON_INGOT)) {
			return false;
		}

		if (!level.isClientSide) {
			// 10% 概率退化 + 掉落幽匿骨粉
			if (RANDOM.nextFloat() < 0.10f) {
				level.setBlockAndUpdate(pos, Blocks.DEEPSLATE.defaultBlockState());
				spawnItem(level, pos, new ItemStack(MaterialItems.SCULK_BONEMEAL.get()));
			}

			// 产物掉落（25%铁粒 / 25%铁粉 / 50%铁板）
			float roll = RANDOM.nextFloat();
			ItemStack result;
			if (roll < 0.25f) {
				result = new ItemStack(Items.IRON_NUGGET, 8);
			} else if (roll < 0.50f) {
				result = new ItemStack(DTMaterials.IRON.getDust().get());
			} else {
				result = new ItemStack(DTMaterials.IRON.getPlate().get());
			}
			spawnItem(level, pos, result);

			if (!player.isCreative()) {
				held.shrink(1);
			}
		}
		return true;

	}

	private static void spawnItem(Level level, BlockPos pos, ItemStack stack) {
		if (!stack.isEmpty()) {
			ItemEntity item = new ItemEntity(
					level,
					pos.getX() + 0.5,
					pos.getY() + 0.8,
					pos.getZ() + 0.5,
					stack
			);
			// 小随机速度让掉落物更生动
			item.setDeltaMovement(
					(RANDOM.nextDouble() - 0.5) * 0.2,
					0.15,
					(RANDOM.nextDouble() - 0.5) * 0.2
			);
			level.addFreshEntity(item);
		}
	}
}