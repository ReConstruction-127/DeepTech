package dev.celestiacraft.deep_tech.common.block.machine.advanced.assembler;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.deep_tech.api.block.machine.MachineBlock;
import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.model.generators.BlockModelBuilder;

public class AssemblerBlock extends MachineBlock<AssemblerBlockEntity> {
	public AssemblerBlock(Properties properties) {
		super(advancedProperties(properties));
	}

	@Override
	public BlockEntityType<AssemblerBlockEntity> getBlockEntityType() {
		return DTBlockEntities.ASSEMBLER.get();
	}

	@Override
	public Class<AssemblerBlockEntity> getBlockEntityClass() {
		return AssemblerBlockEntity.class;
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> genBlockState() {
		return (context, provider) -> {
			BlockModelBuilder modelOff = machineModel(provider, "assembler", "off");
			BlockModelBuilder modelOn = machineModel(provider, "assembler", "on");
			horizontalLitBlock(provider, context.get(), modelOff, modelOn);
		};
	}
}