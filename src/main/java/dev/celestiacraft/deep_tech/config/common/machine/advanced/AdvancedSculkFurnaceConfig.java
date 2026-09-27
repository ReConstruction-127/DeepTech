package dev.celestiacraft.deep_tech.config.common.machine.advanced;

import dev.celestiacraft.deep_tech.api.client.lang.ConfigLang;
import dev.celestiacraft.libs.config.api.ConfigModule;
import net.minecraftforge.common.ForgeConfigSpec;

public class AdvancedSculkFurnaceConfig extends ConfigModule {
	public static ForgeConfigSpec.IntValue MAX_ENERGY;
	public static ForgeConfigSpec.IntValue MAX_RECEIVE;
	public static ForgeConfigSpec.IntValue ENERGY_PER_TICK;
	public static ForgeConfigSpec.IntValue PROCESS_TIME;
	public static ForgeConfigSpec.IntValue SPEED_MULTIPLIER;

	public AdvancedSculkFurnaceConfig(ForgeConfigSpec.Builder builder) {
		super(builder, "advanced_sculk_furnace", ConfigLang.addConfigKey("module.advanced_sculk_furnace"));
	}

	@Override
	protected void addConfigs() {
		MAX_ENERGY = builder.translation(ConfigLang.addConfigTranslationKey("advanced_sculk_furnace.max_energy_stored"))
				.comment("type: int")
				.comment("default: 100000")
				.defineInRange("advanced_sculk_furnace_max_energy_stored", 100000, 1, Integer.MAX_VALUE);

		MAX_RECEIVE = builder.translation(ConfigLang.addConfigTranslationKey("advanced_sculk_furnace.max_energy_receive"))
				.comment("type: int")
				.comment("default: 2000")
				.defineInRange("advanced_sculk_furnace_max_energy_receive", 2000, 1, Integer.MAX_VALUE);

		ENERGY_PER_TICK = builder.translation(ConfigLang.addConfigTranslationKey("advanced_sculk_furnace.energy_per_tick"))
				.comment("type: int")
				.comment("energy cost while working, per tick")
				.comment("default: 20")
				.defineInRange("advanced_sculk_furnace_energy_per_tick", 20, 1, Integer.MAX_VALUE);

		PROCESS_TIME = builder.translation(ConfigLang.addConfigTranslationKey("advanced_sculk_furnace.process_time"))
				.comment("type: int")
				.comment("base time in ticks to smelt one item, before the speed multiplier")
				.comment("default: 100")
				.defineInRange("advanced_sculk_furnace_process_time", 100, 1, Integer.MAX_VALUE);

		SPEED_MULTIPLIER = builder.translation(ConfigLang.addConfigTranslationKey("advanced_sculk_furnace.speed_multiplier"))
				.comment("type: int")
				.comment("how many times faster than the sculk furnace (2 = twice as fast)")
				.comment("default: 2")
				.defineInRange("advanced_sculk_furnace_speed_multiplier", 2, 1, Integer.MAX_VALUE);
	}
}
