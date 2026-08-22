package dev.celestiacraft.deep_tech.config.common;

import dev.celestiacraft.deep_tech.api.client.lang.ConfigLang;
import dev.celestiacraft.libs.config.api.ConfigModule;
import net.minecraftforge.common.ForgeConfigSpec;

public class OtherConfig extends ConfigModule {
	public static ForgeConfigSpec.BooleanValue ENABLE_SCULK_SHEARING;

	public OtherConfig(ForgeConfigSpec.Builder builder) {
		super(builder, "other", ConfigLang.addConfigKey("module.other"));
	}

	@Override
	protected void addConfigs() {
		ENABLE_SCULK_SHEARING = builder.translation(otherConfig("enable_sculk_shearing"))
				.comment("type: boolean")
				.comment("default: true")
				.define("enable_sculk_shearing", true);
	}

	private String otherConfig(String key) {
		return ConfigLang.addConfigTranslationKey("other." + key);
	}
}