package dev.celestiacraft.deep_tech.config.common.machine;

import dev.celestiacraft.libs.config.api.ConfigModule;
import net.minecraftforge.common.ForgeConfigSpec;

public class EnergyCellConfig extends ConfigModule {
	public static ForgeConfigSpec.IntValue MAX_ENERGY;
	public static ForgeConfigSpec.IntValue MAX_RECEIVE;
	public static ForgeConfigSpec.IntValue MAX_EXTRACT;
	public static ForgeConfigSpec.IntValue MAX_CHARGE;

	public EnergyCellConfig(ForgeConfigSpec.Builder builder) {
		super(builder, "energy_cell", "Energy Cell");
	}

	@Override
	protected void addConfigs() {
		MAX_ENERGY = builder.comment("Crusher's max energy stored")
				.comment("type: int")
				.comment("default: 1000000")
				.defineInRange("crusher_max_energy_stored", 1000000, 1, Integer.MAX_VALUE);

		MAX_RECEIVE = builder.comment("Crusher's max energy receive")
				.comment("type: int")
				.comment("default: 2147483647")
				.defineInRange("crusher_max_energy_receive", Integer.MAX_VALUE, 1, Integer.MAX_VALUE);

		MAX_EXTRACT = builder.comment("最大输出速率 (FE/tick)")
				.comment("type: int")
				.comment("default: 2147483647")
				.defineInRange("maxExtract", Integer.MAX_VALUE, 10, Integer.MAX_VALUE);

		MAX_CHARGE = builder
				.comment("每 tick 最大向物品充电量 (FE/t)")
				.comment("default: 2147483647")
				.defineInRange("maxCharge", Integer.MAX_VALUE, 1, Integer.MAX_VALUE);
	}
}