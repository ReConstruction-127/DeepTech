package dev.celestiacraft.deep_tech.datagen.tags;

import dev.celestiacraft.deep_tech.DeepTech;
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
				.add(Blocks.CRAFTING_TABLE)
				.add(Blocks.FURNACE)
				.add(Blocks.BLAST_FURNACE)
				.add(Blocks.SMOKER);
	}
}