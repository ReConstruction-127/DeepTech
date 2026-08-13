package dev.celestiacraft.deep_tech.common.block.machine.sculk_network.port;

import dev.celestiacraft.libs.api.register.block.BasicBlockEntity;
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

public class SNItemOutputPortBlockEntity extends BasicBlockEntity {
	// 目标容器位置（可配置，初版使用方块面向方向）
	private BlockPos targetPos = null;
	private Direction facing = Direction.NORTH; // 默认方向，可后期改为从方块状态获取
	private ItemStack filter = ItemStack.EMPTY;

	public SNItemOutputPortBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		// 从方块状态获取朝向（端口方块使用 FACING,六面）
		if (state.hasProperty(BlockStateProperties.FACING)) {
			this.facing = state.getValue(BlockStateProperties.FACING);
		}
	}

	// 获取目标容器位置（根据朝向自动计算）
	public BlockPos getTargetPos() {
		if (targetPos == null && level != null) {
			// 默认朝方块面向方向偏移一格
			return worldPosition.relative(facing);
		}
		return targetPos;
	}

	public ItemStack getFilter() {
		return filter;
	}
	public void setFilter(ItemStack filter) {
		this.filter = filter.copy();
		this.filter.setCount(1);
		markDirtyAndUpdate();
	}

	public void clearFilter() {
		this.filter = ItemStack.EMPTY;
		markDirtyAndUpdate();
	}

	public void setTargetPos(BlockPos targetPos) {
		this.targetPos = targetPos;
		markDirty();
	}

	// 获取目标容器的 IItemHandler（用于输出）
	public LazyOptional<IItemHandler> getTargetItemHandler() {
		if (level == null) return LazyOptional.empty();
		BlockPos pos = getTargetPos();
		if (pos == null) return LazyOptional.empty();
		BlockEntity be = level.getBlockEntity(pos);
		if (be == null) return LazyOptional.empty();
		return be.getCapability(ForgeCapabilities.ITEM_HANDLER, facing.getOpposite());
	}

	// ========== NBT(持久化) ==========
	@Override
	protected void write(CompoundTag tag) {
		if (targetPos != null) {
			tag.putLong("TargetPos", targetPos.asLong());
		}
		if (!filter.isEmpty()) {
			tag.put("Filter", filter.save(new CompoundTag()));
		}
		tag.putInt("Facing", facing.get3DDataValue());
	}

	@Override
	protected void read(CompoundTag tag) {
		targetPos = tag.contains("TargetPos") ? BlockPos.of(tag.getLong("TargetPos")) : null;
		facing = tag.contains("Facing") ? Direction.from3DDataValue(tag.getInt("Facing")) : Direction.NORTH;
		filter = tag.contains("Filter") ? ItemStack.of(tag.getCompound("Filter")) : ItemStack.EMPTY;
	}

	// ========== 同步(总是写入 Filter,即使为空) ==========
	@Override
	protected void writeSync(CompoundTag tag) {
		tag.put("Filter", filter.save(new CompoundTag()));
		tag.putInt("Facing", facing.get3DDataValue());
	}

	@Override
	protected void readSync(CompoundTag tag) {
		filter = tag.contains("Filter") ? ItemStack.of(tag.getCompound("Filter")) : ItemStack.EMPTY;
		facing = tag.contains("Facing") ? Direction.from3DDataValue(tag.getInt("Facing")) : Direction.NORTH;
	}
}