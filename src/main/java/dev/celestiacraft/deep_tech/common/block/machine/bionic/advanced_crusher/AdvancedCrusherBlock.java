package dev.celestiacraft.deep_tech.common.block.machine.bionic.advanced_crusher;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.deep_tech.api.block.machine.MachineBlock;
import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.model.generators.BlockModelBuilder;

/**
 * 高级粉碎机: 普通粉碎机的升级版, 详情见 {@link AdvancedCrusherBlockEntity}.
 * <p>
 * 贴图暂时借用普通粉碎机的({@code block/machine/crusher/*}), 所以模型名字里带的是 crusher,
 * 只是借用它的贴图文件, 并不复用它的模型文件(两边的 datagen 各生成各的).
 */
public class AdvancedCrusherBlock extends MachineBlock<AdvancedCrusherBlockEntity> {
	public AdvancedCrusherBlock(Properties properties) {
		super(advancedProperties(properties));
	}

	@Override
	public BlockEntityType<AdvancedCrusherBlockEntity> getBlockEntityType() {
		return DTBlockEntities.ADVANCED_CRUSHER.get();
	}

	@Override
	public Class<AdvancedCrusherBlockEntity> getBlockEntityClass() {
		return AdvancedCrusherBlockEntity.class;
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> genBlockState() {
		return (context, provider) -> {
			BlockModelBuilder modelOff = borrowCrusherModel(provider, "off");
			BlockModelBuilder modelOn = borrowCrusherModel(provider, "on");
			horizontalLitBlock(provider, context.get(), modelOff, modelOn);
		};
	}

	/**
	 * 用自己的模型名, 但贴图指向普通粉碎机的贴图目录.
	 * <p>
	 * 不能直接调 {@code MachineBlock.machineModel(provider, "crusher", state)}:
	 * 那样会和普通粉碎机的 datagen 生成同一个模型文件, 属于重复生成.
	 */
	private static BlockModelBuilder borrowCrusherModel(RegistrateBlockstateProvider provider, String state) {
		String textureRoot = "block/machine/crusher/";

		return provider.models().orientableWithBottom(
				"block/machine/advanced_crusher/" + state,
				provider.modLoc(textureRoot + "side_" + state),
				provider.modLoc(textureRoot + "face_" + state),
				provider.modLoc(textureRoot + "bottom"),
				provider.modLoc(textureRoot + "top_" + state)
		);
	}
}
