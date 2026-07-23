package dev.celestiacraft.deep_tech.common.register;

import com.tterrag.registrate.util.entry.BlockEntry;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.api.client.ItemModelGen;
import dev.celestiacraft.deep_tech.common.block.machine.crusher.CrusherBlock;
import dev.celestiacraft.libs.api.register.block.BasicBlock;
import net.minecraft.core.Direction;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;

public class DTBlocks {
	public static BlockEntry<BasicBlock> MACHINE_FRAME;

	static {
		MACHINE_FRAME = DeepTech.REGISTRATE.block("machine_frame", BasicBlock::new)
				.item()
				.model(ItemModelGen.withModel("block/machine_frame"))
				.build()
				.blockstate((context, provider) -> {
					var models = provider.models();
					var model = models.cube(
							"machine_frame",
							provider.modLoc("block/machine_frame_top"),
							provider.modLoc("block/machine_frame_top"),
							provider.modLoc("block/machine_frame_side"),
							provider.modLoc("block/machine_frame_side"),
							provider.modLoc("block/machine_frame_side"),
							provider.modLoc("block/machine_frame_side")
					);
					provider.simpleBlock(context.get(), model);
				})
				.register();
	}

	public static final BlockEntry<CrusherBlock> MACHINE_CRUSHER;

	static {
		MACHINE_CRUSHER = DeepTech.REGISTRATE.block("machine_crusher", CrusherBlock::new)
				.item()
				.model(ItemModelGen.withModel("block/machine/machine_crusher_north"))
				.build()
				.blockstate((context, provider) -> {
					BlockModelProvider models = provider.models();

					// 为每个方向创建模型
					BlockModelBuilder modelOff = models.cube(
							"machine_crusher_off",

							provider.modLoc("block/machine/crusher/bottom"),
							provider.modLoc("block/machine/crusher/top_off"),
							provider.modLoc("block/machine/crusher/face_off"),
							provider.modLoc("block/machine/crusher/side_off"),
							provider.modLoc("block/machine/crusher/side_off"),
							provider.modLoc("block/machine/crusher/side_off")
					);

					BlockModelBuilder modelOn = models.cube(
							"machine_crusher_on",

							provider.modLoc("block/machine/crusher/bottom"),
							provider.modLoc("block/machine/crusher/top_on"),
							provider.modLoc("block/machine/crusher/face_on"),
							provider.modLoc("block/machine/crusher/side_on"),
							provider.modLoc("block/machine/crusher/side_on"),
							provider.modLoc("block/machine/crusher/side_on")
					);
					provider.getVariantBuilder(context.get())
							.forAllStates((state) -> {
								Direction facing = state.getValue(CrusherBlock.HORIZONTAL_FACING);
								boolean active = state.getValue(CrusherBlock.LIT);

								return ConfiguredModel.builder()
										.modelFile(active ? modelOn : modelOff)
										.rotationY((int) facing.toYRot())
										.build();
							});
				})
				.register();
	}

	public static void register() {
		DeepTech.registerLog("Blocks");
	}
}