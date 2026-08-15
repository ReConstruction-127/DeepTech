package dev.celestiacraft.deep_tech.common.effect;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * 感染:玩家走过的脚下方块化为幽匿块轨迹(鸟瞰为 3 列:中间轨迹,两侧幽匿脉络),
 * 幽匿脉络铺在玩家脚同一层的左右两格(垂直面朝方向),仅空气格放置;
 * 玩家浮空时不进行任何转换;每 20 tick 损失经验值(若为玩家)并扣血。
 * 被此效果杀死的生物由 {@link InfectionEvents} 放置幽匿催生体。
 */
public class InfectionEffect extends MobEffect {

	private static final String AGE_KEY = "dt_infection_age";
	private static final DustParticleOptions VEIN_DUST = new DustParticleOptions(new org.joml.Vector3f(0.0f, 0.9f, 1.0f), 1.0f);

	public InfectionEffect() {
		super(MobEffectCategory.HARMFUL, 0x1E4A45);
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		// 每 2 tick 铺一段轨迹(保持连续),扣血/经验每 20 tick
		return duration % 2 == 0;
	}

	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		if (entity.level().isClientSide) {
			return;
		}
		Level level = entity.level();
		if (!(level instanceof ServerLevel serverLevel)) {
			return;
		}

		// 1. 脚下轨迹:脚下方块转幽匿块,脚层左右两格铺幽匿脉络
		if (entity.onGround()) {
			BlockPos feet = entity.blockPosition().below();
			convertToSculk(serverLevel, feet);

			Direction left = Direction.fromYRot(entity.getYRot() + 90.0f);
			Direction right = Direction.fromYRot(entity.getYRot() - 90.0f);
			BlockPos footLayer = feet.above();
			placeVein(serverLevel, footLayer.relative(left));
			placeVein(serverLevel, footLayer.relative(right));
		}

		// 2. 每 20 tick 持续损失经验值(若为玩家)并扣血,伤害与损耗随等级提升
		int age = entity.getPersistentData().getInt(AGE_KEY) + 1;
		entity.getPersistentData().putInt(AGE_KEY, age);
		if (age % 10 == 0) {
			if (entity instanceof Player player) {
				player.giveExperiencePoints(-(2 + amplifier * 2));
			}
			entity.hurt(entity.damageSources().magic(), 1.0f + amplifier);
		}
	}

	/** 若该位置为可感染的实体方块(可破坏、非幽匿系),则转化为幽匿块 */
	private static void convertToSculk(ServerLevel level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		if (state.isAir() || state.getFluidState().isSource() || isSculkLike(state.getBlock())) {
			return;
		}
		// 跳过挖掘等级为 -1 的方块(基岩等不可破坏方块)
		if (state.getDestroySpeed(level, pos) < 0.0f) {
			return;
		}
		level.setBlock(pos, Blocks.SCULK.defaultBlockState(), 3);
		spawnDust(level, pos);
	}

	/** 在玩家脚同一层的空气格放置幽匿脉络(贴地单面);非空气或下方悬空时不做处理 */
	private static void placeVein(ServerLevel level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		if (!state.isAir() || level.getBlockState(pos.below()).isAir()) {
			return;
		}
		level.setBlock(pos, Blocks.SCULK_VEIN.defaultBlockState()
				.setValue(BlockStateProperties.DOWN, true), 3);
		spawnDust(level, pos);
	}

	private static void spawnDust(ServerLevel level, BlockPos pos) {
		level.sendParticles(VEIN_DUST,
				pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5,
				1, 0.1, 0.1, 0.1, 0.0);
	}

	public static boolean isSculkLike(Block block) {
		return block == Blocks.SCULK
				|| block == Blocks.SCULK_VEIN
				|| block == Blocks.SCULK_CATALYST
				|| block == Blocks.SCULK_SENSOR
				|| block == Blocks.SCULK_SHRIEKER;
	}
}