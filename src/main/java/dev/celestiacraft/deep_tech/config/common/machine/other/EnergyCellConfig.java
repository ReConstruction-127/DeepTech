package dev.celestiacraft.deep_tech.config.common.machine.other;

import dev.celestiacraft.deep_tech.api.client.lang.ConfigLang;
import dev.celestiacraft.libs.config.api.ConfigModule;
import net.minecraftforge.common.ForgeConfigSpec;

public class EnergyCellConfig extends ConfigModule {
	public static ForgeConfigSpec.IntValue MAX_ENERGY;
	public static ForgeConfigSpec.IntValue MAX_RECEIVE;
	public static ForgeConfigSpec.IntValue MAX_EXTRACT;
	public static ForgeConfigSpec.IntValue MAX_CHARGE;

	public EnergyCellConfig(ForgeConfigSpec.Builder builder) {
		super(builder, "energy_cell", ConfigLang.addConfigKey("module.energy_cell"));
	}

	@Override
	protected void addConfigs() {
		MAX_ENERGY = builder.translation(ConfigLang.addConfigKey("energy_cell.max_energy_stored"))
				.comment("type: int")
				.comment("default: 1000000")
				.defineInRange("energy_cell_max_energy_stored", 1000000, 1, Integer.MAX_VALUE);

		MAX_RECEIVE = builder.translation(ConfigLang.addConfigKey("energy_cell.max_energy_receive"))
				.comment("type: int")
				.comment("default: 2147483647")
				.defineInRange("energy_cell_max_energy_receive", Integer.MAX_VALUE, 1, Integer.MAX_VALUE);

		MAX_EXTRACT = builder.translation(ConfigLang.addConfigKey("energy_cell.max_extract"))
				.comment("type: int")
				.comment("default: 2147483647")
				.defineInRange("energy_cell_max_extract", Integer.MAX_VALUE, 10, Integer.MAX_VALUE);

		MAX_CHARGE = builder.translation(ConfigLang.addConfigKey("energy_cell.max_charge"))
				.comment("default: 2147483647")
				.defineInRange("energy_cell_max_charge", Integer.MAX_VALUE, 1, Integer.MAX_VALUE);
	}
}