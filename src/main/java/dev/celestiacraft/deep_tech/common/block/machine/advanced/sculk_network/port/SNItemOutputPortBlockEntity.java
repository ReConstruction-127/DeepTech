package dev.celestiacraft.deep_tech.common.block.machine.advanced.sculk_network.port;

import dev.celestiacraft.libs.api.register.block.BasicBlockEntity;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public class SNItemOutputPortBlockEntity extends BasicBlockEntity {
	// 目标容器位置(可配置, 初版使用方块面向方向)
	private @Nullable BlockPos targetPos = null;
	@Getter
	private ItemStack filter = ItemStack.EMPTY;

	public SNItemOutputPortBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// 获取目标容器位置(根据朝向自动计算,朝向实时读取自方块状态)
	public BlockPos getTargetPos() {
		if (targetPos == null && level != null) {
			BlockState state = getState();
			if (state.hasProperty(BlockStateProperties.FACING)) {
				return worldPosition.relative(state.getValue(BlockStateProperties.FACING));
			}
			return null;
		}
		return targetPos;
	}

	public void setFilter(ItemStack filter) {
		this.filter = filter.copy();
		this.filter.setCount(1);
		markDirtyAndUpdate();
	}

	public void clearFilter() {
		filter = ItemStack.EMPTY;
		markDirtyAndUpdate();
	}

	public void setTargetPos(BlockPos targetPos) {
		this.targetPos = targetPos;
		markDirty();
	}

	// 获取目标容器的 IItemHandler(用于输出)
	public LazyOptional<IItemHandler> getTargetItemHandler() {
		if (level == null) return LazyOptional.empty();
		BlockPos pos = getTargetPos();
		if (pos == null) return LazyOptional.empty();
		BlockEntity be = level.getBlockEntity(pos);
		if (be == null) return LazyOptional.empty();
		BlockState state = getState();
		if (!state.hasProperty(BlockStateProperties.FACING)) return LazyOptional.empty();
		Direction side = state.getValue(BlockStateProperties.FACING).getOpposite();
		return be.getCapability(ForgeCapabilities.ITEM_HANDLER, side);
	}

	// ========== NBT(持久化,朝向由方块状态持有,无需存储) ==========
	@Override
	protected void write(CompoundTag tag) {
		if (targetPos != null) {
			tag.putLong("TargetPos", targetPos.asLong());
		}
		if (!filter.isEmpty()) {
			tag.put("Filter", filter.save(new CompoundTag()));
		}
	}

	@Override
	protected void read(CompoundTag tag) {
		targetPos = tag.contains("TargetPos") ? BlockPos.of(tag.getLong("TargetPos")) : null;
		filter = tag.contains("Filter") ? ItemStack.of(tag.getCompound("Filter")) : ItemStack.EMPTY;
	}

	// ========== 同步(总是写入 Filter,即使为空) ==========
	@Override
	protected void writeSync(CompoundTag tag) {
		tag.put("Filter", filter.save(new CompoundTag()));
	}

	@Override
	protected void readSync(CompoundTag tag) {
		filter = tag.contains("Filter") ? ItemStack.of(tag.getCompound("Filter")) : ItemStack.EMPTY;
	}
}