package dev.celestiacraft.deep_tech.event;

import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.common.register.item.MaterialItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = DeepTech.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SculkShearHandler {
	private static final Random RANDOM = new SecureRandom();

	@SubscribeEvent
	public static void onBlockBreak(BlockEvent.BreakEvent event) {
		BlockState state = event.getState();
		Player player = event.getPlayer();
		Level level = (Level) event.getLevel();
		BlockPos pos = event.getPos();
		ItemStack stack = player.getMainHandItem();

		if (!isSculkBlock(state)) {
			return;
		}

		if (!stack.is(Tags.Items.SHEARS)) {
			return;
		}

		event.setCanceled(true);

		level.destroyBlock(pos, false);

		if (state.is(Blocks.SCULK)) {
			// 幽匿块 → 1-4 个幽匿碎块
			int count = RANDOM.nextInt(4) + 1;
			spawnItem(level, pos, new ItemStack(MaterialItems.SCULK_CHUNK.get(), count));
		} else if (state.is(Blocks.SCULK_CATALYST) || state.is(Blocks.SCULK_SHRIEKER)) {
			// 催发体 / 尖啸体 → 1-4 个幽匿碎块 + 1 个幽匿之骨
			int count = RANDOM.nextInt(4) + 1;
			spawnItem(level, pos, new ItemStack(MaterialItems.SCULK_CHUNK.get(), count));
			spawnItem(level, pos, new ItemStack(MaterialItems.SCULK_BONE.get(), 4));
		} else if (state.is(Blocks.SCULK_SENSOR)) {
			// 传感器 → 1-4 个幽匿碎块 + 1 个红石
			int count = RANDOM.nextInt(4) + 1;
			spawnItem(level, pos, new ItemStack(MaterialItems.SCULK_CHUNK.get(), count));
			spawnItem(level, pos, new ItemStack(Items.REDSTONE));
		} else if (state.is(Blocks.SCULK_VEIN)) {
			// 幽匿脉络 → 掉落自身
			spawnItem(level, pos, new ItemStack(Blocks.SCULK_VEIN));
		}

		stack.hurtAndBreak(1, player, (entity) -> {
			entity.broadcastBreakEvent(player.getUsedItemHand());
		});

		DeepTech.LOGGER.debug("Sheared {} at {}, dropped items", state.getBlock(), pos);
	}

	@SubscribeEvent
	public static void onBlockMining(PlayerEvent.BreakSpeed event) {
		BlockState state = event.getState();
		Player player = event.getEntity();
		ItemStack stack = player.getMainHandItem();

		if (!isSculkBlock(state)) {
			return;
		}

		// 挖掘速度提升10倍
		if (isSculkBlock(state) && (stack.is(Tags.Items.SHEARS))) {
			event.setNewSpeed(event.getOriginalSpeed() * 10.0F);
		}
	}

	private static boolean isSculkBlock(BlockState state) {
		List<Block> sculks = List.of(
				Blocks.SCULK,
				Blocks.SCULK_CATALYST,
				Blocks.SCULK_SHRIEKER,
				Blocks.SCULK_SENSOR,
				Blocks.SCULK_VEIN
		);

		return sculks.stream().anyMatch(state::is);
	}

	private static void spawnItem(Level level, BlockPos pos, ItemStack stack) {
		if (!stack.isEmpty()) {
			ItemEntity item = new ItemEntity(
					level,
					pos.getX() + 0.5,
					pos.getY() + 0.5,
					pos.getZ() + 0.5,
					stack
			);
			level.addFreshEntity(item);
		}
	}
}