package dev.celestiacraft.deep_tech.client.link;

import dev.celestiacraft.deep_tech.common.register.block.MachineBlocks;
import net.minecraft.world.level.block.Block;

import java.util.IdentityHashMap;
import java.util.Map;

public class MachineLinkColors {
	public static final int HUB = 0xE8FBFF;
	public static final int RESONANCE_NODE = 0xBC68FF;
	private static final int ITEM_INPUT_PORT = 0x49C9FF;
	private static final int ITEM_OUTPUT_PORT = 0xFF9A3C;
	private static final int FLUID_INPUT_PORT = 0x49C9FF;
	private static final int FLUID_OUTPUT_PORT = 0xFF9A3C;
	private static final int ITEM_RESERVOIR = 0x72ADB9;
	private static final int FLUID_RESERVOIR = 0x106376;
	private static final int ACCESSOR = 0xD1D6B6;
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