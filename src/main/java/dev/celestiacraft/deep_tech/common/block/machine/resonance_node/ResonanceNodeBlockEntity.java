package dev.celestiacraft.deep_tech.common.block.machine.resonance_node;

import dev.celestiacraft.deep_tech.DeepTech;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ResonanceNodeBlockEntity extends BlockEntity {

    private static final int RANGE = 16;
    private List<BlockPos> cachedNodes = new ArrayList<>();
    private int scanCooldown = 0;

    public ResonanceNodeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
        if (capability == ForgeCapabilities.ENERGY) {
            // ✅ 获取当前朝向，计算基座方向（朝向的反方向）
            Direction facing = getBlockState().getValue(ResonanceNodeBlock.FACING);
            Direction baseSide = facing.getOpposite(); // 基座方向 = 水晶朝向的反方向
            // ✅ 只有基座方向 或 null（用于 Jade/TOP 显示）才返回能量
            if (side == baseSide || side == null) {
                return LazyOptional.of(() -> new NetworkEnergyStorage()).cast();
            }
            return LazyOptional.empty();
        }
        return super.getCapability(capability, side);
    }

    /**
     * 自定义 IEnergyStorage：通过网络调度能量
     */
    private class NetworkEnergyStorage implements IEnergyStorage {

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            if (maxReceive <= 0 || level == null) return 0;

            scanNetwork();

            int totalReceived = 0;
            for (BlockPos nodePos : cachedNodes) {
                if (totalReceived >= maxReceive) break;

                BlockEntity nodeBe = level.getBlockEntity(nodePos);
                if (nodeBe == null || nodeBe == ResonanceNodeBlockEntity.this) continue;

                // 获取节点底面的方块
                BlockPos belowPos = nodePos.below();
                BlockEntity belowBe = level.getBlockEntity(belowPos);
                if (belowBe == null) continue;

                LazyOptional<IEnergyStorage> cap = belowBe.getCapability(ForgeCapabilities.ENERGY, Direction.UP);
                if (cap.isPresent()) {
                    IEnergyStorage storage = cap.orElse(null);
                    if (storage != null && storage.canReceive()) {
                        int remaining = maxReceive - totalReceived;
                        int received = storage.receiveEnergy(remaining, simulate);
                        totalReceived += received;
                    }
                }
            }
            return totalReceived;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            if (maxExtract <= 0 || level == null) return 0;

            scanNetwork();

            int totalExtracted = 0;
            for (BlockPos nodePos : cachedNodes) {
                if (totalExtracted >= maxExtract) break;

                BlockEntity nodeBe = level.getBlockEntity(nodePos);
                if (nodeBe == null || nodeBe == ResonanceNodeBlockEntity.this) continue;

                // 获取节点底面的方块
                BlockPos belowPos = nodePos.below();
                BlockEntity belowBe = level.getBlockEntity(belowPos);
                if (belowBe == null) continue;

                LazyOptional<IEnergyStorage> cap = belowBe.getCapability(ForgeCapabilities.ENERGY, Direction.UP);
                if (cap.isPresent()) {
                    IEnergyStorage storage = cap.orElse(null);
                    if (storage != null && storage.canExtract()) {
                        int remaining = maxExtract - totalExtracted;
                        int extracted = storage.extractEnergy(remaining, simulate);
                        totalExtracted += extracted;
                    }
                }
            }
            return totalExtracted;
        }

        @Override
        public int getEnergyStored() {
            if (level == null) return 0;

            scanNetwork();

            int total = 0;
            for (BlockPos nodePos : cachedNodes) {
                BlockEntity nodeBe = level.getBlockEntity(nodePos);
                if (nodeBe == null) continue;

                BlockPos belowPos = nodePos.below();
                BlockEntity belowBe = level.getBlockEntity(belowPos);
                if (belowBe == null) continue;

                LazyOptional<IEnergyStorage> cap = belowBe.getCapability(ForgeCapabilities.ENERGY, Direction.UP);
                if (cap.isPresent()) {
                    IEnergyStorage storage = cap.orElse(null);
                    if (storage != null) {
                        total += storage.getEnergyStored();
                    }
                }
            }
            return total;
        }

        @Override
        public int getMaxEnergyStored() {
            // 返回足够大的值，让机器觉得能存储无限能量
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean canExtract() {
            return true;
        }

        @Override
        public boolean canReceive() {
            return true;
        }
    }

    /**
     * 扫描 16 格范围内的所有节点
     */
    private void scanNetwork() {
        if (level == null) return;
        if (scanCooldown-- > 0) return;
        scanCooldown = 20; // 每 20 tick 刷新一次

        cachedNodes.clear();

        AABB rangeBox = new AABB(
                worldPosition.getX() - RANGE,
                worldPosition.getY() - RANGE,
                worldPosition.getZ() - RANGE,
                worldPosition.getX() + RANGE + 1,
                worldPosition.getY() + RANGE + 1,
                worldPosition.getZ() + RANGE + 1
        );

        for (BlockPos pos : BlockPos.betweenClosed(
                (int) rangeBox.minX, (int) rangeBox.minY, (int) rangeBox.minZ,
                (int) rangeBox.maxX, (int) rangeBox.maxY, (int) rangeBox.maxZ
        )) {
            if (pos.equals(worldPosition)) continue;

            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ResonanceNodeBlockEntity) {
                cachedNodes.add(pos.immutable());
            }
        }

        // 限制节点数量防止性能问题
        if (cachedNodes.size() > 100) {
            cachedNodes = cachedNodes.subList(0, 100);
        }

        DeepTech.LOGGER.debug("ResonanceNode at {} found {} nodes in range", worldPosition, cachedNodes.size());
    }
}