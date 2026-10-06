package dev.celestiacraft.deep_tech.config.common.machine.advanced;

import dev.celestiacraft.deep_tech.api.client.lang.ConfigLang;
import dev.celestiacraft.libs.config.api.ConfigModule;
import net.minecraftforge.common.ForgeConfigSpec;

/**
 * 幽匿反应堆相关配置.
 */
public class ReactorConfig extends ConfigModule {
	/** 控制器内部能量缓存(控制器本身只用来看结构, 不产电, 这里给个缓存是为了 capability 完整) */
	public static ForgeConfigSpec.IntValue MAX_ENERGY;
	public static ForgeConfigSpec.IntValue MAX_RECEIVE;

	/** 每生成一个幽匿块消耗的幽匿培养液(mB) */
	public static ForgeConfigSpec.IntValue MB_PER_SCULK;
	/** 生长尝试间隔(tick), 5 tick = 0.25 秒 */
	public static ForgeConfigSpec.IntValue GROW_INTERVAL;
	/** 失控结束时两次爆炸的威力, 4 与 TNT 一致 */
	public static ForgeConfigSpec.IntValue EXPLOSION_POWER;

	/** 每个生长周期随机放置的幽匿块数量下限 / 上限 */
	public static ForgeConfigSpec.IntValue SCULK_PER_CYCLE_MIN;
	public static ForgeConfigSpec.IntValue SCULK_PER_CYCLE_MAX;
	/** 外围蔓延: 每次覆盖数量下限 / 上限, 以及总次数 */
	public static ForgeConfigSpec.IntValue SPREAD_MIN;
	public static ForgeConfigSpec.IntValue SPREAD_MAX;
	public static ForgeConfigSpec.IntValue SPREAD_OPERATIONS;
	/** 外围蔓延的间隔(tick)与最大半径 */
	public static ForgeConfigSpec.IntValue SPREAD_INTERVAL;
	public static ForgeConfigSpec.IntValue SPREAD_RADIUS;
	/** 扩散阶段: 每多少 tick 蔓延一步, 以及每步最多处理几格(防止一次铺太多卡顿) */
	public static ForgeConfigSpec.IntValue DISPERSAL_INTERVAL;
	public static ForgeConfigSpec.IntValue DISPERSAL_STEP_LIMIT;

	/** 能量接收器: 每破坏一个幽匿块获得的 FE */
	public static ForgeConfigSpec.IntValue ENERGY_PER_SCULK;
	/** 能量接收器: 可放置半径(以反应堆中心算) */
	public static ForgeConfigSpec.IntValue RECEIVER_RADIUS;
	/** 能量接收器: 内部能量缓存 */
	public static ForgeConfigSpec.IntValue RECEIVER_MAX_ENERGY;
	/** 能量接收器: 对外输出速率 */
	public static ForgeConfigSpec.IntValue RECEIVER_MAX_EXTRACT;

	public ReactorConfig(ForgeConfigSpec.Builder builder) {
		super(builder, "sculk_reactor", ConfigLang.addConfigKey("module.sculk_reactor"));
	}

	@Override
	protected void addConfigs() {
		MAX_ENERGY = builder.translation(ConfigLang.addConfigTranslationKey("sculk_reactor.max_energy_stored"))
				.comment("type: int")
				.comment("default: 100000")
				.defineInRange("sculk_reactor_max_energy_stored", 100000, 0, Integer.MAX_VALUE);

		MAX_RECEIVE = builder.translation(ConfigLang.addConfigTranslationKey("sculk_reactor.max_energy_receive"))
				.comment("type: int")
				.comment("default: 10000")
				.defineInRange("sculk_reactor_max_energy_receive", 10000, 0, Integer.MAX_VALUE);

		MB_PER_SCULK = builder.translation(ConfigLang.addConfigTranslationKey("sculk_reactor.mb_per_sculk"))
				.comment("type: int")
				.comment("每生成一个幽匿块消耗的幽匿培养液(mB)")
				.comment("default: 100")
				.defineInRange("sculk_reactor_mb_per_sculk", 100, 1, Integer.MAX_VALUE);

		GROW_INTERVAL = builder.translation(ConfigLang.addConfigTranslationKey("sculk_reactor.grow_interval"))
				.comment("type: int")
				.comment("生长尝试间隔(tick), 5 = 每 0.25 秒一次")
				.comment("default: 5")
				.defineInRange("sculk_reactor_grow_interval", 5, 1, 72000);

		EXPLOSION_POWER = builder.translation(ConfigLang.addConfigTranslationKey("sculk_reactor.explosion_power"))
				.comment("type: int")
				.comment("失控结束时两次爆炸的威力, 4 = TNT")
				.comment("default: 4")
				.defineInRange("sculk_reactor_explosion_power", 4, 0, 100);

		ENERGY_PER_SCULK = builder.translation(ConfigLang.addConfigTranslationKey("sculk_reactor.energy_per_sculk"))
				.comment("type: int")
				.comment("接收器每破坏一个幽匿块获得的 FE")
				.comment("default: 5000")
				.defineInRange("sculk_reactor_energy_per_sculk", 5000, 1, Integer.MAX_VALUE);

		RECEIVER_RADIUS = builder.translation(ConfigLang.addConfigTranslationKey("sculk_reactor.receiver_radius"))
				.comment("type: int")
				.comment("接收器与反应堆中心的最大距离(格)")
				.comment("default: 5")
				.defineInRange("sculk_reactor_receiver_radius", 5, 1, 64);

		RECEIVER_MAX_ENERGY = builder.translation(ConfigLang.addConfigTranslationKey("sculk_reactor.receiver_max_energy"))
				.comment("type: int")
				.comment("default: 1000000")
				.defineInRange("sculk_reactor_receiver_max_energy", 1000000, 1, Integer.MAX_VALUE);

		RECEIVER_MAX_EXTRACT = builder.translation(ConfigLang.addConfigTranslationKey("sculk_reactor.receiver_max_extract"))
				.comment("type: int")
				.comment("default: 5000")
				.defineInRange("sculk_reactor_receiver_max_extract", 5000, 0, Integer.MAX_VALUE);

		SCULK_PER_CYCLE_MIN = builder.translation(ConfigLang.addConfigTranslationKey("sculk_reactor.sculk_per_cycle_min"))
				.comment("type: int")
				.comment("每个生长周期最少放置几个幽匿块")
				.comment("default: 1")
				.defineInRange("sculk_reactor_sculk_per_cycle_min", 1, 1, 64);

		SCULK_PER_CYCLE_MAX = builder.translation(ConfigLang.addConfigTranslationKey("sculk_reactor.sculk_per_cycle_max"))
				.comment("type: int")
				.comment("每个生长周期最多放置几个幽匿块")
				.comment("default: 3")
				.defineInRange("sculk_reactor_sculk_per_cycle_max", 3, 1, 64);

		SPREAD_MIN = builder.translation(ConfigLang.addConfigTranslationKey("sculk_reactor.spread_min"))
				.comment("type: int")
				.comment("外围蔓延时每次最少覆盖几格")
				.comment("default: 3")
				.defineInRange("sculk_reactor_spread_min", 3, 1, 64);

		SPREAD_MAX = builder.translation(ConfigLang.addConfigTranslationKey("sculk_reactor.spread_max"))
				.comment("type: int")
				.comment("外围蔓延时每次最多覆盖几格")
				.comment("default: 5")
				.defineInRange("sculk_reactor_spread_max", 5, 1, 64);

		SPREAD_OPERATIONS = builder.translation(ConfigLang.addConfigTranslationKey("sculk_reactor.spread_operations"))
				.comment("type: int")
				.comment("外围蔓延总次数, 完成后进入扩散阶段")
				.comment("default: 20")
				.defineInRange("sculk_reactor_spread_operations", 20, 1, 10000);

		SPREAD_INTERVAL = builder.translation(ConfigLang.addConfigTranslationKey("sculk_reactor.spread_interval"))
				.comment("type: int")
				.comment("外围蔓延间隔(tick), 20 = 每秒一次")
				.comment("default: 20")
				.defineInRange("sculk_reactor_spread_interval", 20, 1, 72000);

		SPREAD_RADIUS = builder.translation(ConfigLang.addConfigTranslationKey("sculk_reactor.spread_radius"))
				.comment("type: int")
				.comment("外围蔓延的最大半径(格)")
				.comment("default: 6")
				.defineInRange("sculk_reactor_spread_radius", 6, 3, 64);

		DISPERSAL_INTERVAL = builder.translation(ConfigLang.addConfigTranslationKey("sculk_reactor.dispersal_interval"))
				.comment("type: int")
				.comment("连续扩散每多少 tick 蔓延一步, 10 = 每半秒一步")
				.comment("default: 10")
				.defineInRange("sculk_reactor_dispersal_interval", 10, 1, 72000);

		DISPERSAL_STEP_LIMIT = builder.translation(ConfigLang.addConfigTranslationKey("sculk_reactor.dispersal_step_limit"))
				.comment("type: int")
				.comment("每步最多蔓延几格. 洪水没有边界, 只受惰性方块与不可破坏方块阻挡, 用这个值把单步的量摊开免得卡顿")
				.comment("default: 512")
				.defineInRange("sculk_reactor_dispersal_step_limit", 512, 1, 65536);
	}
}
