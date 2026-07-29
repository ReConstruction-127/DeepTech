package dev.celestiacraft.deep_tech.config;

import net.minecraftforge.common.ForgeConfigSpec;

public abstract class ConfigModule {
	protected final ForgeConfigSpec.Builder builder;

	public ConfigModule(ForgeConfigSpec.Builder builder, String path, String comment) {
		this.builder = builder;

		builder.comment(comment + " Settings")
				.push(path);

		addConfigs();

		builder.pop();
	}

	protected abstract void addConfigs();
}