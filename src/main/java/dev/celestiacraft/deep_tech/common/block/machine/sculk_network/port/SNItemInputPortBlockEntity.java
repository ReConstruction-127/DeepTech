package dev.celestiacraft.deep_tech.common.block.machine.sculk_network.port;

import dev.celestiacraft.deep_tech.common.block.machine.sculk_network.center.SNCenterBlockEntity;
import dev.celestiacraft.deep_tech.common.block.machine.sculk_network.reservoir.SNItemReservoirBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.List;

public class SNItemInputPortBlockEntity extends BlockEntity {
	private int tickCounter = 0;
	private static final int EXTRACT_INTERVAL = 10; // 每 10 Tick 抽一次

	public SNItemInputPortBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, SNItemInputPortBlockEntity be) {
		if (level.isClientSide) return;

		be.tickCounter++;
		if (be.tickCounter % EXTRACT_INTERVAL != 0) return;

		// 1. 获取朝向（输入口的面）
		Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
		BlockPos sourcePos = pos.relative(facing);

		// 2. 获取源容器（机器/箱子）的 IItemHandler
		BlockEntity sourceBe = level.getBlockEntity(sourcePos);
		if (sourceBe == null) return;

		LazyOptional<IItemHandler> cap = sourceBe.getCapability(ForgeCapabilities.ITEM_HANDLER, facing.getOpposite());
		if (!cap.isPresent()) return;

		IItemHandler sourceHandler = cap.orElse(null);
		if (sourceHandler == null) return;

		// 3. 查找最近的储存器并存入
		SNCenterBlockEntity center = be.findNearestCenter(level, pos);
		if (center == null) return;

		List<BlockPos> reservoirPositions = center.getSortedReservoirs();
		if (reservoirPositions.isEmpty()) return;

		// 4. 每次 tick 只处理一个槽位：模拟抽取 → 依次存入各储存器 → 实际抽取已存入的数量
		for (int slot = 0; slot < sourceHandler.getSlots(); slot++) {
			ItemStack extracted = sourceHandler.extractItem(slot, 64, true);
			if (extracted.isEmpty()) continue;

			int inserted = 0;
			ItemStack remaining = extracted.copy();
			for (BlockPos resPos : reservoirPositions) {
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

			// 实际从源容器抽取已成功存入的数量（物品不足时全部留存在源容器）
			if (inserted > 0) {
				sourceHandler.extractItem(slot, inserted, false);
			}
			break; // 每次只处理一个槽位
		}
	}

	private SNCenterBlockEntity findNearestCenter(Level level, BlockPos pos) {
		// BFS 找最近的中枢（优化：可以缓存）
		// 简单实现：扫描 16 格内所有中枢
		for (int dx = -16; dx <= 16; dx++) {
			for (int dy = -16; dy <= 16; dy++) {
				for (int dz = -16; dz <= 16; dz++) {
					BlockPos checkPos = pos.offset(dx, dy, dz);
					BlockEntity be = level.getBlockEntity(checkPos);
					if (be instanceof SNCenterBlockEntity) {
						return (SNCenterBlockEntity) be;
					}
				}
			}
		}
		return null;
	}
}