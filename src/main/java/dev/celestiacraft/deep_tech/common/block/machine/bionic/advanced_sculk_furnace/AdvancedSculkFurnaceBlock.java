package dev.celestiacraft.deep_tech.common.block.machine.bionic.advanced_sculk_furnace;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.deep_tech.api.block.machine.MachineBlock;
import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.model.generators.BlockModelBuilder;

/**
 * 高级幽匿电炉: 普通幽匿电炉的升级版, 详情见 {@link AdvancedSculkFurnaceBlockEntity}.
 * <p>
 * 贴图暂时借用普通幽匿电炉的({@code block/machine/furnace/*}), 所以模型名字里带的是 furnace,
 * 只是借用它的贴图文件, 并不复用它的模型文件(两边的 datagen 各生成各的).
 */
public class AdvancedSculkFurnaceBlock extends MachineBlock<AdvancedSculkFurnaceBlockEntity> {
	public AdvancedSculkFurnaceBlock(Properties properties) {
		super(advancedProperties(properties)
				.noOcclusion());
	}

	@Override
	public BlockEntityType<AdvancedSculkFurnaceBlockEntity> getBlockEntityType() {
		return DTBlockEntities.ADVANCED_SCULK_FURNACE.get();
	}

	@Override
	public Class<AdvancedSculkFurnaceBlockEntity> getBlockEntityClass() {
		return AdvancedSculkFurnaceBlockEntity.class;
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> genBlockState() {
		return (context, provider) -> {
			BlockModelBuilder modelOff = borrowFurnaceModel(provider, "off");
			BlockModelBuilder modelOn = borrowFurnaceModel(provider, "on");
			horizontalLitBlock(provider, context.get(), modelOff, modelOn);
		};
	}

	/**
	 * 用自己的模型名, 但贴图指向普通幽匿电炉的贴图目录.
	 * <p>
	 * 不能直接调 {@code MachineBlock.machineModel(provider, "furnace", state)}:
	 * 那样会和普通幽匿电炉的 datagen 生成同一个模型文件, 属于重复生成.
	 */
	private static BlockModelBuilder borrowFurnaceModel(RegistrateBlockstateProvider provider, String state) {
		String textureRoot = "block/machine/furnace/";

		return provider.models().orientableWithBottom(
				"block/machine/advanced_sculk_furnace/" + state,
				provider.modLoc(textureRoot + "side_" + state),
				provider.modLoc(textureRoot + "face_" + state),
				provider.modLoc(textureRoot + "bottom"),
				provider.modLoc(textureRoot + "top_" + state)
		);
	}
}
