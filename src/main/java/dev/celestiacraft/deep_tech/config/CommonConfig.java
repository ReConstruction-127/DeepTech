package dev.celestiacraft.deep_tech.config;

import dev.celestiacraft.deep_tech.api.client.lang.ConfigLang;
import dev.celestiacraft.deep_tech.config.common.OtherConfig;
import dev.celestiacraft.deep_tech.config.common.machine.advanced.AssemblerConfig;
import dev.celestiacraft.deep_tech.config.common.machine.advanced.ProcessorConfig;
import dev.celestiacraft.deep_tech.config.common.machine.advanced.SculkCollectorConfig;
import dev.celestiacraft.deep_tech.config.common.machine.advanced.SculkNurseryConfig;
import dev.celestiacraft.deep_tech.config.common.machine.basic.AlloyFurnaceConfig;
import dev.celestiacraft.deep_tech.config.common.machine.basic.CrusherConfig;
import dev.celestiacraft.deep_tech.config.common.machine.basic.EXPGeneratorConfig;
import dev.celestiacraft.deep_tech.config.common.machine.basic.SculkFurnaceConfig;
import dev.celestiacraft.deep_tech.config.common.machine.other.EnergyCellConfig;
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

	public static final OtherConfig OTHER;

	static {
		BUILDER.translation(ConfigLang.addConfigTranslationKey("module.general"))
				.comment(ConfigLang.addConfigKey("general.comment"))
				.push("general");

		BUILDER.translation(ConfigLang.addConfigTranslationKey("module.crusher"));
		CRUSHER = new CrusherConfig(BUILDER);
		BUILDER.translation(ConfigLang.addConfigTranslationKey("module.sculk_furnace"));
		SCULK_FURNACE = new SculkFurnaceConfig(BUILDER);
		BUILDER.translation(ConfigLang.addConfigTranslationKey("module.exp_generator"));
		EXP_GENERATOR = new EXPGeneratorConfig(BUILDER);
		BUILDER.translation(ConfigLang.addConfigTranslationKey("module.energy_cell"));
		ENERGY_CELL = new EnergyCellConfig(BUILDER);
		BUILDER.translation(ConfigLang.addConfigTranslationKey("module.alloy_furnace"));
		ALLOY_FURNACE = new AlloyFurnaceConfig(BUILDER);
		BUILDER.translation(ConfigLang.addConfigTranslationKey("module.sculk_collector"));
		SCULK_COLLECTOR = new SculkCollectorConfig(BUILDER);
		BUILDER.translation(ConfigLang.addConfigTranslationKey("module.sculk_nursery"));
		SCULK_NURSERY = new SculkNurseryConfig(BUILDER);
		BUILDER.translation(ConfigLang.addConfigTranslationKey("module.processor"));
		PROCESSOR = new ProcessorConfig(BUILDER);
		BUILDER.translation(ConfigLang.addConfigTranslationKey("module.assembler"));
		ASSEMBLER = new AssemblerConfig(BUILDER);

		BUILDER.translation(ConfigLang.addConfigTranslationKey("module.other"));
		OTHER = new OtherConfig(BUILDER);

		SPEC = BUILDER.build();
		BUILDER.pop();
	}
}
