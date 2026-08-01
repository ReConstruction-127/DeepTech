package dev.celestiacraft.deep_tech.config.common.machine;

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
		super(builder, "exp_generator", "Exp Generator");
	}

	@Override
	protected void addConfigs() {
		MAX_ENERGY = builder.comment("最大能量存储 (FE)")
				.comment("type: int")
				.comment("default: 10000")
				.defineInRange("maxEnergy", 10000, 1000, 1000000);

		MAX_EXTRACT = builder.comment("最大输出速率 (FE/tick)")
				.comment("type: int")
				.comment("default: 50")
				.defineInRange("maxExtract", 50, 10, 10000);

		FLUID_CAPACITY = builder.comment("液态经验存储容量 (mB)")
				.comment("type: int")
				.comment("default: 1000")
				.defineInRange("fluidCapacity", 1000, 100, 100000);

		EXP_TO_MB = builder.comment("每点经验转化为多少 mB 液态经验")
				.comment("type: int")
				.comment("default: 20")
				.defineInRange("expToMb", 20, 1, 100);

		PLAYER_EXP_PER_TICK = builder.comment("玩家每 tick 被吸取的经验值")
				.comment("type: int")
				.comment("default: 1")
				.defineInRange("playerExpPerTick", 1, 0, 10);

		MB_PER_TICK = builder.comment("每 tick 消耗的液态经验 (mB)")
				.comment("type: int")
				.comment("default: 1")
				.defineInRange("mbPerTick", 1, 1, 100);

		FE_PER_MB = builder.comment("每 mB 液态经验产生的 FE")
				.comment("type: int")
				.comment("default: 2000")
				.defineInRange("fePerMb", 2000, 1, 10000);
	}
}