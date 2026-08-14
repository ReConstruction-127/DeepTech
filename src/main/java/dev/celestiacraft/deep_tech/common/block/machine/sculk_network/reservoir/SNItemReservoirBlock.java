package dev.celestiacraft.deep_tech.common.block.machine.sculk_network.reservoir;

import dev.celestiacraft.deep_tech.api.block.machine.MachineBlock; // 确保是您自己的基类
import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class SNItemReservoirBlock extends MachineBlock<SNItemReservoirBlockEntity> {

	public SNItemReservoirBlock(Properties properties) {
		super(properties.noOcclusion()); // 根据需要调整
	}

	@Override
	public BlockEntityType<SNItemReservoirBlockEntity> getBlockEntityType() {
		return DTBlockEntities.SN_ITEM_RESERVOIR.get(); // 确保注册名正确
	}

	@Override
	public Class<SNItemReservoirBlockEntity> getBlockEntityClass() {
		return SNItemReservoirBlockEntity.class;
	}

	// 可选：重写光照等（如不需要留空）
	// 注意：UI 打开由基类 MachineBlock 自动处理，无需重写 use()
}