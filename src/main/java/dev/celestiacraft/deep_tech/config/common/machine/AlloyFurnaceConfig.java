package dev.celestiacraft.deep_tech.config.common.machine;

import dev.celestiacraft.libs.config.api.ConfigModule;
import net.minecraftforge.common.ForgeConfigSpec;

public class AlloyFurnaceConfig extends ConfigModule {
	public static ForgeConfigSpec.IntValue MAX_ENERGY;
	public static ForgeConfigSpec.IntValue MAX_RECEIVE;

	public AlloyFurnaceConfig(ForgeConfigSpec.Builder builder) {
		super(builder, "alloy_furnace", "Alloy Furnace");
	}

	@Override
	protected void addConfigs() {
		MAX_ENERGY = builder.comment("Alloy Furnace's max energy stored")
				.comment("type: int")
				.comment("default: 50000")
				.defineInRange("sculk_furnace_max_energy_stored", 50000, 1, Integer.MAX_VALUE);

		MAX_RECEIVE = builder.comment("Alloy Furnace's max energy receive")
				.comment("type: int")
				.comment("default: 100")
				.defineInRange("sculk_furnace_max_energy_receive", 1000, 1, Integer.MAX_VALUE);
	}
}