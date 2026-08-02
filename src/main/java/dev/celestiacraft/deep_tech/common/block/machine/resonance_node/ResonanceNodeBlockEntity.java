package dev.celestiacraft.deep_tech.common.block.machine.resonance_node;

import dev.celestiacraft.deep_tech.api.block.machine.MachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResonanceNodeBlockEntity extends MachineBlockEntity<ResonanceNodeBlockEntity> {
	private LazyOptional<IEnergyStorage> nodeEnergyCap;
	private final ResonanceNodeClientHelper clientHelper;

	public ResonanceNodeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		nodeEnergyCap = LazyOptional.of(() -> new ResonanceNodeEnergyStorage(this));
		clientHelper = new ResonanceNodeClientHelper(this);
	}

	public void clientTick(Level level, BlockPos pos, BlockState state, ResonanceNodeBlockEntity entity) {
		if (isLevelNotNull() || !level.isClientSide()) {
			return;
		}
		clientHelper.tick(level);
	}

	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
		if (capability == ForgeCapabilities.ENERGY) {
			// 紫水晶指向 FACING, 基座在 FACING 的反面; 只有节点自身的基座面能进行能量交互
			Direction baseSide = getBlockState().getValue(ResonanceNodeBlock.FACING).getOpposite();
			if (direction == baseSide || direction == null) {
				return nodeEnergyCap.cast();
			}
			return LazyOptional.empty();
		}
		return super.getCapability(capability, direction);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		nodeEnergyCap.invalidate();
	}
}