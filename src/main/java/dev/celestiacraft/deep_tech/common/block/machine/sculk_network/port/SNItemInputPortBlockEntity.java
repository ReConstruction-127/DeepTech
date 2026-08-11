package dev.celestiacraft.deep_tech.common.block.machine.sculk_network.port;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * 物品输入端口本身不执行任何逻辑。
 * 网络扫描与物品转运全部由中枢（{@link dev.celestiacraft.deep_tech.common.block.machine.sculk_network.center.SNCenterBlockEntity}）的 BFS 承载。
 */
public class SNItemInputPortBlockEntity extends BlockEntity {
	public SNItemInputPortBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
	private ItemStack filter = ItemStack.EMPTY;
	public ItemStack getFilter() {
		return filter;
	}

	public void setFilter(ItemStack filter) {
		this.filter = filter.copy();
		this.filter.setCount(1);
		setChanged();
		sync();
	}

	public void clearFilter() {
		this.filter = ItemStack.EMPTY;
		setChanged();
		sync();
	}

	// ========== NBT ==========
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		if (!filter.isEmpty()) {
			tag.put("Filter", filter.save(new CompoundTag()));
		}
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if (tag.contains("Filter")) {
			filter = ItemStack.of(tag.getCompound("Filter"));
		} else {
			filter = ItemStack.EMPTY;
		}
	}

	// ========== 同步 ==========
	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = super.getUpdateTag();
		// 总是写入 Filter，即使为空也写一个空 CompoundTag
		tag.put("Filter", filter.save(new CompoundTag()));  // 重点
		return tag;
	}

	@Override
	public void handleUpdateTag(CompoundTag tag) {
		super.handleUpdateTag(tag);
		if (tag.contains("Filter")) {
			filter = ItemStack.of(tag.getCompound("Filter"));
		} else {
			filter = ItemStack.EMPTY;
		}
	}

	@Nullable
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		if (pkt.getTag() != null) {
			handleUpdateTag(pkt.getTag());
		}
	}

	private void sync() {
		if (level != null && !level.isClientSide) {
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
		}
	}
}
