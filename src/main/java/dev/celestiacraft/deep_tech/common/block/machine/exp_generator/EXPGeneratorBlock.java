package dev.celestiacraft.deep_tech.common.block.machine.exp_generator;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.deep_tech.api.block.machine.MachineBlock;
import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.model.generators.BlockModelBuilder;

public class EXPGeneratorBlock extends MachineBlock<EXPGeneratorBlockEntity> {
	public EXPGeneratorBlock(Properties properties) {
		super(properties);
	}

	@Override
	public BlockEntityType<EXPGeneratorBlockEntity> getBlockEntityType() {
		return DTBlockEntities.EXP_GENERATOR.get();
	}

	@Override
	public Class<EXPGeneratorBlockEntity> getBlockEntityClass() {
		return EXPGeneratorBlockEntity.class;
	}

	@Override
	public boolean creativeUseFluidInteraction() {
		return true;
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> genBlockState() {
		return (context, provider) -> {
			BlockModelBuilder modelOff = machineModel(provider, "exp_generator", "off");
			BlockModelBuilder modelOn = machineModel(provider, "exp_generator", "on");
			horizontalLitBlock(provider, context.get(), modelOff, modelOn);
		};
	}
}