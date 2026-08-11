package dev.celestiacraft.deep_tech.common.block.machine.sculk_network.port;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public class SNItemOutputPortBlockEntity extends BlockEntity {
	// 目标容器位置（可配置，初版使用方块面向方向）
	private BlockPos targetPos = null;
	private Direction facing = Direction.NORTH; // 默认方向，可后期改为从方块状态获取

	public SNItemOutputPortBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		// 从方块状态获取朝向（端口方块使用 HORIZONTAL_FACING）
		if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
			this.facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
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

	public void setTargetPos(BlockPos targetPos) {
		this.targetPos = targetPos;
		setChanged();
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

	// ========== NBT ==========
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		if (targetPos != null) {
			tag.putLong("TargetPos", targetPos.asLong());
		}
		tag.putInt("Facing", facing.get3DDataValue());
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if (tag.contains("TargetPos")) {
			targetPos = BlockPos.of(tag.getLong("TargetPos"));
		}
		if (tag.contains("Facing")) {
			facing = Direction.from3DDataValue(tag.getInt("Facing"));
		}
	}

	// ========== 同步 ==========
	private void sync() {
		if (level != null && !level.isClientSide) {
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
		}
	}
}