package dev.celestiacraft.deep_tech.common.block.machine.furnace;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.deep_tech.api.block.machine.MachineBlock;
import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.model.generators.BlockModelBuilder;

public class SculkFurnaceBlock extends MachineBlock<SculkFurnaceBlockEntity> {
	public SculkFurnaceBlock(Properties properties) {
		super(properties);
	}

	@Override
	public BlockEntityType<SculkFurnaceBlockEntity> getBlockEntityType() {
		return DTBlockEntities.SCULK_FURNACE.get();
	}

	@Override
	public Class<SculkFurnaceBlockEntity> getBlockEntityClass() {
		return SculkFurnaceBlockEntity.class;
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> genBlockState() {
		return (context, provider) -> {
			BlockModelBuilder modelOff = machineModel(provider, context.getName() + "_off", "furnace", false);
			BlockModelBuilder modelOn = machineModel(provider, context.getName() + "_on", "furnace", true);
			horizontalLitBlock(provider, context.get(), modelOff, modelOn);
		};
	}
}