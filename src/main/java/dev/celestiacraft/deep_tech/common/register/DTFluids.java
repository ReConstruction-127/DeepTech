package dev.celestiacraft.deep_tech.common.register;

import com.tterrag.registrate.util.entry.FluidEntry;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.api.client.texture.DTFluidTexture;
import dev.celestiacraft.deep_tech.api.client.texture.DTFluidTextures;
import dev.celestiacraft.deep_tech.api.register.fluid.DTFluidBuilder;
import dev.celestiacraft.deep_tech.tags.DeepTechFluidTags;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class DTFluids {
	public static final FluidEntry<ForgeFlowingFluid.Flowing> LIQUID_EXPERIENCE;

	static {
		DTCreativeTabs.getTab("material");

		LIQUID_EXPERIENCE = DTFluidBuilder.of("liquid_experience")
				.flowing(DTFluidTextures.EXPERIENCE.getFlowing())
				.still(DTFluidTextures.EXPERIENCE.getStill())
				.bucket()
				.model(DTFluidTexture.forgeFluidBucket("liquid_experience"))
				.build()
				.tag(DeepTechFluidTags.EXPERIENCE)
				.register();
	}

	public static void register() {
		DeepTech.registerLog("Fluids");
	}
}