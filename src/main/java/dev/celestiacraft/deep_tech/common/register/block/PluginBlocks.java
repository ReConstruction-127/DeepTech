package dev.celestiacraft.deep_tech.common.register.block;

import com.tterrag.registrate.util.entry.BlockEntry;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.api.client.model.ItemModelGen;
import dev.celestiacraft.deep_tech.common.block.machine.plugins.SpeedPluginBlock;
import dev.celestiacraft.deep_tech.common.register.DTCreativeTabs;

/**
 * 插件方块: 贴在机器旁边才生效的方块, 现在只有加速插件.
 * <p>
 * 不是机器(没有方块实体也不进 machines 标签), 所以单独一个注册类.
 */
public class PluginBlocks {
	public static final BlockEntry<SpeedPluginBlock> SPEED_PLUGIN;

	static {
		SPEED_PLUGIN = DeepTech.REGISTRATE.block("speed_plugin", SpeedPluginBlock::new)
				.blockstate(SpeedPluginBlock.genBlockState())
				.item()
				.tab(DTCreativeTabs.MACHINE.getKey())
				.model(ItemModelGen.withModel("block/machine/plugins/speed"))
				.build()
				.register();
	}

	public static void register() {
		DeepTech.registerLog("Plugin Blocks");
	}
}
