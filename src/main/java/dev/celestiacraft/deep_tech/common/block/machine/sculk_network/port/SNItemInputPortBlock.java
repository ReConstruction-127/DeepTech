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

public class SNItemInputPortBlock extends SNPortBlock implements IEntityBlock<SNItemInputPortBlockEntity> {
	public SNItemInputPortBlock(Properties properties) {
		super(properties.noOcclusion());
	}

	@Override
	public BlockEntityType<SNItemInputPortBlockEntity> getBlockEntityType() {
		return DTBlockEntities.SN_ITEM_INPUT_PORT.get();
	}

	@Override
	public Class<SNItemInputPortBlockEntity> getBlockEntityClass() {
		return SNItemInputPortBlockEntity.class;
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos,
	                             Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.isClientSide) return InteractionResult.SUCCESS;

		// 过滤信息存在端口 BE 中,由中枢 BFS 读取
		if (!(level.getBlockEntity(pos) instanceof SNItemInputPortBlockEntity portBe)) {
			return InteractionResult.PASS;
		}

		// Shift 按住 → 防误触
		if (player.isShiftKeyDown()) {
			player.displayClientMessage(Component.literal("Hold shift to prevent accidental change"), true);
			return InteractionResult.SUCCESS;
		}

		ItemStack held = player.getItemInHand(hand);
		if (held.isEmpty()) {
			// 空手 → 清空过滤
			portBe.clearFilter();
			player.displayClientMessage(Component.literal("Filter cleared"), true);
		} else {
			// 手持物品 → 设置过滤
			ItemStack filter = held.copy();
			filter.setCount(1);
			portBe.setFilter(filter);
			player.displayClientMessage(Component.literal("Filter set: " + held.getHoverName()), true);
		}
		return InteractionResult.SUCCESS;
	}
}