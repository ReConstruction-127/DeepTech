package dev.celestiacraft.deep_tech.common.block.machine.collector;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.deep_tech.api.block.machine.MachineBlock;
import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.model.generators.BlockModelBuilder;

public class SculkCollectorBlock extends MachineBlock<SculkCollectorBlockEntity> {
	public SculkCollectorBlock(Properties properties) {
		super(properties);
	}

	@Override
	public BlockEntityType<SculkCollectorBlockEntity> getBlockEntityType() {
		return DTBlockEntities.SCULK_COLLECTOR.get();
	}

	@Override
	public Class<SculkCollectorBlockEntity> getBlockEntityClass() {
		return SculkCollectorBlockEntity.class;
	}

	/**
	 * 模型暂复用熔炉纹理(待补专有纹理): orientableWithBottom 的 off/on 两个模型
	 */
	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> genBlockState() {
		return (context, provider) -> {
			BlockModelBuilder modelOff = provider.models().orientableWithBottom(
					"block/machine/sculk_collector/off",
					provider.modLoc("block/machine/furnace/side_off"),
					provider.modLoc("block/machine/furnace/face_off"),
					provider.modLoc("block/machine/furnace/bottom"),
					provider.modLoc("block/machine/furnace/top_off")
			);
			BlockModelBuilder modelOn = provider.models().orientableWithBottom(
					"block/machine/sculk_collector/on",
					provider.modLoc("block/machine/furnace/side_on"),
					provider.modLoc("block/machine/furnace/face_on"),
					provider.modLoc("block/machine/furnace/bottom"),
					provider.modLoc("block/machine/furnace/top_on")
			);
			horizontalLitBlock(provider, context.get(), modelOff, modelOn);
		};
	}
}