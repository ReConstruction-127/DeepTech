package dev.celestiacraft.deep_tech.common.register;

import com.tterrag.registrate.util.entry.FluidEntry;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.api.client.texture.DTFluidTexture;
import dev.celestiacraft.deep_tech.api.client.texture.DTFluidTextures;
import dev.celestiacraft.deep_tech.api.register.fluid.DTFluidBuilder;
import dev.celestiacraft.deep_tech.common.fluid.SculkCultureLiquidBlock;
import dev.celestiacraft.deep_tech.tags.DeepTechFluidTags;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.ForgeRegistries;

public class DTFluids {
	public static final FluidEntry<ForgeFlowingFluid.Flowing> LIQUID_EXPERIENCE;
	public static final FluidEntry<ForgeFlowingFluid.Flowing> SCULK_CULTURE;
	public static final FluidEntry<ForgeFlowingFluid.Flowing> SCULK_INDUCTION_FLUID;

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

		SCULK_CULTURE = DTFluidBuilder.of("sculk_culture")
				.flowing(DTFluidTextures.CULTURE.getFlowing())
				.still(DTFluidTextures.CULTURE.getStill())
				.liquidBlock((supplier, properties) -> {
					return new SculkCultureLiquidBlock(supplier, properties);
				})
				.bucket()
				.model(DTFluidTexture.forgeFluidBucket("sculk_culture"))
				.build()
				.register();

		SCULK_INDUCTION_FLUID = DTFluidBuilder.of("sculk_induction_fluid")
				.flowing(DTFluidTextures.INDUCTION.getFlowing())
				.still(DTFluidTextures.INDUCTION.getStill())
				.bucket()
				.model(DTFluidTexture.forgeFluidBucket("sculk_induction_fluid"))
				.build()
				.register();

		// 源流体是懒实例化的:若拖到注册表冻结后才创建会崩溃(Registry is already frozen),
		// 因此在 FLUIDS 注册完成(冻结前)时立即预热, 确保渲染/存取时源流体已存在
		DeepTech.REGISTRATE.addRegisterCallback(ForgeRegistries.Keys.FLUIDS, () -> {
			DeepTech.LOGGER.info("[DTFluids] preheating fluid sources...");
			LIQUID_EXPERIENCE.getSource();
			SCULK_CULTURE.getSource();
			SCULK_INDUCTION_FLUID.getSource();
			DeepTech.LOGGER.info("[DTFluids] fluid sources ready");
			DeepTech.LOGGER.info("[DTFluids] registry direct read: liquid_experience={}, sculk_culture={}, sculk_induction_fluid={}",
					ForgeRegistries.FLUIDS.getValue(DeepTech.loadResource("liquid_experience")),
					ForgeRegistries.FLUIDS.getValue(DeepTech.loadResource("sculk_culture")),
					ForgeRegistries.FLUIDS.getValue(DeepTech.loadResource("sculk_induction_fluid")));
		});
	}

	public static void register() {
		DeepTech.registerLog("Fluids");
	}
}