package dev.celestiacraft.deep_tech.common.block.machine.sculk_network.accessor;

import com.lowdragmc.lowdraglib.gui.factory.BlockEntityUIFactory;
import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import dev.celestiacraft.libs.api.interaction.context.UseContext;
import dev.celestiacraft.libs.api.register.block.BasicEntityBlock;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class SNAccessorBlock extends BasicEntityBlock<SNAccessorBlockEntity> {
	public SNAccessorBlock(BlockBehaviour.Properties properties) {
		super(properties.sound(SoundType.DEEPSLATE_BRICKS)
				.strength(5.0F, 5.0F)
				.requiresCorrectToolForDrops());
	}

	@Override
	public BlockEntityType<SNAccessorBlockEntity> getBlockEntityType() {
		return DTBlockEntities.SN_ACCESSOR.get();
	}

	@Override
	public Class<SNAccessorBlockEntity> getBlockEntityClass() {
		return SNAccessorBlockEntity.class;
	}

	@Override
	public InteractionResult useOn(UseContext context) {
		Level level = context.getLevel();
		ItemStack stack = context.getStack();

		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		// 桶点击时交给默认逻辑,避免吞掉流体交互
		if (stack.getItem() instanceof BucketItem) {
			return super.useOn(context);
		}

		if (level.getBlockEntity(context.getPos()) instanceof SNAccessorBlockEntity accessor && context.getPlayer() instanceof ServerPlayer serverPlayer) {
			return BlockEntityUIFactory.INSTANCE.openUI(accessor, serverPlayer)
					? InteractionResult.CONSUME
					: InteractionResult.PASS;
		}
		return InteractionResult.PASS;
	}
}