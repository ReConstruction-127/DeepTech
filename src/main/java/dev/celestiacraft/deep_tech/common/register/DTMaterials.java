package dev.celestiacraft.deep_tech.common.register;

import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.libs.common.material.MiningLevels;
import dev.celestiacraft.libs.common.material.NebulaMaterial;
import dev.celestiacraft.libs.common.material.event.RegisterMaterialEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DeepTech.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DTMaterials {
	public static NebulaMaterial
			COPPER,
			IRON,
			GOLD,
			SCULK_ALLOY,
			SCULK_STEEL;

	@SubscribeEvent
	public static void onMaterial(RegisterMaterialEvent event) {
		event.namespace(DeepTech.MODID);
		event.setCreativeTab(DTCreativeTabs.MATERIAL.getKey());

		COPPER = event.register("copper", MiningLevels.STONE)
				.nuggetWithTexture(DeepTech.loadResource("item/material/nugget/copper"))
				.dustWithTexture(DeepTech.loadResource("item/material/dust/copper"))
				.plateWithTexture(DeepTech.loadResource("item/material/plate/copper"));

		IRON = event.register("iron", MiningLevels.STONE)
				.dustWithTexture(DeepTech.loadResource("item/material/dust/iron"))
				.plateWithTexture(DeepTech.loadResource("item/material/plate/iron"));

		GOLD = event.register("gold", MiningLevels.IRON)
				.dustWithTexture(DeepTech.loadResource("item/material/dust/gold"))
				.plateWithTexture(DeepTech.loadResource("item/material/plate/gold"));

		SCULK_ALLOY = event.register("sculk_alloy")
				.ingotWithTexture(DeepTech.loadResource("item/material/ingot/sculk_alloy"))
				.plateWithTexture(DeepTech.loadResource("item/material/plate/sculk_alloy"));

		SCULK_STEEL = event.register("sculk_steel")
				.ingotWithTexture(DeepTech.loadResource("item/material/ingot/sculk_steel"));
	}
}