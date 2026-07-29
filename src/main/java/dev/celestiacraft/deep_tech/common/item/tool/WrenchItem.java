package dev.celestiacraft.deep_tech.common.item.tool;

import dev.celestiacraft.deep_tech.tags.DeepTechBlockTags;
import dev.celestiacraft.deep_tech.tags.DeepTechItemTags;
import dev.celestiacraft.libs.api.register.item.BasicItem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WrenchItem extends BasicItem {
	public WrenchItem(Properties properties) {
		super(properties);
	}

	@Override
	public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
		Level level = context.getLevel();
		Player player = context.getPlayer();
		InteractionHand hand = context.getHand();
		ItemStack stack = context.getItemInHand();
		BlockPos pos = context.getClickedPos();
		BlockState state = level.getBlockState(pos);

		boolean triggered = player != null
				&& player.isShiftKeyDown()
				&& isTrigger(state, stack, hand);

		if (!triggered) {
			return InteractionResult.PASS;
		}

		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		ServerLevel serverLevel = (ServerLevel) level;
		BlockEntity entity = level.getBlockEntity(pos);
		List<ItemStack> drops = Block.getDrops(state, serverLevel, pos, entity, player, stack);
		level.destroyBlock(pos, false, player);

		boolean pickedUp = false;
		for (ItemStack drop : drops) {
			int count = drop.getCount();
			player.getInventory().add(drop);
			if (drop.getCount() < count) {
				pickedUp = true;
			}
			if (!drop.isEmpty()) {
				Block.popResource(level, pos, drop);
			}
		}

		if (pickedUp) {
			level.playSound(
					null,
					player.getX(),
					player.getY(),
					player.getZ(),
					SoundEvents.ITEM_PICKUP,
					SoundSource.PLAYERS,
					0.2F,
					((level.random.nextFloat() - level.random.nextFloat()) * 0.7F + 1.0F) * 2.0F
			);
		}

		return InteractionResult.SUCCESS;
	}

	private boolean isTrigger(BlockState state, ItemStack stack, InteractionHand hand) {
		return state.is(DeepTechBlockTags.WRENCH_PICKUP)
				&& hand.equals(InteractionHand.MAIN_HAND)
				&& stack.is(DeepTechItemTags.WRENCH);
	}
}