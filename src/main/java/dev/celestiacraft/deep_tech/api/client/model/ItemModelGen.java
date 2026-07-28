package dev.celestiacraft.deep_tech.api.client.model;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ItemModelGen {
	public static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> generated(ResourceLocation texture) {
		return (context, provider) -> {
			provider.generated(context::getEntry, texture);
		};
	}

	public static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> generated(String texture) {
		return (context, provider) -> {
			provider.generated(context::getEntry, provider.modLoc(texture));
		};
	}

	public static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> handheld(ResourceLocation texture) {
		return (context, provider) -> {
			provider.handheld(context::getEntry, texture);
		};
	}

	public static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> handheld(String texture) {
		return (context, provider) -> {
			provider.handheld(context::getEntry, provider.modLoc(texture));
		};
	}

	public static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> customModel(ResourceLocation model) {
		return (context, provider) -> {
			provider.getBuilder(context.getName())
					.parent(provider.getExistingFile(model));
		};
	}

	public static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> customModel(String model) {
		return (context, provider) -> {
			provider.getBuilder(context.getName())
					.parent(provider.getExistingFile(provider.modLoc(model)));
		};
	}

	public static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> withModel(ResourceLocation model) {
		return (context, provider) -> {
			provider.withExistingParent(context.getName(), model);
		};
	}

	public static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> withModel(String model) {
		return (context, provider) -> {
			provider.withExistingParent(context.getName(), provider.modLoc(model));
		};
	}
}