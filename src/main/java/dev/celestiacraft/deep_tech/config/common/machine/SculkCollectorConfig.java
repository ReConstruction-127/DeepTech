package dev.celestiacraft.deep_tech.config.common.machine;

import dev.celestiacraft.libs.config.api.ConfigModule;
import net.minecraftforge.common.ForgeConfigSpec;

public class SculkCollectorConfig extends ConfigModule {
	public static ForgeConfigSpec.IntValue MAX_ENERGY;
	public static ForgeConfigSpec.IntValue MAX_RECEIVE;
	public static ForgeConfigSpec.IntValue ENERGY_PER_HARVEST;
	public static ForgeConfigSpec.IntValue HARVEST_SPEED;

	public SculkCollectorConfig(ForgeConfigSpec.Builder builder) {
		super(builder, "sculk_collector", "Sculk Collector");
	}

	@Override
	protected void addConfigs() {
		MAX_ENERGY = builder.comment("Sculk Collector's max energy stored")
				.comment("type: int")
				.comment("default: 10000")
				.defineInRange("sculk_collector_max_energy_stored", 10000, 1, Integer.MAX_VALUE);

		MAX_RECEIVE = builder.comment("Sculk Collector's max energy receive per tick")
				.comment("type: int")
				.comment("default: 500")
				.defineInRange("sculk_collector_max_energy_receive", 500, 1, Integer.MAX_VALUE);

		ENERGY_PER_HARVEST = builder.comment("Sculk Collector's energy cost per harvested block")
				.comment("type: int")
				.comment("default: 200")
				.defineInRange("sculk_collector_energy_per_harvest", 200, 1, Integer.MAX_VALUE);

		HARVEST_SPEED = builder.comment("Sculk Collector's harvest speed, blocks per tick")
				.comment("type: int")
				.comment("default: 1")
				.defineInRange("sculk_collector_harvest_speed", 1, 1, 1024);
	}
}