package dev.celestiacraft.deep_tech.common.block.machine.sculk_network.center;

import com.lowdragmc.lowdraglib.gui.factory.BlockEntityUIFactory;
import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import dev.celestiacraft.libs.api.interaction.context.UseContext;
import dev.celestiacraft.libs.api.register.block.BasicEntityBlock;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class SNCenterBlock extends BasicEntityBlock<SNCenterBlockEntity> {
	public SNCenterBlock(BlockBehaviour.Properties properties) {
		super(properties.sound(SoundType.NETHERITE_BLOCK)
				.strength(5.0F, 5.0F)
				.requiresCorrectToolForDrops());
	}

	@Override
	public BlockEntityType<SNCenterBlockEntity> getBlockEntityType() {
		return DTBlockEntities.SN_CENTER.get();
	}

	@Override
	public Class<SNCenterBlockEntity> getBlockEntityClass() {
		return SNCenterBlockEntity.class;
	}

	@Override
	public InteractionResult useOn(UseContext context) {
		if (context.getLevel().isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		// 桶点击时交给默认逻辑,避免吞掉流体交互
		if (context.getStack().getItem() instanceof BucketItem) {
			return super.useOn(context);
		}

		if (context.getLevel().getBlockEntity(context.getPos()) instanceof SNCenterBlockEntity center && context.getPlayer() instanceof ServerPlayer serverPlayer) {
			return BlockEntityUIFactory.INSTANCE.openUI(center, serverPlayer)
					? InteractionResult.CONSUME
					: InteractionResult.PASS;
		}
		return InteractionResult.PASS;
	}
}