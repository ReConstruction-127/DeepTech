package dev.celestiacraft.deep_tech.common.block.machine.sculk_network.port;

import dev.celestiacraft.libs.api.register.block.BasicBlockEntity;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

@Getter
public class SNFluidInputPortBlockEntity extends BasicBlockEntity {
	private FluidStack filter = FluidStack.EMPTY;   // 过滤流体

	public SNFluidInputPortBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public void setFilter(FluidStack filter) {
		this.filter = filter.copy();
		this.filter.setAmount(1000); // 只存类型，不存数量
		markDirtyAndUpdate();
	}

	public void clearFilter() {
		filter = FluidStack.EMPTY;
		markDirtyAndUpdate();
	}

	// ========== NBT(持久化) ==========
	@Override
	protected void write(CompoundTag tag) {
		if (!filter.isEmpty()) {
			tag.put("Filter", filter.writeToNBT(new CompoundTag()));
		}
	}

	@Override
	protected void read(CompoundTag tag) {
		filter = tag.contains("Filter") ? FluidStack.loadFluidStackFromNBT(tag.getCompound("Filter")) : FluidStack.EMPTY;
	}

	// ========== 同步(总是写入 Filter,即使为空) ==========
	@Override
	protected void writeSync(CompoundTag tag) {
		tag.put("Filter", filter.writeToNBT(new CompoundTag()));
	}

	@Override
	protected void readSync(CompoundTag tag) {
		filter = tag.contains("Filter") ? FluidStack.loadFluidStackFromNBT(tag.getCompound("Filter")) : FluidStack.EMPTY;
	}

	// 获取目标容器的 IFluidHandler（输入端口专用）
	public LazyOptional<IFluidHandler> getTargetFluidHandler() {
		if (level == null) return LazyOptional.empty();
		BlockState state = getBlockState();
		if (!state.hasProperty(BlockStateProperties.FACING)) return LazyOptional.empty();
		Direction facing = state.getValue(BlockStateProperties.FACING);
		BlockPos targetPos = worldPosition.relative(facing);
		BlockEntity be = level.getBlockEntity(targetPos);
		if (be == null) return LazyOptional.empty();
		return be.getCapability(ForgeCapabilities.FLUID_HANDLER, facing.getOpposite());
	}
}