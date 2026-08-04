package dev.celestiacraft.deep_tech.common.event;

import dev.celestiacraft.deep_tech.common.register.item.MaterialItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class SculkBoneRepair {
	private static final Random RANDOM = new Random();

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
			// 1. 替换为强化深板岩
			level.setBlockAndUpdate(pos, Blocks.REINFORCED_DEEPSLATE.defaultBlockState());

			// 2. 消耗一个 sculk_bone（非创造模式）
			if (!player.isCreative()) {
				held.shrink(1);
			}

			// 3. 播放深板岩破坏音效
			level.playSound(null, pos, SoundEvents.DEEPSLATE_BREAK, SoundSource.BLOCKS, 1.0f, 1.0f);

			// 4. 玩家挥手动画
			player.swing(InteractionHand.MAIN_HAND);

			// 5. 生成深板岩破坏粒子（仅服务端，自动广播给客户端）
			if (level instanceof ServerLevel serverLevel) {
				BlockState deepslate = Blocks.DEEPSLATE.defaultBlockState();
				BlockParticleOption particle = new BlockParticleOption(ParticleTypes.BLOCK, deepslate);
				for (int i = 0; i < 20; i++) {
					double x = pos.getX() + 0.5 + (RANDOM.nextDouble() - 0.5) * 1.2;
					double y = pos.getY() + 0.5 + (RANDOM.nextDouble() - 0.5) * 1.2;
					double z = pos.getZ() + 0.5 + (RANDOM.nextDouble() - 0.5) * 1.2;
					double speedX = (RANDOM.nextDouble() - 0.5) * 0.5;
					double speedY = RANDOM.nextDouble() * 0.5;
					double speedZ = (RANDOM.nextDouble() - 0.5) * 0.5;
					serverLevel.sendParticles(particle, x, y, z, 0, speedX, speedY, speedZ, 1.0);
				}
			}
		}
		return true;
	}
}