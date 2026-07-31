package dev.celestiacraft.deep_tech.common.block.machine.crusher;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.deep_tech.api.block.machine.MachineBlock;
import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.model.generators.BlockModelBuilder;

public class CrusherBlock extends MachineBlock<CrusherBlockEntity> {
	public CrusherBlock(Properties properties) {
		super(properties);
	}

	@Override
	public BlockEntityType<CrusherBlockEntity> getBlockEntityType() {
		return DTBlockEntities.CRUSHER.get();
	}

	@Override
	public Class<CrusherBlockEntity> getBlockEntityClass() {
		return CrusherBlockEntity.class;
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> genBlockState() {
		return (context, provider) -> {
			BlockModelBuilder modelOff = machineModel(provider, context.getName() + "_off", "crusher", false);
			BlockModelBuilder modelOn = machineModel(provider, context.getName() + "_on", "crusher", true);
			horizontalLitBlock(provider, context.get(), modelOff, modelOn);
		};
	}
}