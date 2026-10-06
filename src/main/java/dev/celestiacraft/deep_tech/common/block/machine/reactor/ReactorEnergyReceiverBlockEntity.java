package dev.celestiacraft.deep_tech.common.block.machine.reactor;

import dev.celestiacraft.deep_tech.api.block.machine.MachineBlockEntity;
import dev.celestiacraft.deep_tech.config.common.machine.advanced.ReactorConfig;
import dev.celestiacraft.libs.api.register.block.BasicBlock;
import dev.celestiacraft.libs.api.register.block.ITickableBlockEntity;
import dev.celestiacraft.libs.compat.patchouli.multiblock.MultiblockHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * 反应堆能量接收器.
 * <p>
 * 放在反应堆中心 {@code RECEIVER_RADIUS}(默认 5)格以内, 每 tick 破坏结构内 1 个幽匿块,
 * 每块给自己充 {@code ENERGY_PER_SCULK}(默认 5000)FE, 再通过能量 capability 对外输出。
 * <p>
 * 「哪个方块算结构内的」完全由控制器的 {@link MultiblockHandler} 判定,
 * 所以接收器不需要自己知道结构的形状与朝向。
 */
public class ReactorEnergyReceiverBlockEntity extends MachineBlockEntity<ReactorEnergyReceiverBlockEntity>
		implements ITickableBlockEntity<ReactorEnergyReceiverBlockEntity> {

	public ReactorEnergyReceiverBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// ------------------------------------------------------------------
	// 存储配置: 只出不进(电只能靠收割幽匿块获得)
	// ------------------------------------------------------------------

	@Override
	public int getItemInputSlotCount() {
		return 0;
	}

	@Override
	public int getItemOutputSlotCount() {
		return 0;
	}

	@Override
	public int getMachineMaxEnergy() {
		return ReactorConfig.RECEIVER_MAX_ENERGY.get();
	}

	@Override
	public int getMaxReceive() {
		return 0;
	}

	@Override
	public int getMaxExtract() {
		return ReactorConfig.RECEIVER_MAX_EXTRACT.get();
	}

	// ------------------------------------------------------------------
	// tick
	// ------------------------------------------------------------------

	@Override
	public void serverTick(Level level, BlockPos pos, BlockState state, ReactorEnergyReceiverBlockEntity entity) {
		int gain = ReactorConfig.ENERGY_PER_SCULK.get();

		if (getEnergyStored() + gain > getMaxEnergyStored()) {
			updateLit(level, pos, state, false);
			return;
		}

		BlockPos sculk = findSculk(level, pos);

		if (sculk == null) {
			updateLit(level, pos, state, false);
			return;
		}

		// drop = false: 收割不产生掉落物
		if (level.destroyBlock(sculk, false)) {
			setEnergy(getEnergyStored() + gain);
			markDirty();
			sync();
			updateLit(level, pos, state, true);
			return;
		}

		updateLit(level, pos, state, false);
	}

	/**
	 * 在半径内找一台结构成立的反应堆控制器, 再问它要结构内幽匿块的位置。
	 */
	private BlockPos findSculk(Level level, BlockPos pos) {
		int radius = ReactorConfig.RECEIVER_RADIUS.get();

		for (BlockPos probe : BlockPos.betweenClosed(pos.offset(-radius, -radius, -radius), pos.offset(radius, radius, radius))) {
			if (!(level.getBlockEntity(probe) instanceof ReactorControllerBlockEntity controller)) {
				continue;
			}

			MultiblockHandler handler = controller.getMultiblockHandler();
			if (!handler.isValid()) {
				continue;
			}

			List<BlockPos> sculk = handler.findBlock(Blocks.SCULK);
			if (!sculk.isEmpty()) {
				return sculk.get(0);
			}
		}

		return null;
	}

	private void updateLit(Level level, BlockPos pos, BlockState state, boolean lit) {
		if (state.hasProperty(BasicBlock.LIT) && state.getValue(BasicBlock.LIT) != lit) {
			level.setBlock(pos, state.setValue(BasicBlock.LIT, lit), Block.UPDATE_ALL);
		}
	}
}
