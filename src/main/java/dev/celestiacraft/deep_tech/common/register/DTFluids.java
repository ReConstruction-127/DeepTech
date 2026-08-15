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
				.tint(0xFF2F6E55)
				.liquidBlock((supplier, props) -> new SculkCultureLiquidBlock(supplier::get, props))
				.bucket()
				.model(DTFluidTexture.forgeFluidBucket("sculk_culture"))
				.build()
				.register();

		// 源流体是懒实例化的:若拖到注册表冻结后才创建会崩溃(Registry is already frozen),
		// 因此在 FLUIDS 注册完成(冻结前)时立即预热, 确保渲染/存取时源流体已存在
		DeepTech.REGISTRATE.addRegisterCallback(ForgeRegistries.Keys.FLUIDS, () -> {
			DeepTech.LOGGER.info("[DTFluids] preheating fluid sources...");
			LIQUID_EXPERIENCE.getSource();
			SCULK_CULTURE.getSource();
			DeepTech.LOGGER.info("[DTFluids] fluid sources ready");
			DeepTech.LOGGER.info("[DTFluids] registry direct read: liquid_experience={}, sculk_culture={}",
					ForgeRegistries.FLUIDS.getValue(DeepTech.loadResource("liquid_experience")),
					ForgeRegistries.FLUIDS.getValue(DeepTech.loadResource("sculk_culture")));
		});
	}

	public static void register() {
		DeepTech.registerLog("Fluids");
	}
}