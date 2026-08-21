package dev.celestiacraft.deep_tech.common.block.machine.sculk_nursery;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.deep_tech.api.block.machine.MachineBlock;
import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.model.generators.BlockModelBuilder;

public class SculkNurseryBlock extends MachineBlock<SculkNurseryBlockEntity> {
	public SculkNurseryBlock(Properties properties) {
		super(properties.sound(SoundType.NETHERITE_BLOCK));
	}

	@Override
	public BlockEntityType<SculkNurseryBlockEntity> getBlockEntityType() {
		return DTBlockEntities.SCULK_NURSERY.get();
	}

	@Override
	public Class<SculkNurseryBlockEntity> getBlockEntityClass() {
		return SculkNurseryBlockEntity.class;
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> genBlockState() {
		return (context, provider) -> {
			BlockModelBuilder modelOff = machineModel(provider, "sculk_nursery", "off");
			BlockModelBuilder modelOn = machineModel(provider, "sculk_nursery", "on");
			horizontalLitBlock(provider, context.get(), modelOff, modelOn);
		};
	}
}