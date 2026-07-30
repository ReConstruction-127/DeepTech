package dev.celestiacraft.deep_tech.config.common.machine;

import net.minecraftforge.common.ForgeConfigSpec;

public class EXPGeneratorConfig {
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

	public static final ForgeConfigSpec.IntValue MAX_ENERGY = BUILDER
			.comment("最大能量存储 (FE)")
			.defineInRange("maxEnergy", 10000, 1000, 1000000);

	public static final ForgeConfigSpec.IntValue MAX_EXTRACT = BUILDER
			.comment("最大输出速率 (FE/tick)")
			.defineInRange("maxExtract", 50, 10, 10000);

	public static final ForgeConfigSpec.IntValue FLUID_CAPACITY = BUILDER
			.comment("液态经验存储容量 (mB)")
			.defineInRange("fluidCapacity", 1000, 100, 100000);

	public static final ForgeConfigSpec.IntValue EXP_TO_MB = BUILDER
			.comment("每点经验转化为多少 mB 液态经验")
			.defineInRange("expToMb", 10, 1, 100);

	public static final ForgeConfigSpec.IntValue PLAYER_EXP_PER_TICK = BUILDER
			.comment("玩家每 tick 被吸取的经验值")
			.defineInRange("playerExpPerTick", 1, 0, 10);

	public static final ForgeConfigSpec.IntValue MB_PER_TICK = BUILDER
			.comment("每 tick 消耗的液态经验 (mB)")
			.defineInRange("mbPerTick", 5, 1, 100);

	public static final ForgeConfigSpec.IntValue FE_PER_MB = BUILDER
			.comment("每 mB 液态经验产生的 FE")
			.defineInRange("fePerMb", 10, 1, 100);

	public static final ForgeConfigSpec SPEC = BUILDER.build();

	// ✅ 安全获取方法：配置未加载时返回默认值
	public static int getFluidCapacityOrDefault(int defaultValue) {
		try { return FLUID_CAPACITY.get(); } catch (IllegalStateException e) { return defaultValue; }
	}

	public static int getMaxEnergyOrDefault(int defaultValue) {
		try { return MAX_ENERGY.get(); } catch (IllegalStateException e) { return defaultValue; }
	}

	public static int getMaxExtractOrDefault(int defaultValue) {
		try { return MAX_EXTRACT.get(); } catch (IllegalStateException e) { return defaultValue; }
	}

	public static int getExpToMbOrDefault(int defaultValue) {
		try { return EXP_TO_MB.get(); } catch (IllegalStateException e) { return defaultValue; }
	}

	public static int getPlayerExpPerTickOrDefault(int defaultValue) {
		try { return PLAYER_EXP_PER_TICK.get(); } catch (IllegalStateException e) { return defaultValue; }
	}

	public static int getMbPerTickOrDefault(int defaultValue) {
		try { return MB_PER_TICK.get(); } catch (IllegalStateException e) { return defaultValue; }
	}

	public static int getFePerMbOrDefault(int defaultValue) {
		try { return FE_PER_MB.get(); } catch (IllegalStateException e) { return defaultValue; }
	}
}