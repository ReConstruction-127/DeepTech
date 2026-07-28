package dev.celestiacraft.deep_tech.common.register;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.common.block.machine.crusher.CrusherBlockEntity;
import dev.celestiacraft.deep_tech.common.block.machine.furnace.SculkFurnaceBlockEntity;

public class DTBlockEntities {
    public static final BlockEntityEntry<CrusherBlockEntity> CRUSHER =
            DeepTech.REGISTRATE
                    .blockEntity("crusher", CrusherBlockEntity::create)  // ✅ 明确类型
                    .validBlocks(DTBlocks.MACHINE_CRUSHER)
                    .register();

    public static final BlockEntityEntry<SculkFurnaceBlockEntity> SCULK_FURNACE =
            DeepTech.REGISTRATE
                    .blockEntity("furnace", SculkFurnaceBlockEntity::create)  // ✅ 明确类型
                    .validBlocks(DTBlocks.MACHINE_SCULK_FURNACE)
                    .register();

    public static void register() {
        DeepTech.registerLog("Block Entities");
    }
}
