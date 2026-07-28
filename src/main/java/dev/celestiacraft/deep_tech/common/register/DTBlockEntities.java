package dev.celestiacraft.deep_tech.common.register;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.common.block.machine.crusher.CrusherBlockEntity;
import dev.celestiacraft.deep_tech.common.block.machine.furnace.SculkFurnaceBlockEntity;

public class DTBlockEntities {
	public static final BlockEntityEntry<CrusherBlockEntity> CRUSHER;
	public static final BlockEntityEntry<SculkFurnaceBlockEntity> SCULK_FURNACE;

	static {
		CRUSHER = DeepTech.REGISTRATE.blockEntity("crusher", CrusherBlockEntity::new)
				.validBlocks(DTBlocks.MACHINE_CRUSHER)
				.register();

		SCULK_FURNACE = DeepTech.REGISTRATE.blockEntity("furnace", SculkFurnaceBlockEntity::new)
				.validBlocks(DTBlocks.MACHINE_SCULK_FURNACE)
				.register();
	}

	public static void register() {
		DeepTech.registerLog("Block Entities");
	}
}