package dev.celestiacraft.deep_tech.common.block.machine.sculk_network.port;

import dev.celestiacraft.libs.api.register.block.BasicBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class SNFluidOutputPortBlockEntity extends BasicBlockEntity {
	private FluidStack filter = FluidStack.EMPTY;   // 过滤流体

	public SNFluidOutputPortBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public FluidStack getFilter() {
		return filter;
	}

	public void setFilter(FluidStack filter) {
		this.filter = filter.copy();
		this.filter.setAmount(1000); // 只存类型，不存数量
		markDirtyAndUpdate();
	}

	public void clearFilter() {
		this.filter = FluidStack.EMPTY;
		markDirtyAndUpdate();
	}

	// ========== NBT(持久化,朝向由方块状态持有,无需存储) ==========
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

	// 获取目标容器的 IFluidHandler（输出端口专用）
	public LazyOptional<IFluidHandler> getTargetFluidHandler() {
		if (level == null) return LazyOptional.empty();
		BlockState state = getState();
		if (!state.hasProperty(BlockStateProperties.FACING)) return LazyOptional.empty();
		BlockPos targetPos = worldPosition.relative(state.getValue(BlockStateProperties.FACING));
		BlockEntity be = level.getBlockEntity(targetPos);
		if (be == null) return LazyOptional.empty();
		return be.getCapability(ForgeCapabilities.FLUID_HANDLER, state.getValue(BlockStateProperties.FACING).getOpposite());
	}
}