package dev.celestiacraft.deep_tech.common.block.machine.advanced.sculk_network.reservoir.capability;

import dev.celestiacraft.deep_tech.common.block.machine.advanced.sculk_network.reservoir.SNItemReservoirBlockEntity;
import net.minecraftforge.items.ItemStackHandler;

/** 物品储库库存(6 行 x 9 列):内容变化时标记所属储库脏数据 */
public class SNItemReservoirInventory extends ItemStackHandler {
	public static final int SLOT_COUNT = 54;

	private final SNItemReservoirBlockEntity entity;

	public SNItemReservoirInventory(SNItemReservoirBlockEntity entity) {
		super(SLOT_COUNT);
		this.entity = entity;
	}

	@Override
	protected void onContentsChanged(int slot) {
		entity.setChanged();
	}
}