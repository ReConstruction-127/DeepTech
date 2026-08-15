package dev.celestiacraft.deep_tech.config;

import dev.celestiacraft.deep_tech.config.common.machine.*;
import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig {
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec SPEC;

	public static final CrusherConfig CRUSHER;
	public static final SculkFurnaceConfig SCULK_FURNACE;
	public static final EXPGeneratorConfig EXP_GENERATOR;
	public static final AlloyFurnaceConfig ALLOY_FURNACE;
	public static final EnergyCellConfig ENERGY_CELL;
	public static final SculkCollectorConfig SCULK_COLLECTOR;

	static {
		BUILDER.comment("All settings below will only take effect after restarting the server or client.")
				.push("general");

		CRUSHER = new CrusherConfig(BUILDER);
		SCULK_FURNACE = new SculkFurnaceConfig(BUILDER);
		EXP_GENERATOR = new EXPGeneratorConfig(BUILDER);
		ENERGY_CELL = new EnergyCellConfig(BUILDER);
		ALLOY_FURNACE = new AlloyFurnaceConfig(BUILDER);
		SCULK_COLLECTOR = new SculkCollectorConfig(BUILDER);

		SPEC = BUILDER.build();
		BUILDER.pop();
	}
}