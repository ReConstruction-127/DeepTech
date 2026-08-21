package dev.celestiacraft.deep_tech.datagen.tags;

import dev.celestiacraft.deep_tech.DeepTech;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class DTItemTagsProvider extends ItemTagsProvider {
	public DTItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, BlockTagsProvider blockTags, @Nullable ExistingFileHelper helper) {
		super(output, provider, blockTags.contentsGetter(), DeepTech.MODID, helper);
	}

	@Override
	protected void addTags(HolderLookup.@NotNull Provider provider) {
	}
}