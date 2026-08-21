package dev.celestiacraft.deep_tech.common.block.machine.advanced.sculk_network.port;

import dev.celestiacraft.deep_tech.api.block.SNPortBlock;
import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import dev.celestiacraft.libs.api.interaction.context.UseContext;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class SNItemOutputPortBlock extends SNPortBlock<SNItemOutputPortBlockEntity> {
	public SNItemOutputPortBlock(Properties properties) {
		super(properties);
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
	public InteractionResult useOn(UseContext context) {
		// 桶/流体交互优先(未启用时默认直接 PASS)
		InteractionResult fluid = tryFluidInteraction(context.getPlayer(), context.getHand(), context.getLevel(), context.getPos(), context.getResult());
		if (fluid.consumesAction()) {
			return fluid;
		}

		Level level = context.getLevel();
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		if (!(level.getBlockEntity(context.getPos()) instanceof SNItemOutputPortBlockEntity portBe)) {
			return InteractionResult.PASS;
		}

		Player player = context.getPlayer();

		if (player.isShiftKeyDown()) {
			// 按住Shift防误触, 可以不加提示, 直接返回
			return InteractionResult.SUCCESS;
		}

		ItemStack held = context.getStack();
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