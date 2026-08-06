package dev.celestiacraft.deep_tech.compat.jade.machine;

import dev.celestiacraft.deep_tech.api.block.machine.MachineBlock;
import dev.celestiacraft.deep_tech.api.block.machine.MachineBlockEntity;
import dev.celestiacraft.deep_tech.common.register.block.MachineBlocks;
import dev.celestiacraft.deep_tech.compat.jade.api.DTJadeType;
import dev.celestiacraft.deep_tech.compat.jade.api.IJadeUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class MachineBasicInfo implements IBlockComponentProvider, IJadeUtils {
	@Override
	public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
		Block block = accessor.getBlock();
		BlockState state = accessor.getBlockState();
		BlockEntity entity = accessor.getBlockEntity();

		if (!(block instanceof MachineBlock<?>)) {
			return;
		}

		if (!(entity instanceof MachineBlockEntity<?> machine)) {
			return;
		}

		// 节点的数值比较特殊, 所以不分配信息显示
		if (state.is(MachineBlocks.RESONANCE_NODE.get())) {
			return;
		}

		Integer maxReceive = machine.getMaxReceive();
		if (maxReceive != null) {
			tooltip.add(Component.translatable(
					addTranKey("tooltip.jade.%s.info.max_receive"),
					maxReceive
			).withStyle(ChatFormatting.AQUA));
		}

		Integer maxExtract = machine.getMaxExtract();
		if (maxExtract != null) {
			tooltip.add(Component.translatable(
					addTranKey("tooltip.jade.%s.info.max_extract"),
					maxExtract
			).withStyle(ChatFormatting.GOLD));
		}
	}

	@Override
	public ResourceLocation getUid() {
		return DTJadeType.MACHINE;
	}
}