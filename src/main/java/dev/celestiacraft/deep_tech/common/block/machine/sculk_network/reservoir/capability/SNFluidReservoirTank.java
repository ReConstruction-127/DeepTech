package dev.celestiacraft.deep_tech.common.block.machine.sculk_network.reservoir.capability;

import dev.celestiacraft.deep_tech.common.block.machine.sculk_network.reservoir.SNFluidReservoirBlockEntity;
import net.minecraftforge.fluids.capability.templates.FluidTank;

/** 流体储库储罐:内容变化时通知所属储库标记脏数据并同步客户端 */
public class SNFluidReservoirTank extends FluidTank {
	private final SNFluidReservoirBlockEntity entity;

	public SNFluidReservoirTank(int capacity, SNFluidReservoirBlockEntity entity) {
		super(capacity);
		this.entity = entity;
	}

	@Override
	protected void onContentsChanged() {
		entity.onTankContentChanged();
	}
}