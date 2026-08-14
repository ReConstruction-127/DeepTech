package dev.celestiacraft.deep_tech.common.block.machine.sculk_network.center;

import dev.celestiacraft.deep_tech.common.block.machine.sculk_network.port.SNFluidInputPortBlockEntity;
import dev.celestiacraft.deep_tech.common.block.machine.sculk_network.port.SNFluidOutputPortBlockEntity;
import dev.celestiacraft.deep_tech.common.block.machine.sculk_network.port.SNHelper;
import dev.celestiacraft.deep_tech.common.block.machine.sculk_network.port.SNItemInputPortBlockEntity;
import dev.celestiacraft.deep_tech.common.block.machine.sculk_network.port.SNItemOutputPortBlockEntity;
import dev.celestiacraft.deep_tech.common.block.machine.sculk_network.reservoir.SNFluidReservoirBlockEntity;
import dev.celestiacraft.deep_tech.common.block.machine.sculk_network.reservoir.SNItemReservoirBlockEntity;
import dev.celestiacraft.deep_tech.common.register.block.MachineBlocks;
import dev.celestiacraft.libs.api.register.block.BasicBlockEntity;
import dev.celestiacraft.libs.api.register.block.ITickableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class SNCenterBlockEntity extends BasicBlockEntity implements ITickableBlockEntity<SNCenterBlockEntity> {



	// ========== 能量 ==========
	private int energyStored = 0;
	private static final int MAX_ENERGY = 10000;
	private static final int SCAN_COST = 1;

	// ========== 主控标签 ==========
	private boolean isMaster = false;   // 是否带标签

	// ========== 扫描控制 ==========
	private int scanCooldown = 0;
	private static final int SCAN_INTERVAL = 20; // 20 tick = 1 秒

	// ========== 扫描结果（用于视觉标记） ==========
	private Set<BlockPos> currentScanResult = new HashSet<>();
	private Set<BlockPos> previousScanResult = new HashSet<>();

	// ========== 扫描结果:网络组件列表（按距离由近到远排序） ==========
	private final List<BlockPos> foundReservoirs = new ArrayList<>();
	private final List<BlockPos> foundItemInputPorts = new ArrayList<>();
	private final List<BlockPos> foundItemOutputPorts = new ArrayList<>();  // 新增
	private final List<BlockPos> foundFluidReservoirs = new ArrayList<>();
	private final List<BlockPos> foundFluidInputPorts = new ArrayList<>();
	private final List<BlockPos> foundFluidOutputPorts = new ArrayList<>();

	// ========== 物品转运控制 ==========
	private int tickCounter = 0;
	private static final int TRANSFER_INTERVAL = 10; // 每 10 Tick 转运一次

	private final LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(this::getEnergyCapability);


	public SNCenterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// ============================================================
	//  Tick 调度（由 IEntityBlock 默认 ticker 驱动,仅服务端）
	// ============================================================

	@Override
	public void serverTick(Level level, BlockPos pos, BlockState state, SNCenterBlockEntity entity) {
		// 1. 如果自身已被移除（比如爆炸后），不再执行
		if (isRemoved()) return;

		// 2. 扫描与冲突检测（每秒一次）
		scanCooldown--;
		if (scanCooldown <= 0) {
			scanCooldown = SCAN_INTERVAL;
			performScan((ServerLevel) level, pos);
		}

		// 3. 更新调试标记（若 BE 还在）
		if (!isRemoved()) {
			cleanupDebugMarkers((ServerLevel) level);
			applyDebugMarkers((ServerLevel) level);
		}

		// 4. 物品转运：从网络中的输入端口抽取物品存入最近的贮存器(每 10 tick 一次)
		tickCounter++;
		if (tickCounter % TRANSFER_INTERVAL == 0) {
			transferItemsFromInputPorts((ServerLevel) level);
			transferItemsToOutputPorts((ServerLevel) level);
		}

		// 5. 流体转运:每 tick 执行,50 mB/tick = 1000 mB/s
		transferFluidsFromInputPorts((ServerLevel) level);
		transferFluidsToOutputPorts((ServerLevel) level);
	}

	// ============================================================
	//  核心扫描 (BFS) + 冲突检测
	// ============================================================

	private void performScan(ServerLevel level, BlockPos center) {
		// 检查能量
		if (energyStored < SCAN_COST) {
			return;
		}
		energyStored -= SCAN_COST;
		markDirty();

		// 保存旧结果
		previousScanResult = new HashSet<>(currentScanResult);
		currentScanResult = new HashSet<>();
		foundReservoirs.clear();
		foundItemInputPorts.clear();
		foundItemOutputPorts.clear();
		foundFluidReservoirs.clear();
		foundFluidInputPorts.clear();
		foundFluidOutputPorts.clear();

		// BFS 队列
		Queue<BlockPos> queue = new ArrayDeque<>();
		Map<BlockPos, Integer> distanceMap = new HashMap<>();

		queue.add(center);
		distanceMap.put(center, 0);
		currentScanResult.add(center);

		// 记录扫描到的中枢（包括自身）
		List<BlockPos> centersFound = new ArrayList<>();

		while (!queue.isEmpty()) {
			BlockPos current = queue.poll();
			int distance = distanceMap.get(current);

			// 检查当前方块是否为中枢（用于后续冲突检测）
			if (level.getBlockState(current).getBlock() == MachineBlocks.SN_CENTER.get()) {
				// 只记录有效的中枢（即未被破坏的）
				BlockEntity be = level.getBlockEntity(current);
				if (be instanceof SNCenterBlockEntity) {
					centersFound.add(current);
				}
			}

			// 收集网络组件（物品贮存器 / 物品输入端口）
			Block blockAt = level.getBlockState(current).getBlock();
			if (blockAt == MachineBlocks.SN_ITEM_RESERVOIR.get()) {
				foundReservoirs.add(current);
			}
			if (blockAt == MachineBlocks.SN_ITEM_INPUT_PORT.get()) {
				foundItemInputPorts.add(current);
			}
			if (blockAt == MachineBlocks.SN_ITEM_OUTPUT_PORT.get()) {   // 新增
				foundItemOutputPorts.add(current);
			}
			if (blockAt == MachineBlocks.SN_FLUID_RESERVOIR.get()) {
				foundFluidReservoirs.add(current);
			}
			if (blockAt == MachineBlocks.SN_FLUID_INPUT_PORT.get()) {
				foundFluidInputPorts.add(current);
			}
			if (blockAt == MachineBlocks.SN_FLUID_OUTPUT_PORT.get()) {
				foundFluidOutputPorts.add(current);
			}

			if (distance >= 16) continue;

			for (Direction dir : Direction.values()) {
				BlockPos neighbor = current.relative(dir);
				if (distanceMap.containsKey(neighbor)) continue;
				if (isNetworkComponent(level, neighbor)) {
					distanceMap.put(neighbor, distance + 1);
					queue.add(neighbor);
					currentScanResult.add(neighbor);
				}
			}
		}

		// 组件按到中枢的距离由近到远排序
		foundReservoirs.sort(Comparator.comparingDouble(pos -> pos.distSqr(center)));
		foundItemInputPorts.sort(Comparator.comparingDouble(pos -> pos.distSqr(center)));
		foundItemOutputPorts.sort(Comparator.comparingDouble(pos -> pos.distSqr(center)));
		foundFluidReservoirs.sort(Comparator.comparingDouble(pos -> pos.distSqr(center)));
		foundFluidInputPorts.sort(Comparator.comparingDouble(pos -> pos.distSqr(center)));
		foundFluidOutputPorts.sort(Comparator.comparingDouble(pos -> pos.distSqr(center)));

		// ----- 冲突检测逻辑 -----
		// 统计网络中所有中枢的主控状态
		List<BlockPos> masterCenters = new ArrayList<>();
		for (BlockPos pos : centersFound) {
			BlockEntity be = level.getBlockEntity(pos);
			if (be instanceof SNCenterBlockEntity) {
				if (((SNCenterBlockEntity) be).isMaster) {
					masterCenters.add(pos);
				}
			}
		}

		// 当前中枢是否在 masterCenters 中（可能自身已经是主控）
		boolean selfIsMaster = masterCenters.contains(center);

		// 情况 1：网络中无主控
		if (masterCenters.isEmpty()) {
			// 自动成为主控
			isMaster = true;
			markDirtyAndUpdate();
			return;
		}

		// 情况 2：网络中恰好一个主控
		if (masterCenters.size() == 1) {
			// 如果当前中枢不是主控，且网络中有其他主控 → 触发爆炸（后来的中枢）
			if (!selfIsMaster) {
				// 这个中枢是“后来的”，爆炸
				explodeCenter(level, center, "conflict");
				return; // 爆炸后 BE 被移除，直接返回
			}
			// 如果自身是主控，则正常
			return;
		}

		// 情况 3：网络中 ≥2 个主控 → 所有带标签的中枢全部爆炸
		for (BlockPos pos : masterCenters) {
			explodeCenter(level, pos, "duplicate");
		}
		// 爆炸会移除这些中枢，可能包括自身，所以返回
		// 注意：如果当前中枢在 masterCenters 中，它也会被移除，BE 将失效
		// 但因为我们已经爆炸，返回即可
	}

	// ============================================================
	//  爆炸工具
	// ============================================================

	private void explodeCenter(ServerLevel level, BlockPos pos, String reason) {
		// 触发等级 2 爆炸（不破坏地形？可以用原版爆炸，也可只移除方块并产生粒子）
		// 为了达到“等级2的爆炸”效果，我们使用原版爆炸并保留破坏
		level.explode(null, pos.getX(), pos.getY(), pos.getZ(),
				1.0f, Level.ExplosionInteraction.TNT);

		// 注意：爆炸会移除该位置的方块，同时触发事件，BE 会自动失效
		// 但可能因为爆炸延迟，我们需要额外确保方块被移除
		// 不过爆炸本身会处理，所以不额外操作
	}

	// ============================================================
	//  网络组件判定
	// ============================================================

	/**
	 * 返回 BFS 扫描到的物品贮存器位置列表（已按距离由近到远排序）。
	 * 供物品转运逻辑查找存储目标使用。
	 */
	public List<BlockPos> getSortedReservoirs() {
		return foundReservoirs;
	}

	/**
	 * 返回 BFS 扫描到的物品输入端口位置列表（已按距离由近到远排序）。
	 */
	public List<BlockPos> getItemInputPorts() {
		return foundItemInputPorts;
	}

	// ============================================================
	//  物品转运（中枢驱动）
	// ============================================================

	private void transferItemsFromInputPorts(ServerLevel level) {
		for (BlockPos portPos : foundItemInputPorts) {
			BlockState portState = level.getBlockState(portPos);
			if (portState.getBlock() != MachineBlocks.SN_ITEM_INPUT_PORT.get()) continue;

			// 获取端口 BE 及过滤
			BlockEntity portBe = level.getBlockEntity(portPos);
			if (!(portBe instanceof SNItemInputPortBlockEntity inputPort)) continue;
			ItemStack filter = inputPort.getFilter();

			// 获取源容器
			Direction facing = portState.getValue(BlockStateProperties.FACING);
			BlockEntity sourceBe = level.getBlockEntity(portPos.relative(facing));
			if (sourceBe == null) continue;

			LazyOptional<IItemHandler> cap = sourceBe.getCapability(ForgeCapabilities.ITEM_HANDLER, facing.getOpposite());
			if (!cap.isPresent()) continue;
			IItemHandler sourceHandler = cap.orElse(null);
			if (sourceHandler == null) continue;

			// 遍历源容器槽位
			for (int slot = 0; slot < sourceHandler.getSlots(); slot++) {
				ItemStack extracted = sourceHandler.extractItem(slot, 64, true);
				if (extracted.isEmpty()) continue;

				// --- 过滤检查 ---
				if (!filter.isEmpty() && !ItemStack.isSameItemSameTags(extracted, filter)) {
					continue; // 不匹配，跳过此物品
				}

				// 尝试存入储存器
				int inserted = 0;
				ItemStack remaining = extracted.copy();
				for (BlockPos resPos : foundReservoirs) {
					BlockEntity resBe = level.getBlockEntity(resPos);
					if (!(resBe instanceof SNItemReservoirBlockEntity reservoirBe)) continue;

					LazyOptional<IItemHandler> resCap = reservoirBe.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.UP);
					if (!resCap.isPresent()) continue;
					IItemHandler resHandler = resCap.orElse(null);
					if (resHandler == null) continue;

					int before = remaining.getCount();
					ItemStack leftover = ItemHandlerHelper.insertItem(resHandler, remaining, false);
					inserted += before - leftover.getCount();
					remaining = leftover;
					if (remaining.isEmpty()) break;
				}

				if (inserted > 0) {
					sourceHandler.extractItem(slot, inserted, false);
				}
				break; // 每次只处理一个槽位
			}
		}
	}


	private void transferItemsToOutputPorts(ServerLevel level) {
		if (foundReservoirs.isEmpty() || foundItemOutputPorts.isEmpty()) return;

		for (BlockPos portPos : foundItemOutputPorts) {
			BlockState portState = level.getBlockState(portPos);
			if (portState.getBlock() != MachineBlocks.SN_ITEM_OUTPUT_PORT.get()) continue;

			// 获取端口 BE 及过滤
			BlockEntity portBe = level.getBlockEntity(portPos);
			if (!(portBe instanceof SNItemOutputPortBlockEntity outputPort)) continue;
			ItemStack filter = outputPort.getFilter();

			// 获取目标容器
			LazyOptional<IItemHandler> targetCap = outputPort.getTargetItemHandler();
			if (!targetCap.isPresent()) continue;
			IItemHandler targetHandler = targetCap.orElse(null);
			if (targetHandler == null) continue;

			for (BlockPos resPos : foundReservoirs) {
				BlockEntity resBe = level.getBlockEntity(resPos);
				if (!(resBe instanceof SNItemReservoirBlockEntity reservoir)) continue;

				LazyOptional<IItemHandler> resCap = reservoir.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.UP);
				if (!resCap.isPresent()) continue;
				IItemHandler resHandler = resCap.orElse(null);
				if (resHandler == null) continue;

				for (int slot = 0; slot < resHandler.getSlots(); slot++) {
					ItemStack stack = resHandler.getStackInSlot(slot);
					if (stack.isEmpty()) continue;

					// --- 过滤检查 ---
					if (!filter.isEmpty() && !ItemStack.isSameItemSameTags(stack, filter)) {
						continue;
					}

					int maxExtract = Math.min(stack.getCount(), 64);
					ItemStack extracted = resHandler.extractItem(slot, maxExtract, true);
					if (extracted.isEmpty()) continue;

					int before = extracted.getCount();
					ItemStack leftover = ItemHandlerHelper.insertItem(targetHandler, extracted, false);
					int inserted = before - leftover.getCount();

					if (inserted > 0) {
						resHandler.extractItem(slot, inserted, false);
						reservoir.markAsDirty();
						break; // 每次只转移一组
					}
				}
			}
		}
	}


	private static final int FLUID_TRANSFER_RATE = 50; // 每 tick 50 mB = 1000 mB/s(传输每 tick 执行)

	private void transferFluidsFromInputPorts(ServerLevel level) {
		for (BlockPos portPos : foundFluidInputPorts) {
			BlockState portState = level.getBlockState(portPos);
			if (portState.getBlock() != MachineBlocks.SN_FLUID_INPUT_PORT.get()) continue;

			BlockEntity portBe = level.getBlockEntity(portPos);
			if (!(portBe instanceof SNFluidInputPortBlockEntity inputPort)) continue;
			FluidStack filter = inputPort.getFilter();

			LazyOptional<IFluidHandler> targetCap = inputPort.getTargetFluidHandler();
			if (!targetCap.isPresent()) continue;
			IFluidHandler targetHandler = targetCap.orElse(null);
			if (targetHandler == null) continue;

			// 按罐遍历(从后往前),逐罐匹配过滤后抽取。
			// 不能用无类型 drain(max) 一次抽:聚合容器只会返回最后一个非空罐的流体,
			// 过滤判断会错位(例如最后罐是牛奶、前面罐是水,过滤=水将永远抽不出)。
			for (int tank = targetHandler.getTanks() - 1; tank >= 0; tank--) {
				FluidStack tankFluid = targetHandler.getFluidInTank(tank);
				if (tankFluid.isEmpty()) continue;

				// 过滤检查
				if (!filter.isEmpty() && !tankFluid.isFluidEqual(filter)) continue;

				int maxExtract = Math.min(FLUID_TRANSFER_RATE, tankFluid.getAmount());
				if (maxExtract <= 0) continue;

				// 从该罐模拟抽取
				FluidStack request = new FluidStack(tankFluid.getFluid(), maxExtract, tankFluid.getTag());
				FluidStack drained = targetHandler.drain(request, IFluidHandler.FluidAction.SIMULATE);
				if (drained.isEmpty()) continue;

				// 尝试存入最近的储存器
				for (BlockPos resPos : foundFluidReservoirs) {
					BlockEntity resBe = level.getBlockEntity(resPos);
					if (!(resBe instanceof SNFluidReservoirBlockEntity reservoir)) continue;
					IFluidHandler resHandler = reservoir.getTank();

					int filled = resHandler.fill(drained, IFluidHandler.FluidAction.SIMULATE);
					if (filled > 0) {
						// 先从源容器真正抽取
						FluidStack actualDrained = targetHandler.drain(request, IFluidHandler.FluidAction.EXECUTE);
						if (!actualDrained.isEmpty()) {
							resHandler.fill(actualDrained, IFluidHandler.FluidAction.EXECUTE);
							reservoir.markAsDirty();
							break; // 一次只处理一个罐
						}
					}
				}
			}
		}
	}

	private void transferFluidsToOutputPorts(ServerLevel level) {
		if (foundFluidReservoirs.isEmpty() || foundFluidOutputPorts.isEmpty()) return;

		for (BlockPos portPos : foundFluidOutputPorts) {
			BlockState portState = level.getBlockState(portPos);
			if (portState.getBlock() != MachineBlocks.SN_FLUID_OUTPUT_PORT.get()) continue;

			BlockEntity portBe = level.getBlockEntity(portPos);
			if (!(portBe instanceof SNFluidOutputPortBlockEntity outputPort)) continue;
			FluidStack filter = outputPort.getFilter();

			// 获取目标容器
			LazyOptional<IFluidHandler> targetCap = outputPort.getTargetFluidHandler();
			if (!targetCap.isPresent()) continue;
			IFluidHandler targetHandler = targetCap.orElse(null);
			if (targetHandler == null) continue;

			// 从最近的流体储存器抽取:按罐(从后往前)逐罐匹配过滤。
			// 不能用聚合 drain(max) 一次抽:它只返回最后一个非空罐的流体,
			// 过滤判断会错位(例如最后罐是牛奶、前面罐是水,过滤=水将永远抽不出)。
			for (BlockPos resPos : foundFluidReservoirs) {
				BlockEntity resBe = level.getBlockEntity(resPos);
				if (!(resBe instanceof SNFluidReservoirBlockEntity reservoir)) continue;

				for (int tank = reservoir.getTankCount() - 1; tank >= 0; tank--) {
					FluidTank fluidTank = reservoir.getFluidTank(tank);
					FluidStack tankFluid = fluidTank.getFluid();
					if (tankFluid.isEmpty()) continue;

					// 过滤检查
					if (!filter.isEmpty() && !tankFluid.isFluidEqual(filter)) continue;

					int maxExtract = Math.min(FLUID_TRANSFER_RATE, tankFluid.getAmount());
					if (maxExtract <= 0) continue;

					// 模拟抽取
					FluidStack drained = fluidTank.drain(maxExtract, IFluidHandler.FluidAction.SIMULATE);
					if (drained.isEmpty()) continue;

					// 尝试推入目标容器
					int filled = targetHandler.fill(drained, IFluidHandler.FluidAction.SIMULATE);
					if (filled > 0) {
						// 真正抽取
						FluidStack actualDrained = fluidTank.drain(filled, IFluidHandler.FluidAction.EXECUTE);
						if (!actualDrained.isEmpty()) {
							targetHandler.fill(actualDrained, IFluidHandler.FluidAction.EXECUTE);
							reservoir.markAsDirty();
							return; // 每次只处理一组
						}
					}
				}
			}
		}
	}

	private boolean isNetworkComponent(ServerLevel level, BlockPos pos) {
		return SNHelper.isNetworkComponent(level, pos);
	}

	// ============================================================
	//  调试视觉标记
	// ============================================================

	/**
	 * 在所有扫描到的组件上方放一块玻璃（临时标记）
	 */
	private void applyDebugMarkers(ServerLevel level) {
		for (BlockPos pos : currentScanResult) {
			BlockPos markerPos = pos.above();
			// 只在空位放标记
			if (level.isEmptyBlock(markerPos)) {
				level.setBlock(markerPos, Blocks.GLASS.defaultBlockState(), 3);
			}

			// 粒子效果：在方块中心生成紫色粒子
			Vec3 center = Vec3.atCenterOf(pos);
			level.sendParticles(
					ParticleTypes.END_ROD,
					center.x, center.y + 0.5, center.z,
					1,  // 数量
					0.1, 0.1, 0.1,  // 随机偏移
					0.0  // 速度
			);
		}
	}

	/**
	 * 移除上一轮放的玻璃标记
	 */
	private void cleanupDebugMarkers(ServerLevel level) {
		for (BlockPos pos : previousScanResult) {
			BlockPos markerPos = pos.above();
			if (level.getBlockState(markerPos).getBlock() == Blocks.GLASS) {
				level.setBlock(markerPos, Blocks.AIR.defaultBlockState(), 3);
			}
		}
	}

	// ============================================================
	//  能量 Capability
	// ============================================================

	private IEnergyStorage getEnergyCapability() {
		return new IEnergyStorage() {
			@Override public int receiveEnergy(int maxReceive, boolean simulate) {
				int received = Math.min(maxReceive, MAX_ENERGY - energyStored);
				if (!simulate) { energyStored += received; markDirty(); }
				return received;
			}
			@Override public int extractEnergy(int maxExtract, boolean simulate) { return 0; }
			@Override public int getEnergyStored() { return energyStored; }
			@Override public int getMaxEnergyStored() { return MAX_ENERGY; }
			@Override public boolean canExtract() { return false; }
			@Override public boolean canReceive() { return true; }
		};
	}

	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if (cap == ForgeCapabilities.ENERGY) {
			return energyCap.cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	protected void onCapsInvalidated() {
		super.onCapsInvalidated();
		energyCap.invalidate();
	}

	// ============================================================
	//  NBT 持久化
	// ============================================================

	@Override
	protected void write(CompoundTag tag) {
		tag.putInt("Energy", energyStored);
		tag.putBoolean("IsMaster", isMaster);
	}

	@Override
	protected void read(CompoundTag tag) {
		energyStored = tag.getInt("Energy");
		isMaster = tag.getBoolean("IsMaster");
	}
}