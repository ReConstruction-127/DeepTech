package dev.celestiacraft.deep_tech.datagen;

import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.datagen.language.LanguageGenerate;
import dev.celestiacraft.deep_tech.datagen.language.locale.Chinese;
import dev.celestiacraft.deep_tech.datagen.language.locale.English;
import dev.celestiacraft.deep_tech.datagen.recipes.DTRecipeProvider;
import dev.celestiacraft.deep_tech.datagen.tags.DTBlockTagsProvider;
import dev.celestiacraft.deep_tech.datagen.tags.DTFluidTagsProvider;
import dev.celestiacraft.deep_tech.datagen.tags.DTItemTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = DeepTech.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
	@SubscribeEvent
	public static void onDatagen(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput output = generator.getPackOutput();
		ExistingFileHelper helper = event.getExistingFileHelper();
		CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();
		boolean client = event.includeClient();
		boolean server = event.includeServer();

		// Client
		LanguageGenerate.register();
		generator.addProvider(client, new English(output));
		generator.addProvider(client, new Chinese(output));

		// Server
		DTBlockTagsProvider blockTags = new DTBlockTagsProvider(output, provider, helper);
		DTItemTagsProvider itemTags = new DTItemTagsProvider(output, provider, blockTags, helper);
		DTFluidTagsProvider fluidTags = new DTFluidTagsProvider(output, provider, helper);

		generator.addProvider(server, blockTags);
		generator.addProvider(server, itemTags);
		generator.addProvider(server, fluidTags);

		generator.addProvider(server, new DTRecipeProvider(output));
	}
}