package dev.celestiacraft.deep_tech.tags;

import dev.celestiacraft.libs.tags.TagsBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class DeepTechBlockTags {
	public static final TagKey<Block>
			WRENCH_PICKUP,
			MACHINES,
			REACTOR_CASING,
			REACTOR_CAVITY,
			REACTOR_INERT;

	static {
		WRENCH_PICKUP = TagsBuilder.block("wrench_pickup").deepTech();
		MACHINES = TagsBuilder.block("machines").deepTech();
		// 幽匿反应堆外壳: 结构中标记为外壳的位置, 允许被扩展坞替换
		REACTOR_CASING = TagsBuilder.block("reactor_casing").deepTech();
		// 幽匿反应堆生长腔: 空气(未生长)与已长出的幽匿块都算合法
		REACTOR_CAVITY = TagsBuilder.block("reactor_cavity").deepTech();
		// 反应堆过载时不会被幽匿覆盖的"惰性"方块: 外壳、控制器、全部 bionic 机器
		REACTOR_INERT = TagsBuilder.block("reactor_inert").deepTech();
	}
}