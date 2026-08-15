package dev.celestiacraft.deep_tech.common.block.machine.resonance_node.capability;

import dev.celestiacraft.deep_tech.common.block.machine.resonance_node.ResonanceNodeBlock;
import dev.celestiacraft.deep_tech.common.block.machine.resonance_node.ResonanceNodeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ResonanceNodeEnergyStorage implements IEnergyStorage {
	private final ResonanceNodeBlockEntity entity;
	private static final int RANGE = 16;
	private static final int SCAN_INTERVAL = 20;
	private List<BlockPos> cachedNodes = new ArrayList<>();
	private long lastScanTime = -1;

	public ResonanceNodeEnergyStorage(ResonanceNodeBlockEntity entity) {
		this.entity = entity;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		if (maxReceive <= 0 || entity.getLevel() == null) {
			return 0;
		}

		scanNetwork();

		int totalReceived = 0;
		for (BlockPos nodePos : cachedNodes) {
			if (totalReceived >= maxReceive) break;

			BlockEntity nodeBe = entity.getLevel().getBlockEntity(nodePos);
			if (nodeBe == null || nodeBe == entity) {
				continue;
			}

			IEnergyStorage storage = getBaseEnergyStorage(nodePos);
			if (storage != null && storage.canReceive()) {
				int remaining = maxReceive - totalReceived;
				int received = storage.receiveEnergy(remaining, simulate);
				totalReceived += received;
			}
		}
		return totalReceived;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		if (maxExtract <= 0 || entity.getLevel() == null) return 0;

		scanNetwork();

		int totalExtracted = 0;
		for (BlockPos nodePos : cachedNodes) {
			if (totalExtracted >= maxExtract) break;

			BlockEntity nodeBe = entity.getLevel().getBlockEntity(nodePos);
			if (nodeBe == null || nodeBe == entity) continue;

			IEnergyStorage storage = getBaseEnergyStorage(nodePos);
			if (storage != null && storage.canExtract()) {
				int remaining = maxExtract - totalExtracted;
				int extracted = storage.extractEnergy(remaining, simulate);
				totalExtracted += extracted;
			}
		}
		return totalExtracted;
	}

	@Override
	public int getEnergyStored() {
		if (entity.getLevel() == null) {
			return 0;
		}

		scanNetwork();

		int total = 0;
		for (BlockPos nodePos : cachedNodes) {
			IEnergyStorage storage = getBaseEnergyStorage(nodePos);
			if (storage != null) {
				total += storage.getEnergyStored();
			}
		}
		return total;
	}

	@Override
	public int getMaxEnergyStored() {
		// 返回足够大的值, 让机器觉得能存储无限能量
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

	/**
	 * 获取某个节点“基座面”所紧贴方块的能量存储. 
	 * <p>
	 * 基座面是节点自身朝向(FACING, 紫水晶指向)的反方向, 
	 * 而不是世界坐标下的底面(below). 
	 */
	@Nullable
	private IEnergyStorage getBaseEnergyStorage(BlockPos nodePos) {
		Level level = entity.getLevel();
		if (level == null) {
			return null;
		}

		BlockState nodeState = level.getBlockState(nodePos);
		if (!(nodeState.getBlock() instanceof ResonanceNodeBlock)) {
			return null;
		}

		Direction baseDir = nodeState.getValue(ResonanceNodeBlock.FACING).getOpposite();
		BlockPos basePos = nodePos.relative(baseDir);

		BlockEntity baseBe = level.getBlockEntity(basePos);
		// 基座紧贴的方块不能是另一个节点, 避免节点之间互相查询造成递归
		if (baseBe == null || baseBe instanceof ResonanceNodeBlockEntity) {
			return null;
		}

		LazyOptional<IEnergyStorage> cap = baseBe.getCapability(ForgeCapabilities.ENERGY, baseDir.getOpposite());
		return cap.orElse(null);
	}

	/**
	 * 扫描 16 格范围内的所有节点
	 */
	private void scanNetwork() {
		Level level = entity.getLevel();
		if (level == null) {
			return;
		}
		long time = level.getGameTime();
		if (lastScanTime != -1 && time - lastScanTime < SCAN_INTERVAL && time >= lastScanTime) {
			return;
		}
		lastScanTime = time;

		cachedNodes.clear();

		AABB rangeBox = new AABB(
				entity.getBlockPos().getX() - RANGE,
				entity.getBlockPos().getY() - RANGE,
				entity.getBlockPos().getZ() - RANGE,
				entity.getBlockPos().getX() + RANGE + 1,
				entity.getBlockPos().getY() + RANGE + 1,
				entity.getBlockPos().getZ() + RANGE + 1
		);

		for (BlockPos pos : BlockPos.betweenClosed(
				(int) rangeBox.minX, (int) rangeBox.minY, (int) rangeBox.minZ,
				(int) rangeBox.maxX, (int) rangeBox.maxY, (int) rangeBox.maxZ
		)) {
			if (pos.equals(entity.getBlockPos())) continue;

			BlockEntity be = entity.getLevel().getBlockEntity(pos);
			if (be instanceof ResonanceNodeBlockEntity) {
				cachedNodes.add(pos.immutable());
			}
		}

		// 限制节点数量防止性能问题
		if (cachedNodes.size() > 100) {
			cachedNodes = new ArrayList<>(cachedNodes.subList(0, 100));
		}
	}
}
