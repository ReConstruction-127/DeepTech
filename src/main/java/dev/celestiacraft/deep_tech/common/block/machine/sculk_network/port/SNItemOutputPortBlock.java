package dev.celestiacraft.deep_tech.common.block.machine.sculk_network.port;

import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import dev.celestiacraft.libs.api.register.block.IEntityBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class SNItemOutputPortBlock extends SNPortBlock implements IEntityBlock<SNItemOutputPortBlockEntity> {
	public SNItemOutputPortBlock(Properties properties) {
		super(properties.noOcclusion());
	}

	@Override
	public BlockEntityType<SNItemOutputPortBlockEntity> getBlockEntityType() {
		return DTBlockEntities.SN_ITEM_OUTPUT_PORT.get();
	}

	@Override
	public Class<SNItemOutputPortBlockEntity> getBlockEntityClass() {
		return SNItemOutputPortBlockEntity.class;
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos,
	                             Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.isClientSide) return InteractionResult.SUCCESS;

		if (!(level.getBlockEntity(pos) instanceof SNItemOutputPortBlockEntity portBe)) {
			return InteractionResult.PASS;
		}

		if (player.isShiftKeyDown()) {
			// 按住Shift防误触，可以不加提示，直接返回
			return InteractionResult.SUCCESS;
		}

		ItemStack held = player.getItemInHand(hand);
		if (held.isEmpty()) {
			portBe.clearFilter();
			player.displayClientMessage(Component.literal("Filter cleared"), true);
		} else {
			ItemStack filter = held.copy();
			filter.setCount(1);
			portBe.setFilter(filter);
			player.displayClientMessage(Component.literal("Filter set: " + held.getHoverName()), true);
		}
		return InteractionResult.SUCCESS;
	}
}