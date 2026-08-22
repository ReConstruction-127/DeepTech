package dev.celestiacraft.deep_tech.config.common.machine.advanced;

import dev.celestiacraft.deep_tech.api.client.lang.ConfigLang;
import dev.celestiacraft.libs.config.api.ConfigModule;
import net.minecraftforge.common.ForgeConfigSpec;

public class ProcessorConfig extends ConfigModule {
	public static ForgeConfigSpec.IntValue MAX_ENERGY;
	public static ForgeConfigSpec.IntValue MAX_RECEIVE;

	public ProcessorConfig(ForgeConfigSpec.Builder builder) {
		super(builder, "processor", ConfigLang.addConfigKey("module.processor"));
	}

	@Override
	protected void addConfigs() {
		MAX_ENERGY = builder.translation(ConfigLang.addConfigKey("processor.max_energy_stored"))
				.comment("type: int")
				.comment("default: 50000")
				.defineInRange("processor_max_energy_stored", 50000, 1, Integer.MAX_VALUE);

		MAX_RECEIVE = builder.translation(ConfigLang.addConfigKey("processor.max_energy_receive"))
				.comment("type: int")
				.comment("default: 1000")
				.defineInRange("processor_max_energy_receive", 1000, 1, Integer.MAX_VALUE);
	}
}