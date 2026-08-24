package dev.celestiacraft.deep_tech.config;

import dev.celestiacraft.deep_tech.api.client.lang.ConfigLang;
import dev.celestiacraft.deep_tech.config.client.MachineLinkConfig;
import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec SPEC;
	public static final MachineLinkConfig MACHINE_LINK;

	static {
		BUILDER.translation(ConfigLang.addConfigTranslationKey("module.render"))
				.comment(ConfigLang.addConfigKey("render.comment"))
				.push("render");

		BUILDER.translation(ConfigLang.addConfigTranslationKey("module.machine_link"));
		MACHINE_LINK = new MachineLinkConfig(BUILDER);

		SPEC = BUILDER.build();
		BUILDER.pop();
	}
}