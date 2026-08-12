package dev.celestiacraft.deep_tech.common.block.machine.sculk_network.port;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class SNFluidOutputPortBlockEntity extends BlockEntity {
	private FluidStack filter = FluidStack.EMPTY;   // 过滤流体
	private Direction facing = Direction.NORTH; // 新增

	public SNFluidOutputPortBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		// 从方块状态获取朝向（端口方块使用 FACING,六面）
		if (state.hasProperty(BlockStateProperties.FACING)) {
			this.facing = state.getValue(BlockStateProperties.FACING);
		}
	}

	public FluidStack getFilter() {
		return filter;
	}

	public void setFilter(FluidStack filter) {
		this.filter = filter.copy();
		this.filter.setAmount(1000); // 只存类型，不存数量
		setChanged();
		sync();
	}

	public void clearFilter() {
		this.filter = FluidStack.EMPTY;
		setChanged();
		sync();
	}

	// ========== NBT ==========
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		if (!filter.isEmpty()) {
			tag.put("Filter", filter.writeToNBT(new CompoundTag()));
		}
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if (tag.contains("Filter")) {
			filter = FluidStack.loadFluidStackFromNBT(tag.getCompound("Filter"));
		} else {
			filter = FluidStack.EMPTY;
		}
	}

	// ========== 同步 ==========
	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = super.getUpdateTag();
		// 总是写入，即使为空
		tag.put("Filter", filter.writeToNBT(new CompoundTag()));
		return tag;
	}

	@Override
	public void handleUpdateTag(CompoundTag tag) {
		super.handleUpdateTag(tag);
		filter = FluidStack.loadFluidStackFromNBT(tag.getCompound("Filter"));
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

	// 获取目标容器的 IFluidHandler（输入端口专用）
	public LazyOptional<IFluidHandler> getTargetFluidHandler() {
		if (level == null) return LazyOptional.empty();
		BlockPos targetPos = worldPosition.relative(facing); // 使用存储的 facing
		BlockEntity be = level.getBlockEntity(targetPos);
		if (be == null) return LazyOptional.empty();
		return be.getCapability(ForgeCapabilities.FLUID_HANDLER, facing.getOpposite());
	}
}