package dev.celestiacraft.deep_tech.config.common.machine.basic;

import dev.celestiacraft.deep_tech.api.client.lang.ConfigLang;
import dev.celestiacraft.libs.config.api.ConfigModule;
import net.minecraftforge.common.ForgeConfigSpec;

public class CrusherConfig extends ConfigModule {
	public static ForgeConfigSpec.IntValue MAX_ENERGY;
	public static ForgeConfigSpec.IntValue MAX_RECEIVE;

	public CrusherConfig(ForgeConfigSpec.Builder builder) {
		super(builder, "crusher", ConfigLang.addConfigKey("module.crusher"));
	}

	@Override
	protected void addConfigs() {
		MAX_ENERGY = builder.translation(ConfigLang.addConfigKey("crusher.max_energy_stored"))
				.comment("type: int")
				.comment("default: 10000")
				.defineInRange("crusher_max_energy_stored", 10000, 1, Integer.MAX_VALUE);

		MAX_RECEIVE = builder.translation(ConfigLang.addConfigKey("crusher.max_energy_receive"))
				.comment("type: int")
				.comment("default: 100")
				.defineInRange("crusher_max_energy_receive", 100, 1, Integer.MAX_VALUE);
	}
}