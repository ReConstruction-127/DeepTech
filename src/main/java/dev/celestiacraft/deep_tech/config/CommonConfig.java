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
		BUILDER.comment(ConfigLang.addConfigKey("general.comment"))
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

		OTHER = new OtherConfig(BUILDER);

		SPEC = BUILDER.build();
		BUILDER.pop();
	}
}