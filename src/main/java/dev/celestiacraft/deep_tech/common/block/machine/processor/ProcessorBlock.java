package dev.celestiacraft.deep_tech.common.block.machine.processor;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.deep_tech.api.block.machine.MachineBlock;
import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.model.generators.BlockModelBuilder;

public class ProcessorBlock extends MachineBlock<ProcessorBlockEntity> {
	public ProcessorBlock(Properties properties) {
		super(properties);
	}

	@Override
	public BlockEntityType<ProcessorBlockEntity> getBlockEntityType() {
		return DTBlockEntities.PROCESSOR.get();
	}

	@Override
	public Class<ProcessorBlockEntity> getBlockEntityClass() {
		return ProcessorBlockEntity.class;
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> genBlockState() {
		return (context, provider) -> {
			BlockModelBuilder modelOff = machineModel(provider, "processor", "off");
			BlockModelBuilder modelOn = machineModel(provider, "processor", "on");
			horizontalLitBlock(provider, context.get(), modelOff, modelOn);
		};
	}
}