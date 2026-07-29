package dev.celestiacraft.deep_tech.common.register;

import com.tterrag.registrate.util.entry.BlockEntry;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.api.client.model.ItemModelGen;
import dev.celestiacraft.deep_tech.common.block.machine.crusher.CrusherBlock;
import dev.celestiacraft.deep_tech.common.block.machine.furnace.SculkFurnaceBlock;
import dev.celestiacraft.libs.api.register.block.BasicBlock;
import net.minecraft.core.Direction;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;

public class DTBlocks {
	public static final BlockEntry<BasicBlock> MACHINE_FRAME;
	public static final BlockEntry<CrusherBlock> MACHINE_CRUSHER;
	public static final BlockEntry<SculkFurnaceBlock> MACHINE_SCULK_FURNACE;

	static {
		DTCreativeTabs.getTab("machine");

		MACHINE_FRAME = DeepTech.REGISTRATE.block("machine_frame", BasicBlock::new)
				.item()
				.model(ItemModelGen.withModel("block/machine_frame"))
				.build()
				.blockstate((context, provider) -> {
					BlockModelProvider models = provider.models();
					BlockModelBuilder model = models.cube(
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
								Direction facing = state.getValue(CrusherBlock.FACING);
								boolean active = state.getValue(CrusherBlock.LIT);

								return ConfiguredModel.builder()
										.modelFile(active ? modelOn : modelOff)
										.rotationY((int) facing.toYRot())
										.build();
							});
				})
				.register();

		MACHINE_SCULK_FURNACE = DeepTech.REGISTRATE.block("machine_sculk_furnace", SculkFurnaceBlock::new)
				.item()
				.model(ItemModelGen.withModel("block/machine/machine_sculk_furnace_north"))
				.build()
				.blockstate((context, provider) -> {
					BlockModelProvider models = provider.models();

					// 为每个方向创建模型
					BlockModelBuilder modelOff = models.cube(
							"machine_furnace_off",

							provider.modLoc("block/machine/furnace/bottom"),
							provider.modLoc("block/machine/furnace/top_off"),
							provider.modLoc("block/machine/furnace/face_off"),
							provider.modLoc("block/machine/furnace/side_off"),
							provider.modLoc("block/machine/furnace/side_off"),
							provider.modLoc("block/machine/furnace/side_off")
					);

					BlockModelBuilder modelOn = models.cube(
							"machine_furnace_on",

							provider.modLoc("block/machine/furnace/bottom"),
							provider.modLoc("block/machine/furnace/top_on"),
							provider.modLoc("block/machine/furnace/face_on"),
							provider.modLoc("block/machine/furnace/side_on"),
							provider.modLoc("block/machine/furnace/side_on"),
							provider.modLoc("block/machine/furnace/side_on")
					);
					provider.getVariantBuilder(context.get())
							.forAllStates((state) -> {
								Direction facing = state.getValue(SculkFurnaceBlock.FACING);
								boolean active = state.getValue(SculkFurnaceBlock.LIT);

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
