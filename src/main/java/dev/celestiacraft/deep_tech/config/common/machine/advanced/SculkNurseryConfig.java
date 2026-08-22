package dev.celestiacraft.deep_tech.config.common.machine.advanced;

import dev.celestiacraft.deep_tech.api.client.lang.ConfigLang;
import dev.celestiacraft.libs.config.api.ConfigModule;
import net.minecraftforge.common.ForgeConfigSpec;

public class SculkNurseryConfig extends ConfigModule {
	public static ForgeConfigSpec.IntValue MAX_ENERGY;
	public static ForgeConfigSpec.IntValue MAX_RECEIVE;
	public static ForgeConfigSpec.IntValue FLUID_CAPACITY;

	public SculkNurseryConfig(ForgeConfigSpec.Builder builder) {
		super(builder, "sculk_nursery", ConfigLang.addConfigKey("module.sculk_nursery"));
	}

	@Override
	protected void addConfigs() {
		MAX_ENERGY = builder.translation(ConfigLang.addConfigKey("sculk_nursery.max_energy_stored"))
				.comment("type: int")
				.comment("default: 50000")
				.defineInRange("sculk_nursery_max_energy_stored", 50000, 1, Integer.MAX_VALUE);

		MAX_RECEIVE = builder.translation(ConfigLang.addConfigKey("sculk_nursery.max_energy_receive"))
				.comment("type: int")
				.comment("default: 1000")
				.defineInRange("sculk_nursery_max_energy_receive", 1000, 1, Integer.MAX_VALUE);

		FLUID_CAPACITY = builder.translation(ConfigLang.addConfigKey("sculk_nursery.fluid_capacity"))
				.comment("type: int")
				.comment("default: 8000")
				.defineInRange("sculk_nursery_fluid_capacity", 8000, 100, Integer.MAX_VALUE);
	}
}