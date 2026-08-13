package dev.celestiacraft.deep_tech.common.block.machine.sculk_network.accessor;

import com.lowdragmc.lowdraglib.gui.factory.BlockEntityUIFactory;
import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import dev.celestiacraft.libs.api.register.block.BasicEntityBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class SNAccessorBlock extends BasicEntityBlock<SNAccessorBlockEntity> {
	public SNAccessorBlock(BlockBehaviour.Properties properties) {
		super(properties);
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
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		// 桶点击时交给默认逻辑,避免吞掉流体交互
		ItemStack stack = player.getMainHandItem();
		if (stack.getItem() instanceof BucketItem) {
			return super.use(state, level, pos, player, hand, hit);
		}

		if (level.getBlockEntity(pos) instanceof SNAccessorBlockEntity accessor && player instanceof ServerPlayer serverPlayer) {
			return BlockEntityUIFactory.INSTANCE.openUI(accessor, serverPlayer)
					? InteractionResult.CONSUME
					: InteractionResult.PASS;
		}
		return InteractionResult.PASS;
	}
}