package dev.celestiacraft.deep_tech.event;

import dev.celestiacraft.deep_tech.common.recipe.interaction.InteractionRecipe;
import dev.celestiacraft.deep_tech.common.register.DTRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class InteractionHandler {
	private static final Random RANDOM = new SecureRandom();

	public static boolean process(Level level, BlockPos pos, Player player, boolean isRightClick) {
		if (level.isClientSide()) {
			return false;
		}

		ItemStack held = player.getMainHandItem();
		BlockState state = level.getBlockState(pos);

		Optional<InteractionRecipe> manager = level.getRecipeManager()
				.getAllRecipesFor(DTRecipes.INTERACTION.getRecipeType())
				.stream()
				.filter((recipe) -> {
					return recipe.matches(held, state, isRightClick);
				})
				.findFirst();

		if (manager.isEmpty()) {
			return false;
		}

		InteractionRecipe recipe = manager.get();

		player.swing(InteractionHand.MAIN_HAND);

		if (level instanceof ServerLevel serverLevel) {
			ItemParticleOption particle = new ItemParticleOption(ParticleTypes.ITEM, held.copy());
			for (int i = 0; i < 15; i++) {
				double x = pos.getX() + 0.5 + (RANDOM.nextDouble() - 0.5);
				double y = pos.getY() + 0.8 + (RANDOM.nextDouble() - 0.5) * 0.8;
				double z = pos.getZ() + 0.5 + (RANDOM.nextDouble() - 0.5);
				double speedX = (RANDOM.nextDouble() - 0.5) * 0.3;
				double speedY = RANDOM.nextDouble() * 0.2 + 0.1;
				double speedZ = (RANDOM.nextDouble() - 0.5) * 0.3;
				serverLevel.sendParticles(particle, x, y, z, 1, speedX, speedY, speedZ, 1.0);
			}
		}

		if (recipe.getExtraEffect() != null && RANDOM.nextFloat() < recipe.getExtraEffect().getChance()) {
			BlockState toState = recipe.getExtraEffect().getState();
			if (toState != null && !toState.isAir()) {
				if (level instanceof ServerLevel serverLevel) {
					BlockParticleOption breakParticle = new BlockParticleOption(ParticleTypes.BLOCK, state);
					for (int i = 0; i < 20; i++) {
						double x = pos.getX() + 0.5 + (RANDOM.nextDouble() - 0.5) * 1.2;
						double y = pos.getY() + 0.5 + (RANDOM.nextDouble() - 0.5) * 1.2;
						double z = pos.getZ() + 0.5 + (RANDOM.nextDouble() - 0.5) * 1.2;
						double speedX = (RANDOM.nextDouble() - 0.5) * 0.5;
						double speedY = RANDOM.nextDouble() * 0.5;
						double speedZ = (RANDOM.nextDouble() - 0.5) * 0.5;
						serverLevel.sendParticles(breakParticle, x, y, z, 1, speedX, speedY, speedZ, 1.0);
					}
					level.playSound(null, pos, state.getSoundType().getBreakSound(), SoundSource.BLOCKS, 1.0f, 1.0f);
				}
				level.setBlockAndUpdate(pos, toState);
			}
			for (ItemStack drop : recipe.getExtraEffect().getExtraDrops()) {
				spawnItem(level, pos, drop.copy());
			}
		}

		List<ItemStack> results = recipe.getTriggeredResults(RANDOM);
		for (ItemStack result : results) {
			if (!result.isEmpty()) spawnItem(level, pos, result);
		}

		level.playSound(
				null,
				pos,
				SoundEvents.ITEM_BREAK,
				SoundSource.PLAYERS,
				1.0f,
				1.0f
		);

		if (recipe.isConsumeTrigger() && !player.isCreative()) {
			held.shrink(1);
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
			item.setDeltaMovement(
					(RANDOM.nextDouble() - 0.5) * 0.2,
					0.15,
					(RANDOM.nextDouble() - 0.5) * 0.2
			);
			level.addFreshEntity(item);
		}
	}
}