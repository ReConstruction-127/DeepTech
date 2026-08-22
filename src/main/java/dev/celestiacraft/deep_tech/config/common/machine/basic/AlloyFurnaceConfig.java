package dev.celestiacraft.deep_tech.config.common.machine.basic;

import dev.celestiacraft.deep_tech.api.client.lang.ConfigLang;
import dev.celestiacraft.libs.config.api.ConfigModule;
import net.minecraftforge.common.ForgeConfigSpec;

public class AlloyFurnaceConfig extends ConfigModule {
	public static ForgeConfigSpec.IntValue MAX_ENERGY;
	public static ForgeConfigSpec.IntValue MAX_RECEIVE;

	public AlloyFurnaceConfig(ForgeConfigSpec.Builder builder) {
		super(builder, "alloy_furnace", ConfigLang.addConfigKey("module.alloy_furnace"));
	}

	@Override
	protected void addConfigs() {
		MAX_ENERGY = builder.translation(ConfigLang.addConfigTranslationKey("alloy_furnace.max_energy_stored"))
				.comment("type: int")
				.comment("default: 50000")
				.defineInRange("alloy_furnace_max_energy_stored", 50000, 1, Integer.MAX_VALUE);

		MAX_RECEIVE = builder.translation(ConfigLang.addConfigTranslationKey("alloy_furnace.max_energy_receive"))
				.comment("type: int")
				.comment("default: 100")
				.defineInRange("alloy_furnace_max_energy_receive", 1000, 1, Integer.MAX_VALUE);
	}
}