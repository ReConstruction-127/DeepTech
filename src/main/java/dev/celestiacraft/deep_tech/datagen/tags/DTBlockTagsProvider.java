package dev.celestiacraft.deep_tech.datagen.tags;

import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.common.register.block.MachineBlocks;
import dev.celestiacraft.deep_tech.common.register.block.PluginBlocks;
import dev.celestiacraft.deep_tech.common.register.block.ReactorBlocks;
import dev.celestiacraft.deep_tech.tags.DeepTechBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class DTBlockTagsProvider extends BlockTagsProvider {
	public DTBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper helper) {
		super(output, provider, DeepTech.MODID, helper);
	}

	@Override
	protected void addTags(HolderLookup.@NotNull Provider provider) {
		tag(DeepTechBlockTags.WRENCH_PICKUP)
				.addTag(DeepTechBlockTags.MACHINES)
				.add(PluginBlocks.SPEED_PLUGIN.get())
				.add(Blocks.CRAFTING_TABLE)
				.add(Blocks.FURNACE)
				.add(Blocks.BLAST_FURNACE)
				.add(Blocks.SMOKER);

		// 反应堆外壳: 反应堆外壳 + 扩展坞(外壳位置允许放扩展坞)
		tag(DeepTechBlockTags.REACTOR_CASING)
				.add(ReactorBlocks.REACTOR_CASING.get())
				.add(MachineBlocks.DOCKING_STATION.get());

		// 反应堆生长腔: 空气或长出来的幽匿块
		tag(DeepTechBlockTags.REACTOR_CAVITY)
				.add(Blocks.AIR)
				.add(Blocks.CAVE_AIR)
				.add(Blocks.SCULK);

		// 反应堆惰性方块: 过载蔓延时不会被幽匿覆盖
		// —— 外壳、控制器、能量接收器、全部 bionic 机器, 以及原版幽匿系方块
		// 注意 sculk_vein 不在此列: 脉络是要被扩散覆盖掉的装饰, 放进惰性标签会把扩散路径堵死
		tag(DeepTechBlockTags.REACTOR_INERT)
				.add(ReactorBlocks.REACTOR_CASING.get())
				.add(ReactorBlocks.REACTOR_CONTROLLER.get())
				.add(ReactorBlocks.REACTOR_ENERGY_RECEIVER.get())
				.add(MachineBlocks.DOCKING_STATION.get())
				.add(MachineBlocks.ADVANCED_SCULK_FURNACE.get())
				.add(MachineBlocks.ADVANCED_CRUSHER.get())
				.add(Blocks.SCULK)
				.add(Blocks.SCULK_CATALYST)
				.add(Blocks.SCULK_SHRIEKER)
				.add(Blocks.SCULK_SENSOR)
				.add(Blocks.CALIBRATED_SCULK_SENSOR);
	}
}