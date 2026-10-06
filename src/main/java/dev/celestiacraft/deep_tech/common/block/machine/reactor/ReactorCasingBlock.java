package dev.celestiacraft.deep_tech.common.block.machine.reactor;

import dev.celestiacraft.deep_tech.api.block.machine.MachineBlock;
import dev.celestiacraft.libs.api.register.block.BasicBlock;
import dev.celestiacraft.libs.api.register.block.BlockFacing;
import org.jetbrains.annotations.NotNull;

/**
 * 反应堆外壳: 结构里所有标记为 {@code 1} 的位置.
 * <p>
 * 没有方块实体、没有朝向、也没有激活状态, 就是个建材; 但它和扩展坞一起构成
 * {@code deep_tech:reactor_casing} 标签, 所以外壳位置可以换成扩展坞。
 */
public class ReactorCasingBlock extends BasicBlock {
	public ReactorCasingBlock(Properties properties) {
		super(MachineBlock.advancedProperties(properties));
	}

	@Override
	protected @NotNull BlockFacing useFacingType() {
		return BlockFacing.NONE;
	}

	@Override
	protected boolean useLitState() {
		return false;
	}
}
