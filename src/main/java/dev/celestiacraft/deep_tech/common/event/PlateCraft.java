//package dev.celestiacraft.deep_tech.common.event;
//
//import dev.celestiacraft.deep_tech.common.register.DTMaterials;
//import dev.celestiacraft.deep_tech.common.register.item.MaterialItems;
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.particles.ItemParticleOption;
//import net.minecraft.core.particles.ParticleTypes;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.sounds.SoundEvents;
//import net.minecraft.sounds.SoundSource;
//import net.minecraft.world.InteractionHand;
//import net.minecraft.world.entity.item.ItemEntity;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.Items;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.Blocks;
//import net.minecraft.world.level.block.state.BlockState;
//
//import java.util.Random;
//
//public class PlateCraft {
//	private static final Random RANDOM = new Random();
//
//	public static boolean process(Level level, BlockPos pos, Player player) {
//		BlockState state = level.getBlockState(pos);
//		if (!state.is(Blocks.REINFORCED_DEEPSLATE)) {
//			return false;
//		}
//		ItemStack held = player.getMainHandItem();
//		if (!held.is(Items.IRON_INGOT)) {
//			return false;
//		}
//
//		if (!level.isClientSide) {
//			// 1. 挥手动效
//			player.swing(InteractionHand.MAIN_HAND);
//
//			// 2. 铁锭粒子效果（仅在服务端发送，自动同步给客户端）
//			if (level instanceof ServerLevel serverLevel) {
//				ItemStack ironIngot = new ItemStack(Items.IRON_INGOT);
//				ItemParticleOption particle = new ItemParticleOption(ParticleTypes.ITEM, ironIngot);
//				for (int i = 0; i < 15; i++) {
//					double x = pos.getX() + 0.5 + (RANDOM.nextDouble() - 0.5) * 1.0;
//					double y = pos.getY() + 0.8 + (RANDOM.nextDouble() - 0.5) * 0.8;
//					double z = pos.getZ() + 0.5 + (RANDOM.nextDouble() - 0.5) * 1.0;
//					double speedX = (RANDOM.nextDouble() - 0.5) * 0.3;
//					double speedY = RANDOM.nextDouble() * 0.2 + 0.1;
//					double speedZ = (RANDOM.nextDouble() - 0.5) * 0.3;
//					serverLevel.sendParticles(particle, x, y, z, 1, speedX, speedY, speedZ, 1.0);
//				}
//			}
//
//			// 3. 10% 概率退化 + 掉落幽匿骨粉
//			if (RANDOM.nextFloat() < 0.10f) {
//				level.setBlockAndUpdate(pos, Blocks.DEEPSLATE.defaultBlockState());
//				spawnItem(level, pos, new ItemStack(MaterialItems.SCULK_BONEMEAL.get()));
//			}
//
//			// 4. 产物掉落（25%铁粒 / 25%铁粉 / 50%铁板）
//			float roll = RANDOM.nextFloat();
//			ItemStack result;
//			boolean isSuccess = false; // 是否得到铁板（成功）
//			if (roll < 0.25f) {
//				result = new ItemStack(Items.IRON_NUGGET, 8);
//				isSuccess = false;
//			} else if (roll < 0.50f) {
//				result = new ItemStack(DTMaterials.IRON.getDust().get());
//				isSuccess = false;
//			} else {
//				result = new ItemStack(DTMaterials.IRON.getPlate().get());
//				isSuccess = true;
//			}
//			spawnItem(level, pos, result);
//
//			// 5. 根据结果播放不同音效
//			if (isSuccess) {
//				level.playSound(null, pos, SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 1.0f, 1.0f);
//			} else {
//				level.playSound(null, pos, SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.0f, 1.0f);
//			}
//
//			// 6. 消耗铁锭（非创造模式）
//			if (!player.isCreative()) {
//				held.shrink(1);
//			}
//		}
//		return true;
//	}
//
//	private static void spawnItem(Level level, BlockPos pos, ItemStack stack) {
//		if (!stack.isEmpty()) {
//			ItemEntity item = new ItemEntity(
//					level,
//					pos.getX() + 0.5,
//					pos.getY() + 0.8,
//					pos.getZ() + 0.5,
//					stack
//			);
//			item.setDeltaMovement(
//					(RANDOM.nextDouble() - 0.5) * 0.2,
//					0.15,
//					(RANDOM.nextDouble() - 0.5) * 0.2
//			);
//			level.addFreshEntity(item);
//		}
//	}
//}