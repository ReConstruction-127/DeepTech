package dev.celestiacraft.deep_tech.config.common.machine;

import dev.celestiacraft.deep_tech.datagen.language.LanguageGenerate;
import dev.celestiacraft.libs.config.api.ConfigModule;
import net.minecraftforge.common.ForgeConfigSpec;

public class EXPGeneratorConfig extends ConfigModule {
	public static ForgeConfigSpec.IntValue MAX_ENERGY;
	public static ForgeConfigSpec.IntValue MAX_EXTRACT;
	public static ForgeConfigSpec.IntValue FLUID_CAPACITY;
	public static ForgeConfigSpec.IntValue EXP_TO_MB;
	public static ForgeConfigSpec.IntValue PLAYER_EXP_PER_TICK;
	public static ForgeConfigSpec.IntValue MB_PER_TICK;
	public static ForgeConfigSpec.IntValue FE_PER_MB;

	public EXPGeneratorConfig(ForgeConfigSpec.Builder builder) {
		super(builder, "exp_generator", LanguageGenerate.configEnglish("module.exp_generator"));
	}

	@Override
	protected void addConfigs() {
		MAX_ENERGY = builder.comment(LanguageGenerate.configEnglish("exp_generator.max_energy_stored"))
				.comment("type: int")
				.comment("default: 10000")
				.defineInRange("maxEnergy", 10000, 1000, 1000000);

		MAX_EXTRACT = builder.comment(LanguageGenerate.configEnglish("exp_generator.max_extract"))
				.comment("type: int")
				.comment("default: 500")
				.defineInRange("maxExtract", 500, 10, 10000);

		FLUID_CAPACITY = builder.comment(LanguageGenerate.configEnglish("exp_generator.fluid_capacity"))
				.comment("type: int")
				.comment("default: 1000")
				.defineInRange("fluidCapacity", 1000, 100, 100000);

		EXP_TO_MB = builder.comment(LanguageGenerate.configEnglish("exp_generator.exp_to_mb"))
				.comment("type: int")
				.comment("default: 20")
				.defineInRange("expToMb", 20, 1, 100);

		PLAYER_EXP_PER_TICK = builder.comment(LanguageGenerate.configEnglish("exp_generator.player_exp_per_tick"))
				.comment("type: int")
				.comment("default: 1")
				.defineInRange("playerExpPerTick", 1, 0, 10);

		MB_PER_TICK = builder.comment(LanguageGenerate.configEnglish("exp_generator.mb_per_tick"))
				.comment("type: int")
				.comment("default: 1")
				.defineInRange("mbPerTick", 1, 1, 100);

		FE_PER_MB = builder.comment(LanguageGenerate.configEnglish("exp_generator.fe_per_mb"))
				.comment("type: int")
				.comment("default: 5000")
				.defineInRange("fePerMb", 5000, 1, 10000);
	}
}