package dev.celestiacraft.deep_tech.config.common.machine;

import dev.celestiacraft.deep_tech.datagen.language.LanguageGenerate;
import dev.celestiacraft.libs.config.api.ConfigModule;
import net.minecraftforge.common.ForgeConfigSpec;

public class AssemblerConfig extends ConfigModule {
	public static ForgeConfigSpec.IntValue MAX_ENERGY;
	public static ForgeConfigSpec.IntValue MAX_RECEIVE;
	public static ForgeConfigSpec.IntValue FLUID_CAPACITY;

	public AssemblerConfig(ForgeConfigSpec.Builder builder) {
		super(builder, "assembler", LanguageGenerate.configEnglish("module.assembler"));
	}

	@Override
	protected void addConfigs() {
		MAX_ENERGY = builder.comment(LanguageGenerate.configEnglish("assembler.max_energy_stored"))
				.comment("type: int")
				.comment("default: 50000")
				.defineInRange("assembler_max_energy_stored", 50000, 1, Integer.MAX_VALUE);

		MAX_RECEIVE = builder.comment(LanguageGenerate.configEnglish("assembler.max_energy_receive"))
				.comment("type: int")
				.comment("default: 1000")
				.defineInRange("assembler_max_energy_receive", 1000, 1, Integer.MAX_VALUE);

		FLUID_CAPACITY = builder.comment(LanguageGenerate.configEnglish("assembler.fluid_capacity"))
				.comment("type: int")
				.comment("default: 8000")
				.defineInRange("assembler_fluid_capacity", 8000, 1000, Integer.MAX_VALUE);
	}
}