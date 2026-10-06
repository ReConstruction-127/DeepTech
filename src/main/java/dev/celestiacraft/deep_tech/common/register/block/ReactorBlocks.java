package dev.celestiacraft.deep_tech.common.register.block;

import com.tterrag.registrate.util.entry.BlockEntry;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.common.block.machine.reactor.ReactorCasingBlock;
import dev.celestiacraft.deep_tech.common.block.machine.reactor.ReactorControllerBlock;
import dev.celestiacraft.deep_tech.common.block.machine.reactor.ReactorEnergyReceiverBlock;
import dev.celestiacraft.deep_tech.common.register.DTCreativeTabs;
import dev.celestiacraft.deep_tech.tags.DeepTechBlockTags;
import dev.celestiacraft.deep_tech.tags.DeepTechItemTags;

/**
 * 幽匿反应堆的三个方块: 反应堆外壳、反应堆控制器、反应堆能量接收器.
 * <p>
 * 方块状态与模型用 {@code src/main/resources} 下的静态 JSON(原版贴图占位),
 * 所以这里把 Registrate 的 datagen provider 传成空实现, 免得 runData 又生成一套把它覆盖掉。
 */
public class ReactorBlocks {
	public static final BlockEntry<ReactorCasingBlock> REACTOR_CASING;
	public static final BlockEntry<ReactorControllerBlock> REACTOR_CONTROLLER;
	public static final BlockEntry<ReactorEnergyReceiverBlock> REACTOR_ENERGY_RECEIVER;

	static {
		REACTOR_CASING = DeepTech.REGISTRATE.block("reactor_casing", ReactorCasingBlock::new)
				.blockstate((context, provider) -> {
				})
				.tag(DeepTechBlockTags.MACHINES)
				.item()
				.tab(DTCreativeTabs.MACHINE.getKey())
				.tag(DeepTechItemTags.MACHINES)
				.model((context, provider) -> {
				})
				.build()
				.register();

		REACTOR_CONTROLLER = DeepTech.REGISTRATE.block("reactor_controller", ReactorControllerBlock::new)
				.blockstate((context, provider) -> {
				})
				.tag(DeepTechBlockTags.MACHINES)
				.item()
				.tab(DTCreativeTabs.MACHINE.getKey())
				.tag(DeepTechItemTags.MACHINES)
				.model((context, provider) -> {
				})
				.build()
				.register();

		REACTOR_ENERGY_RECEIVER = DeepTech.REGISTRATE.block("reactor_energy_receiver", ReactorEnergyReceiverBlock::new)
				.blockstate((context, provider) -> {
				})
				.tag(DeepTechBlockTags.MACHINES)
				.item()
				.tab(DTCreativeTabs.MACHINE.getKey())
				.tag(DeepTechItemTags.MACHINES)
				.model((context, provider) -> {
				})
				.build()
				.register();
	}

	public static void register() {
		DeepTech.registerLog("Reactor Blocks");
	}
}
