package dev.celestiacraft.deep_tech.api.client.texture;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.libs.NebulaLibs;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder;
import net.minecraftforge.registries.ForgeRegistries;

@Getter
@AllArgsConstructor
public class DTFluidTexture {
	private final ResourceLocation flowing;
	private final ResourceLocation still;

	public static DTFluidTexture of(ResourceLocation flowing, ResourceLocation still) {
		return new DTFluidTexture(flowing, still);
	}

	public static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> forgeFluidBucket(Fluid fluid) {
		return (context, provider) -> {
			provider.withExistingParent(context.getName(), NebulaLibs.loadForge("item/bucket_drip"))
					.customLoader(DynamicFluidContainerModelBuilder::begin)
					.fluid(fluid);
		};
	}

	public static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> forgeFluidBucket(ResourceLocation fluid) {
		return (context, provider) -> {
			provider.withExistingParent(context.getName(), NebulaLibs.loadForge("item/bucket_drip"))
					.customLoader(DynamicFluidContainerModelBuilder::begin)
					.fluid(ForgeRegistries.FLUIDS.getValue(fluid));
		};
	}

	public static <T extends Item> NonNullBiConsumer<DataGenContext<Item, T>, RegistrateItemModelProvider> forgeFluidBucket(String fluid) {
		return (context, provider) -> {
			provider.withExistingParent(context.getName(), NebulaLibs.loadForge("item/bucket_drip"))
					.customLoader(DynamicFluidContainerModelBuilder::begin)
					.fluid(ForgeRegistries.FLUIDS.getValue(DeepTech.loadResource(fluid)));
		};
	}
}