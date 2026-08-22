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
		addOther();
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