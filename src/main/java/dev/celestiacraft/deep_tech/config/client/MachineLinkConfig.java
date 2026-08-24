package dev.celestiacraft.deep_tech.config.client;

import dev.celestiacraft.deep_tech.api.client.lang.ConfigLang;
import dev.celestiacraft.libs.config.api.ConfigModule;
import net.minecraftforge.common.ForgeConfigSpec;

public class MachineLinkConfig extends ConfigModule {
	public static ForgeConfigSpec.BooleanValue ENABLED;
	public static ForgeConfigSpec.IntValue SCAN_RANGE;
	public static ForgeConfigSpec.IntValue MAX_LINKS;
	public static ForgeConfigSpec.DoubleValue BEAM_WIDTH;
	public static ForgeConfigSpec.DoubleValue OPACITY;
	public static ForgeConfigSpec.BooleanValue SHOW_THROUGH_BLOCKS;

	public MachineLinkConfig(ForgeConfigSpec.Builder builder) {
		super(builder, "machine_link", ConfigLang.addConfigKey("module.machine_link"));
	}

	@Override
	protected void addConfigs() {
		ENABLED = builder.translation(machineLink("enabled"))
				.comment("type: boolean")
				.comment("default: true")
				.define("machine_link_enabled", true);

		SCAN_RANGE = builder.translation(machineLink("scan_range"))
				.comment("type: int")
				.comment("default: 32")
				.defineInRange("machine_link_scan_range", 32, 8, 128);

		MAX_LINKS = builder.translation(machineLink("max_links"))
				.comment("type: int")
				.comment("default: 512")
				.defineInRange("machine_link_max_links", 512, 16, 4096);

		BEAM_WIDTH = builder.translation(machineLink("beam_width"))
				.comment("type: double")
				.comment("default: 0.035")
				.defineInRange("machine_link_beam_width", 0.035D, 0.005D, 0.25D);

		OPACITY = builder.translation(machineLink("opacity"))
				.comment("type: double")
				.comment("default: 0.85")
				.defineInRange("machine_link_opacity", 0.85D, 0.1D, 1.0D);

		SHOW_THROUGH_BLOCKS = builder.translation(machineLink("show_through_blocks"))
				.comment("type: boolean")
				.comment("default: true")
				.define("machine_link_show_through_blocks", true);
	}

	private String machineLink(String key) {
		return ConfigLang.addConfigTranslationKey("machine_link." + key);
	}
}