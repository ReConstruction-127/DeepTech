package dev.celestiacraft.deep_tech.common.block.machine.sculk_network.accessor;

import dev.celestiacraft.deep_tech.common.block.machine.sculk_network.port.SNPortBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class SNAccessorBlock extends SNPortBlock {  // 或者 extends HorizontalDirectionalBlock
	public SNAccessorBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}
}