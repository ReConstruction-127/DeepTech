package dev.celestiacraft.deep_tech.config.common.machine.advanced;

import dev.celestiacraft.deep_tech.api.client.lang.ConfigLang;
import dev.celestiacraft.libs.config.api.ConfigModule;
import net.minecraftforge.common.ForgeConfigSpec;

public class AdvancedCrusherConfig extends ConfigModule {
	public static ForgeConfigSpec.IntValue MAX_ENERGY;
	public static ForgeConfigSpec.IntValue MAX_RECEIVE;
	public static ForgeConfigSpec.IntValue SPEED_MULTIPLIER;

	public AdvancedCrusherConfig(ForgeConfigSpec.Builder builder) {
		super(builder, "advanced_crusher", ConfigLang.addConfigKey("module.advanced_crusher"));
	}

	@Override
	protected void addConfigs() {
		MAX_ENERGY = builder.translation(ConfigLang.addConfigTranslationKey("advanced_crusher.max_energy_stored"))
				.comment("type: int")
				.comment("default: 100000")
				.defineInRange("advanced_crusher_max_energy_stored", 100000, 1, Integer.MAX_VALUE);

		MAX_RECEIVE = builder.translation(ConfigLang.addConfigTranslationKey("advanced_crusher.max_energy_receive"))
				.comment("type: int")
				.comment("default: 2000")
				.defineInRange("advanced_crusher_max_energy_receive", 2000, 1, Integer.MAX_VALUE);

		SPEED_MULTIPLIER = builder.translation(ConfigLang.addConfigTranslationKey("advanced_crusher.speed_multiplier"))
				.comment("type: int")
				.comment("how many times faster than the crusher (2 = twice as fast)")
				.comment("energy cost stays whatever the recipe says")
				.comment("default: 2")
				.defineInRange("advanced_crusher_speed_multiplier", 2, 1, Integer.MAX_VALUE);
	}
}
