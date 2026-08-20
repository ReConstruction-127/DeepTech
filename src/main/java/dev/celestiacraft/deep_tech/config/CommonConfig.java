package dev.celestiacraft.deep_tech.config;

import dev.celestiacraft.deep_tech.config.common.machine.*;
import dev.celestiacraft.deep_tech.datagen.language.LanguageGenerate;
import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig {
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec SPEC;

	public static final CrusherConfig CRUSHER;
	public static final SculkFurnaceConfig SCULK_FURNACE;
	public static final EXPGeneratorConfig EXP_GENERATOR;
	public static final AlloyFurnaceConfig ALLOY_FURNACE;
	public static final EnergyCellConfig ENERGY_CELL;
	public static final SculkCollectorConfig SCULK_COLLECTOR;
	public static final SculkNurseryConfig SCULK_NURSERY;
	public static final ProcessorConfig PROCESSOR;
	public static final AssemblerConfig ASSEMBLER;

	static {
		BUILDER.comment(LanguageGenerate.configEnglish("general.comment"))
				.push("general");

		CRUSHER = new CrusherConfig(BUILDER);
		SCULK_FURNACE = new SculkFurnaceConfig(BUILDER);
		EXP_GENERATOR = new EXPGeneratorConfig(BUILDER);
		ENERGY_CELL = new EnergyCellConfig(BUILDER);
		ALLOY_FURNACE = new AlloyFurnaceConfig(BUILDER);
		SCULK_COLLECTOR = new SculkCollectorConfig(BUILDER);
		SCULK_NURSERY = new SculkNurseryConfig(BUILDER);
		PROCESSOR = new ProcessorConfig(BUILDER);
		ASSEMBLER = new AssemblerConfig(BUILDER);

		SPEC = BUILDER.build();
		BUILDER.pop();
	}
}