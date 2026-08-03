package dev.celestiacraft.deep_tech.common.event;

import dev.celestiacraft.deep_tech.common.register.item.MaterialItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SculkBoneRepair {

	public static boolean process(Level level, BlockPos pos, Player player) {
		BlockState state = level.getBlockState(pos);
		// 必须是普通深板岩
		if (!state.is(Blocks.DEEPSLATE)) {
			return false;
		}

		ItemStack held = player.getMainHandItem();
		// 必须是 sculk_bone
		if (!held.is(MaterialItems.SCULK_BONE.get())) {
			return false;
		}

		if (!level.isClientSide) {
			// 替换为强化深板岩
			level.setBlockAndUpdate(pos, Blocks.REINFORCED_DEEPSLATE.defaultBlockState());
			// 消耗一个 sculk_bone（非创造模式才消耗）
			if (!player.isCreative()) {
				held.shrink(1);
			}
			// 可选：播放音效或粒子，增加反馈
			// level.playSound(null, pos, SoundEvents.SCULK_BLOCK_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);
		}
		return true;
	}
}