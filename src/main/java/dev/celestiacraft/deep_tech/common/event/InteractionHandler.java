package dev.celestiacraft.deep_tech.common.event;

import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.common.recipe.interaction.InteractionRecipe;
import dev.celestiacraft.deep_tech.common.register.DTRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;
import java.util.Random;

public class InteractionHandler {
	private static final Random RANDOM = new Random();

	public static boolean process(Level level, BlockPos pos, Player player) {
		if (level.isClientSide) return false;

		ItemStack held = player.getMainHandItem();
		BlockState state = level.getBlockState(pos);

		// 查找匹配的配方
		Optional<InteractionRecipe> recipe = level.getRecipeManager()
				.getAllRecipesFor(DTRecipes.INTERACTION.getRecipeType())
				.stream()
				.filter(r -> r.matches(held, state))
				.findFirst();

		if (recipe.isEmpty()) {
			DeepTech.LOGGER.debug("No interaction recipe found for {} on {}", held.getItem(), state.getBlock());
			return false;
		}

		InteractionRecipe r = recipe.get();
		DeepTech.LOGGER.info("Executing interaction recipe: {}", r.getId());

		// 挥手动效
		player.swing(InteractionHand.MAIN_HAND);

		// 额外效果（方块转化 + 额外掉落）
		if (r.getExtraEffect() != null && RANDOM.nextFloat() < r.getExtraEffect().chance) {
			BlockState toState = r.getExtraEffect().toState;
			if (toState != null && !toState.isAir()) {
				level.setBlockAndUpdate(pos, toState);
				DeepTech.LOGGER.debug("Block transformed to {}", toState.getBlock());
			}
			for (ItemStack drop : r.getExtraEffect().extraDrops) {
				spawnItem(level, pos, drop.copy());
				DeepTech.LOGGER.debug("Extra drop: {}", drop.getItem());
			}
		}

		// 主产物
		ItemStack result = r.getRandomResult(RANDOM);
		if (!result.isEmpty()) {
			spawnItem(level, pos, result);
			DeepTech.LOGGER.debug("Main result: {} x {}", result.getItem(), result.getCount());
		}

		// 消耗触发物品
		if (r.isConsumeTrigger() && !player.isCreative()) {
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