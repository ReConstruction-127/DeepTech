package dev.celestiacraft.deep_tech.common.block.machine.collector;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.deep_tech.api.block.machine.MachineBlock;
import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import dev.celestiacraft.libs.api.register.block.BasicBlock;
import dev.celestiacraft.libs.api.register.block.BlockFacing;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ConfiguredModel;

public class SculkCollectorBlock extends MachineBlock<SculkCollectorBlockEntity> {
	public SculkCollectorBlock(Properties properties) {
		super(properties.sound(SoundType.NETHERITE_BLOCK));
	}

	@Override
	public BlockEntityType<SculkCollectorBlockEntity> getBlockEntityType() {
		return DTBlockEntities.SCULK_COLLECTOR.get();
	}

	@Override
	public Class<SculkCollectorBlockEntity> getBlockEntityClass() {
		return SculkCollectorBlockEntity.class;
	}

	/** 采集器无方向: 不需要 face 朝向属性, 也不旋转模型(无对应转向贴图资源) */
	@Override
	protected BlockFacing useFacingType() {
		return BlockFacing.NONE;
	}

	/**
	 * 占位模型(待换专有纹理): 六面同构无朝向, 仅按 LIT 切换 off/on 模型
	 */
	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> genBlockState() {
		return (context, provider) -> {
			BlockModelBuilder modelOff = cubeModel(provider, "off");
			BlockModelBuilder modelOn = cubeModel(provider, "on");
			provider.getVariantBuilder(context.get())
					.forAllStates((state) -> ConfiguredModel.builder()
							.modelFile(state.getValue(BasicBlock.LIT) ? modelOn : modelOff)
							.build());
		};
	}

	private static BlockModelBuilder cubeModel(RegistrateBlockstateProvider provider, String state) {
		String root = "block/machine/sculk_collector/" + state;
		ResourceLocation side = provider.modLoc("block/machine/sculk_collector/side_" + state);
		ResourceLocation top = provider.modLoc("block/machine/sculk_collector/top_" + state);
		ResourceLocation bottom = provider.modLoc("block/machine/sculk_collector/bottom");
		return provider.models().cube(
				root,
				bottom,
				top,
				side,
				side,
				side,
				side
		);
	}
}