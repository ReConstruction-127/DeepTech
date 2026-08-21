package dev.celestiacraft.deep_tech.common.fluid;

import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.common.effect.InfectionEffect;
import dev.celestiacraft.deep_tech.common.register.DTEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * 幽匿培养液行为:液体每 tick 检测所在格内的实体并给予「感染」效果;
 * 液体流经的位置会将下方实体方块转化为幽匿块。
 */
public class SculkCultureFluid {

	private SculkCultureFluid() {
	}

	public static void tickFluid(Level level, BlockPos pos) {
		if (level.isClientSide()) {
			return;
		}
		List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, new AABB(pos));
		for (LivingEntity entity : entities) {
			entity.addEffect(new MobEffectInstance(DTEffects.INFECTION.get(), 1200, 0, false, false));
			if (level.getGameTime() % 100 == 0) {
				DeepTech.LOGGER.info("[CultureFluid] infected {} @ {}", entity, pos);
			}
		}
		// 液体流经:将液体所在位置下方的实体方块转化为幽匿块
		convertGround(level, pos);
		if (level.getGameTime() % 100 == 0) {
			DeepTech.LOGGER.info("[CultureFluid] ticking @ {} entities={} gameTime={}", pos, entities.size(), level.getGameTime());
		}
	}

	private static void convertGround(Level level, BlockPos pos) {
		BlockPos below = pos.below();
		BlockState state = level.getBlockState(below);
		boolean air = state.isAir();
		boolean fluidSource = state.getFluidState().isSource();
		boolean sculkLike = InfectionEffect.isSculkLike(state.getBlock());
		boolean unbreakable = state.getDestroySpeed(level, below) < 0.0f;
		if (air || fluidSource || sculkLike || unbreakable) {
			if (level.getGameTime() % 100 == 0) {
				DeepTech.LOGGER.info("[CultureFluid] skip convert @ {} below={} belowState={} air={} fluidSource={} sculkLike={} unbreakable={}",
						pos, below, state.getBlock(), air, fluidSource, sculkLike, unbreakable);
			}
			return;
		}
		level.setBlock(below, Blocks.SCULK.defaultBlockState(), 3);
		if (level.getGameTime() % 100 == 0) {
			DeepTech.LOGGER.info("[CultureFluid] converted {} -> sculk @ {}", state.getBlock(), below);
		}
	}
}