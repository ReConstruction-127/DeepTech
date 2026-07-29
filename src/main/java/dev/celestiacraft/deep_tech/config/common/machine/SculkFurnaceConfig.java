package dev.celestiacraft.deep_tech.config.common.machine;

import dev.celestiacraft.deep_tech.config.ConfigModule;
import net.minecraftforge.common.ForgeConfigSpec;

public class SculkFurnaceConfig extends ConfigModule {
	public static ForgeConfigSpec.IntValue MAX_ENERGY;
	public static ForgeConfigSpec.IntValue MAX_RECEIVE;

	public SculkFurnaceConfig(ForgeConfigSpec.Builder builder) {
		super(builder, "sculk_furnace", "Sculk Furnace");
	}

	@Override
	protected void addConfigs() {
		MAX_ENERGY = builder.comment("Sculk Furnace's max energy stored")
				.comment("type: int")
				.comment("default: 10000")
				.defineInRange("sculk_furnace_max_energy_stored", 10000, 1, Integer.MAX_VALUE);

		MAX_RECEIVE = builder.comment("Sculk Furnace's max energy receive")
				.comment("type: int")
				.comment("default: 100")
				.defineInRange("sculk_furnace_max_energy_receive", 100, 1, Integer.MAX_VALUE);
	}
}