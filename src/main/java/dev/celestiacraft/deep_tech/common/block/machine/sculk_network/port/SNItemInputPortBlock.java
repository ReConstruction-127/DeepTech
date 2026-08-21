package dev.celestiacraft.deep_tech.common.block.machine.sculk_network.port;

import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import dev.celestiacraft.libs.api.interaction.context.UseContext;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class SNItemInputPortBlock extends SNPortBlock<SNItemInputPortBlockEntity> {
	public SNItemInputPortBlock(Properties properties) {
		super(properties.sound(SoundType.SCULK_CATALYST)
				.noOcclusion()
				.strength(5.0F, 5.0F)
				.requiresCorrectToolForDrops());
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

		// 过滤信息存在端口 BE 中,由中枢 BFS 读取
		if (!(level.getBlockEntity(context.getPos()) instanceof SNItemInputPortBlockEntity portBe)) {
			return InteractionResult.PASS;
		}

		Player player = context.getPlayer();

		// Shift 按住 防误触
		if (player.isShiftKeyDown()) {
			player.displayClientMessage(Component.literal("Hold shift to prevent accidental change"), true);
			return InteractionResult.SUCCESS;
		}

		ItemStack held = context.getStack();
		if (held.isEmpty()) {
			// 空手 清空过滤
			portBe.clearFilter();
			player.displayClientMessage(Component.literal("Filter cleared"), true);
		} else {
			// 手持物品 设置过滤
			ItemStack filter = held.copy();
			filter.setCount(1);
			portBe.setFilter(filter);
			player.displayClientMessage(Component.literal("Filter set: " + held.getHoverName()), true);
		}
		return InteractionResult.SUCCESS;
	}
}