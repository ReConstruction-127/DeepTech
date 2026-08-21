package dev.celestiacraft.deep_tech.common.block.machine.alloy_furnace;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.deep_tech.api.block.machine.MachineBlock;
import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.model.generators.BlockModelBuilder;

public class AlloyFurnaceBlock extends MachineBlock<AlloyFurnaceBlockEntity> {
	public AlloyFurnaceBlock(Properties properties) {
		super(properties.sound(SoundType.DEEPSLATE_BRICKS));
	}

	@Override
	public BlockEntityType<AlloyFurnaceBlockEntity> getBlockEntityType() {
		return DTBlockEntities.ALLOY_FURNACE.get();
	}

	@Override
	public Class<AlloyFurnaceBlockEntity> getBlockEntityClass() {
		return AlloyFurnaceBlockEntity.class;
	}

	public static <T extends Block> NonNullBiConsumer<DataGenContext<Block, T>, RegistrateBlockstateProvider> genBlockState() {
		return (context, provider) -> {
			BlockModelBuilder modelOff = machineModel(provider, "alloy_furnace", "off");
			BlockModelBuilder modelOn = machineModel(provider, "alloy_furnace", "on");
			horizontalLitBlock(provider, context.get(), modelOff, modelOn);
		};
	}
}