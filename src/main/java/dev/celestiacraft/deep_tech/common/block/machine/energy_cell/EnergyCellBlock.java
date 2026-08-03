package dev.celestiacraft.deep_tech.common.block.machine.energy_cell;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.deep_tech.api.block.machine.MachineBlock;
import dev.celestiacraft.deep_tech.common.block.machine.resonance_node.ResonanceNodeBlockEntity;
import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import dev.celestiacraft.libs.api.register.block.BlockFacing;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;

public class EnergyCellBlock extends MachineBlock<EnergyCellBlockEntity> {
	public EnergyCellBlock(Properties properties) {
		super(properties.noOcclusion());
		registerDefaultState(stateDefinition.any());
	}

	@Override
	protected BlockFacing useFacingType() {
		return BlockFacing.NONE;
	}

	@Override
	protected boolean useLitState() {
		return false;
	}

	@Override
	public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
		return 0;
	}

	@Override
	public BlockEntityType<EnergyCellBlockEntity> getBlockEntityType() {
		return DTBlockEntities.ENERGY_CELL.get();
	}

	@Override
	public Class<EnergyCellBlockEntity> getBlockEntityClass() {
		return EnergyCellBlockEntity.class;
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> genBlockState() {
		return (context, provider) -> {
			BlockModelProvider models = provider.models();
			BlockModelBuilder model = models.getBuilder(context.getName())
					.parent(models.getExistingFile(provider.modLoc("block/machine/energy_cell")));

			provider.getVariantBuilder(context.get())
					.forAllStates((state) -> ConfiguredModel.builder()
							.modelFile(model)
							.build() // ✅ 无方向旋转
					);
		};
	}
}