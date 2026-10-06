package dev.celestiacraft.deep_tech.datagen.language.type;

import dev.celestiacraft.deep_tech.datagen.language.LanguageGenerate;

public class ConfigLanguage extends LanguageGenerate {
	public static void addLang() {
		addConfigLang(
				"module.general",
				"General",
				"通用"
		);
		addConfigLang(
				"general.comment",
				"All settings below will only take effect after restarting the server or client.",
				"以下所有设置将在重启服务器或客户端后生效"
		);

		addCrusher();
		addSculkFurnace();
		addExpGenerator();
		addAlloyFurnace();
		addEnergyCell();
		addSculkCollector();
		addSculkNursery();
		addProcessor();
		addAssembler();
		addAdvancedSculkFurnace();
		addAdvancedCrusher();
		addReactor();
		addOther();
		addRender();
	}

	private static void addRender() {
		addConfigLang(
				"module.render",
				"Render",
				"渲染"
		);
		addConfigLang(
				"render.comment",
				"Client only visual settings.",
				"仅客户端生效的视觉设置"
		);
		addConfigLang(
				"module.machine_link",
				"Machine Link",
				"机器连接"
		);
		addConfigLang(
				"machine_link.enabled",
				"Show machine link lasers while holding a wrench",
				"手持扳手时显示机器连接激光"
		);
		addConfigLang(
				"machine_link.scan_range",
				"Machine scan range around the player (blocks)",
				"以玩家为中心的机器扫描范围 (格)"
		);
		addConfigLang(
				"machine_link.max_links",
				"Max laser lines rendered at once",
				"同时渲染的激光线条数量上限"
		);
		addConfigLang(
				"machine_link.beam_width",
				"Laser line width (blocks)",
				"激光线条宽度 (格)"
		);
		addConfigLang(
				"machine_link.opacity",
				"Laser opacity",
				"激光不透明度"
		);
		addConfigLang(
				"machine_link.show_through_blocks",
				"Show a faint laser outline through blocks",
				"透过方块显示微弱的激光轮廓"
		);
	}

	private static void addCrusher() {
		addConfigLang(
				"module.crusher",
				"Crusher",
				"粉碎机"
		);
		addConfigLang(
				"crusher.max_energy_stored",
				"Crusher's max energy stored",
				"粉碎机最大能量存储 (FE)"
		);
		addConfigLang(
				"crusher.max_energy_receive",
				"Crusher's max energy receive",
				"粉碎机最大能量接收速率 (FE/t)"
		);
	}

	private static void addSculkFurnace() {
		addConfigLang(
				"module.sculk_furnace",
				"Sculk Furnace",
				"幽匿电炉"
		);
		addConfigLang(
				"sculk_furnace.max_energy_stored",
				"Sculk Furnace's max energy stored",
				"幽匿电炉最大能量存储 (FE)"
		);
		addConfigLang(
				"sculk_furnace.max_energy_receive",
				"Sculk Furnace's max energy receive",
				"幽匿电炉最大能量接收速率 (FE/t)"
		);
	}

	private static void addExpGenerator() {
		addConfigLang(
				"module.exp_generator",
				"Exp Generator",
				"经验发电机"
		);
		addConfigLang(
				"exp_generator.max_energy_stored",
				"Max energy stored (FE)",
				"最大能量存储 (FE)"
		);
		addConfigLang(
				"exp_generator.max_extract",
				"Max output rate (FE/tick)",
				"最大输出速率 (FE/tick)"
		);
		addConfigLang(
				"exp_generator.fluid_capacity",
				"Liquid XP storage capacity (mB)",
				"液态经验存储容量 (mB)"
		);
		addConfigLang(
				"exp_generator.exp_to_mb",
				"How many mB of liquid XP per XP point",
				"每点经验转化为多少 mB 液态经验"
		);
		addConfigLang(
				"exp_generator.player_exp_per_tick",
				"Player XP drained per tick",
				"玩家每 tick 被吸取的经验值"
		);
		addConfigLang(
				"exp_generator.mb_per_tick",
				"Liquid XP consumed per tick (mB)",
				"每 tick 消耗的液态经验 (mB)"
		);
		addConfigLang(
				"exp_generator.fe_per_mb",
				"FE produced per mB of liquid XP",
				"每 mB 液态经验产生的 FE"
		);
	}

	private static void addAlloyFurnace() {
		addConfigLang(
				"module.alloy_furnace",
				"Alloy Furnace",
				"合金炉"
		);
		addConfigLang(
				"alloy_furnace.max_energy_stored",
				"Alloy Furnace's max energy stored",
				"合金炉最大能量存储 (FE)"
		);
		addConfigLang(
				"alloy_furnace.max_energy_receive",
				"Alloy Furnace's max energy receive",
				"合金炉最大能量接收速率 (FE/t)"
		);
	}

	private static void addEnergyCell() {
		addConfigLang(
				"module.energy_cell",
				"Energy Cell",
				"能量单元"
		);
		addConfigLang(
				"energy_cell.max_energy_stored",
				"Max energy stored (FE)",
				"最大能量存储 (FE)"
		);
		addConfigLang(
				"energy_cell.max_energy_receive",
				"Max energy receive rate (FE/t)",
				"最大接收速率 (FE/t)"
		);
		addConfigLang(
				"energy_cell.max_extract",
				"Max output rate (FE/tick)",
				"最大输出速率 (FE/tick)"
		);
		addConfigLang(
				"energy_cell.max_charge",
				"Max item charging rate per tick (FE/t)",
				"每 tick 最大向物品充电量 (FE/t)"
		);
	}

	private static void addSculkCollector() {
		addConfigLang(
				"module.sculk_collector",
				"Sculk Collector",
				"幽匿采集器"
		);
		addConfigLang(
				"sculk_collector.max_energy_stored",
				"Sculk Collector's max energy stored",
				"幽匿采集器最大能量存储 (FE)"
		);
		addConfigLang(
				"sculk_collector.max_energy_receive",
				"Sculk Collector's max energy receive per tick",
				"幽匿采集器最大能量接收速率 (FE/t)"
		);
		addConfigLang(
				"sculk_collector.energy_per_harvest",
				"Sculk Collector's energy cost per harvested block",
				"幽匿采集器每收获一个方块的能耗"
		);
		addConfigLang(
				"sculk_collector.harvest_speed",
				"Sculk Collector's harvest speed, blocks per tick",
				"幽匿采集器收获速度 (方块/tick)"
		);
	}

	private static void addSculkNursery() {
		addConfigLang(
				"module.sculk_nursery",
				"Sculk Nursery",
				"幽匿培育室"
		);
		addConfigLang(
				"sculk_nursery.max_energy_stored",
				"Sculk Nursery's max energy stored",
				"幽匿培育室最大能量存储 (FE)"
		);
		addConfigLang(
				"sculk_nursery.max_energy_receive",
				"Sculk Nursery's max energy receive per tick",
				"幽匿培育室最大能量接收速率 (FE/t)"
		);
		addConfigLang(
				"sculk_nursery.fluid_capacity",
				"Sculk Nursery's per-tank fluid capacity (mB)",
				"幽匿培育室每个储罐容量 (mB)"
		);
	}

	private static void addProcessor() {
		addConfigLang(
				"module.processor",
				"Processor",
				"加工机"
		);
		addConfigLang(
				"processor.max_energy_stored",
				"Processor's max energy stored",
				"加工机最大能量存储 (FE)"
		);
		addConfigLang(
				"processor.max_energy_receive",
				"Processor's max energy receive",
				"加工机最大能量接收速率 (FE/t)"
		);
	}

	private static void addAssembler() {
		addConfigLang(
				"module.assembler",
				"Assembler",
				"组装机"
		);
		addConfigLang(
				"assembler.max_energy_stored",
				"Assembler's max energy stored",
				"组装机最大能量存储 (FE)"
		);
		addConfigLang(
				"assembler.max_energy_receive",
				"Assembler's max energy receive",
				"组装机最大能量接收速率 (FE/t)"
		);
		addConfigLang(
				"assembler.fluid_capacity",
				"Assembler's fluid tank capacity (mB)",
				"组装机液体储罐容量 (mB)"
		);
	}
	private static void addAdvancedSculkFurnace() {
		addConfigLang(
				"module.advanced_sculk_furnace",
				"Advanced Sculk Furnace",
				"高级幽匿电炉"
		);
		addConfigLang(
				"advanced_sculk_furnace.max_energy_stored",
				"Advanced Sculk Furnace's max energy stored",
				"高级幽匿电炉最大能量存储 (FE)"
		);
		addConfigLang(
				"advanced_sculk_furnace.max_energy_receive",
				"Advanced Sculk Furnace's max energy receive",
				"高级幽匿电炉最大能量接收速率 (FE/t)"
		);
		addConfigLang(
				"advanced_sculk_furnace.energy_per_tick",
				"Energy consumed per tick for each working parallel lane",
				"工作时每条并行线每 tick 消耗的能量 (FE/t)"
		);
		addConfigLang(
				"advanced_sculk_furnace.process_time",
				"Base time in ticks to smelt one item (before the speed multiplier)",
				"熔炼一个物品的基准时间 (tick, 未乘速度倍率)"
		);
		addConfigLang(
				"advanced_sculk_furnace.speed_multiplier",
				"How many times faster than the sculk furnace",
				"相对普通幽匿电炉的速度倍率"
		);
	}

	private static void addAdvancedCrusher() {
		addConfigLang(
				"module.advanced_crusher",
				"Advanced Crusher",
				"高级粉碎机"
		);
		addConfigLang(
				"advanced_crusher.max_energy_stored",
				"Advanced Crusher's max energy stored",
				"高级粉碎机最大能量存储 (FE)"
		);
		addConfigLang(
				"advanced_crusher.max_energy_receive",
				"Advanced Crusher's max energy receive",
				"高级粉碎机最大能量接收速率 (FE/t)"
		);
		addConfigLang(
				"advanced_crusher.speed_multiplier",
				"How many times faster than the crusher",
				"相对普通粉碎机的速度倍率"
		);
	}

	private static void addReactor() {
		addConfigLang(
				"module.sculk_reactor",
				"Sculk Reactor",
				"幽匿反应堆"
		);
		addConfigLang(
				"sculk_reactor.max_energy_stored",
				"Reactor Controller's max energy stored (FE)",
				"反应堆控制器最大能量存储 (FE)"
		);
		addConfigLang(
				"sculk_reactor.max_energy_receive",
				"Reactor Controller's max energy receive rate (FE/t)",
				"反应堆控制器最大能量接收速率 (FE/t)"
		);
		addConfigLang(
				"sculk_reactor.mb_per_sculk",
				"Sculk culture consumed per sculk block grown (mB)",
				"每生成一个幽匿块消耗的幽匿培养液 (mB)"
		);
		addConfigLang(
				"sculk_reactor.grow_interval",
				"Ticks between growth attempts (5 = four times per second)",
				"生长尝试间隔 (tick, 5 = 每秒四次)"
		);
		addConfigLang(
				"sculk_reactor.explosion_power",
				"Power of each of the two final explosions (4 = TNT)",
				"失控结束时两次爆炸的威力 (4 = TNT)"
		);
		addConfigLang(
				"sculk_reactor.energy_per_sculk",
				"FE gained by the energy receiver per sculk block",
				"能量接收器每破坏一个幽匿块获得的 FE"
		);
		addConfigLang(
				"sculk_reactor.receiver_radius",
				"Max distance between the energy receiver and the reactor centre (blocks)",
				"能量接收器与反应堆中心的最大距离 (格)"
		);
		addConfigLang(
				"sculk_reactor.receiver_max_energy",
				"Energy receiver's internal energy buffer (FE)",
				"能量接收器内部能量缓存 (FE)"
		);
		addConfigLang(
				"sculk_reactor.receiver_max_extract",
				"Energy receiver's max output rate (FE/t)",
				"能量接收器最大输出速率 (FE/t)"
		);
		addConfigLang(
				"sculk_reactor.sculk_per_cycle_min",
				"Min sculk blocks placed per growth cycle",
				"每个生长周期最少放置几个幽匿块"
		);
		addConfigLang(
				"sculk_reactor.sculk_per_cycle_max",
				"Max sculk blocks placed per growth cycle",
				"每个生长周期最多放置几个幽匿块"
		);
		addConfigLang(
				"sculk_reactor.spread_min",
				"Min blocks covered per peripheral spread",
				"外围蔓延时每次最少覆盖几格"
		);
		addConfigLang(
				"sculk_reactor.spread_max",
				"Max blocks covered per peripheral spread",
				"外围蔓延时每次最多覆盖几格"
		);
		addConfigLang(
				"sculk_reactor.spread_operations",
				"Peripheral spreads before the dispersal phase starts",
				"进入扩散阶段前的外围蔓延次数"
		);
		addConfigLang(
				"sculk_reactor.spread_interval",
				"Ticks between peripheral spreads (20 = once per second)",
				"外围蔓延间隔 (tick, 20 = 每秒一次)"
		);
		addConfigLang(
				"sculk_reactor.spread_radius",
				"Peripheral spread radius (blocks)",
				"外围蔓延半径 (格)"
		);
		addConfigLang(
				"sculk_reactor.dispersal_step_limit",
				"Max blocks dispersed per step (spreads the work out to avoid lag)",
				"每步最多蔓延几格 (把单步的量摊开, 避免卡顿)"
		);
		addConfigLang(
				"sculk_reactor.dispersal_interval",
				"Ticks between each contiguous dispersal step",
				"连续扩散每多少 tick 蔓延一步"
		);
		addConfigLang(
				"sculk_reactor.cloud_chance",
				"Chance (percent, decimals allowed) for a sculk vein to spawn an infection effect cloud",
				"幽匿脉络生成感染效果云的概率 (百分比, 可填小数)"
		);
		addConfigLang(
				"sculk_reactor.cloud_duration",
				"Effect cloud lifetime in ticks (6000 = 5 minutes)",
				"效果云存在时长 (tick, 6000 = 5 分钟)"
		);
		addConfigLang(
				"sculk_reactor.infection_duration",
				"Duration of the infection effect applied by the clouds (ticks)",
				"效果云施加的感染效果持续时间 (tick)"
		);
	}

	private static void addOther() {
		addConfigLang(
				"module.other",
				"Other",
				"其他"
		);
		addConfigLang(
				"other.enable_sculk_shearing",
				"Enable sculk shearing with shears",
				"启用剪刀剪切幽匿方块"
		);
	}
}