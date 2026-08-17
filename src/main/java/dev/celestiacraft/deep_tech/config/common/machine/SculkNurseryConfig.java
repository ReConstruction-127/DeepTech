package dev.celestiacraft.deep_tech.config.common.machine;

import dev.celestiacraft.libs.config.api.ConfigModule;
import net.minecraftforge.common.ForgeConfigSpec;

public class SculkNurseryConfig extends ConfigModule {
	public static ForgeConfigSpec.IntValue MAX_ENERGY;
	public static ForgeConfigSpec.IntValue MAX_RECEIVE;
	public static ForgeConfigSpec.IntValue FLUID_CAPACITY;

	public SculkNurseryConfig(ForgeConfigSpec.Builder builder) {
		super(builder, "sculk_nursery", "Sculk Nursery");
	}

	@Override
	protected void addConfigs() {
		MAX_ENERGY = builder.comment("Sculk Nursery's max energy stored")
				.comment("type: int")
				.comment("default: 50000")
				.defineInRange("sculk_nursery_max_energy_stored", 50000, 1, Integer.MAX_VALUE);

		MAX_RECEIVE = builder.comment("Sculk Nursery's max energy receive per tick")
				.comment("type: int")
				.comment("default: 1000")
				.defineInRange("sculk_nursery_max_energy_receive", 1000, 1, Integer.MAX_VALUE);

		FLUID_CAPACITY = builder.comment("Sculk Nursery's per-tank fluid capacity (mB)")
				.comment("type: int")
				.comment("default: 8000")
				.defineInRange("sculk_nursery_fluid_capacity", 8000, 100, Integer.MAX_VALUE);
	}
}
