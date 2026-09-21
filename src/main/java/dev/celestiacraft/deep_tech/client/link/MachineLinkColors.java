package dev.celestiacraft.deep_tech.client.link;

import dev.celestiacraft.deep_tech.common.register.block.MachineBlocks;
import net.minecraft.world.level.block.Block;

import java.util.IdentityHashMap;
import java.util.Map;

public class MachineLinkColors {
	public static final int HUB = 0xE8FBFF;
	public static final int RESONANCE_NODE = 0x00E0B4;
	private static final int ITEM_INPUT_PORT = 0x62E06A;
	private static final int ITEM_OUTPUT_PORT = 0xFF9A3C;
	private static final int FLUID_INPUT_PORT = 0x49C9FF;
	private static final int FLUID_OUTPUT_PORT = 0xC072FF;
	private static final int ITEM_RESERVOIR = 0xFFE05C;
	private static final int FLUID_RESERVOIR = 0x4A7BFF;
	private static final int ACCESSOR = 0xFF6FA8;
	private static Map<Block, Integer> componentColors;

	public static int ofComponent(Block block) {
		if (componentColors == null) {
			componentColors = buildComponentColors();
		}
		return componentColors.getOrDefault(block, 0);
	}

	private static Map<Block, Integer> buildComponentColors() {
		Map<Block, Integer> colors = new IdentityHashMap<>();

		colors.put(MachineBlocks.SN_ITEM_INPUT_PORT.get(), ITEM_INPUT_PORT);
		colors.put(MachineBlocks.SN_ITEM_OUTPUT_PORT.get(), ITEM_OUTPUT_PORT);
		colors.put(MachineBlocks.SN_FLUID_INPUT_PORT.get(), FLUID_INPUT_PORT);
		colors.put(MachineBlocks.SN_FLUID_OUTPUT_PORT.get(), FLUID_OUTPUT_PORT);
		colors.put(MachineBlocks.SN_ITEM_RESERVOIR.get(), ITEM_RESERVOIR);
		colors.put(MachineBlocks.SN_FLUID_RESERVOIR.get(), FLUID_RESERVOIR);
		colors.put(MachineBlocks.SN_ACCESSOR.get(), ACCESSOR);

		return colors;
	}
}