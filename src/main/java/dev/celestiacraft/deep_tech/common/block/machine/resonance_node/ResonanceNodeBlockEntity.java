package dev.celestiacraft.deep_tech.common.block.machine.resonance_node;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResonanceNodeBlockEntity extends BlockEntity {

    public ResonanceNodeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
        if (capability == ForgeCapabilities.ENERGY) {
            // ✅ 只有底面（DOWN）或 null 查询时返回能量
            if (side == Direction.DOWN || side == null) {
                // TODO: 实现 16 格范围传输逻辑
                return LazyOptional.empty();
            }
            return LazyOptional.empty();
        }
        return super.getCapability(capability, side);
    }
}