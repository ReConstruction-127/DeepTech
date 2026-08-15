package dev.celestiacraft.deep_tech.common.register;

import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.common.effect.InfectionEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DTEffects {
	public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, DeepTech.MODID);

	/** 感染 */
	public static final RegistryObject<MobEffect> INFECTION = REGISTRY.register("infection", InfectionEffect::new);

	public static void register(IEventBus bus) {
		REGISTRY.register(bus);
		DeepTech.registerLog("Effects");
	}
}