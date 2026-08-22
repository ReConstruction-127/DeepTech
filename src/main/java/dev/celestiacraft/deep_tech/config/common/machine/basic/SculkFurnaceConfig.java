package dev.celestiacraft.deep_tech.config.common.machine.basic;

import dev.celestiacraft.deep_tech.api.client.lang.ConfigLang;
import dev.celestiacraft.libs.config.api.ConfigModule;
import net.minecraftforge.common.ForgeConfigSpec;

public class SculkFurnaceConfig extends ConfigModule {
	public static ForgeConfigSpec.IntValue MAX_ENERGY;
	public static ForgeConfigSpec.IntValue MAX_RECEIVE;

	public SculkFurnaceConfig(ForgeConfigSpec.Builder builder) {
		super(builder, "sculk_furnace", ConfigLang.addConfigKey("module.sculk_furnace"));
	}

	@Override
	protected void addConfigs() {
		MAX_ENERGY = builder.translation(ConfigLang.addConfigTranslationKey("sculk_furnace.max_energy_stored"))
				.comment("type: int")
				.comment("default: 10000")
				.defineInRange("sculk_furnace_max_energy_stored", 10000, 1, Integer.MAX_VALUE);

		MAX_RECEIVE = builder.translation(ConfigLang.addConfigTranslationKey("sculk_furnace.max_energy_receive"))
				.comment("type: int")
				.comment("default: 100")
				.defineInRange("sculk_furnace_max_energy_receive", 1000, 1, Integer.MAX_VALUE);
	}
}