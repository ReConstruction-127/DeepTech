package dev.celestiacraft.deep_tech.common.block.machine.other.energy_cell;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.deep_tech.api.block.machine.MachineBlock;
import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ConfiguredModel;

public class EnergyCellBlock extends MachineBlock<EnergyCellBlockEntity> {
	public EnergyCellBlock(Properties properties) {
		super(basicProperties(properties));
		registerDefaultState(stateDefinition.any());
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
			BlockModelBuilder model = provider.models()
					.withExistingParent("block/machine/energy_cell", "minecraft:block/cube")
					.texture("down", provider.modLoc("block/machine/energy_cell/top_off"))
					.texture("east", provider.modLoc("block/machine/energy_cell/side_off"))
					.texture("north", provider.modLoc("block/machine/energy_cell/face_off"))
					.texture("south", provider.modLoc("block/machine/energy_cell/side_off"))
					.texture("up", provider.modLoc("block/machine/energy_cell/bottom"))
					.texture("west", provider.modLoc("block/machine/energy_cell/side_off"));

			provider.getVariantBuilder(context.get())
					.partialState()
					.setModels(new ConfiguredModel(model));
		};
	}
}