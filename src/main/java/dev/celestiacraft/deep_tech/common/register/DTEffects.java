package dev.celestiacraft.deep_tech.common.register;

import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.common.effect.infection.InfectionEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class DTEffects {
	public static final RegistryEntry<InfectionEffect> INFECTION;

	static {
		INFECTION = DeepTech.REGISTRATE.effect("infection", InfectionEffect::new)
				.category(MobEffectCategory.HARMFUL)
				.color(0x1E4A45)
				.durationEffectTick((duration, amplifier) -> {
					// 每 2 tick 铺一段轨迹(保持连续), 扣血/经验每 20 tick
					return duration % 2 == 0;
				})
				.register();
	}

	public static void register() {
		DeepTech.registerLog("Effects");
	}
}