package dev.celestiacraft.deep_tech.common.effect;

import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.common.register.DTEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 感染效果的事件处理:被感染杀死的生物在死亡点放置幽匿催生体,催生体上放置幽匿传感器。
 */
@Mod.EventBusSubscriber(modid = DeepTech.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class InfectionEvents {

	@SubscribeEvent
	public static void onDeath(LivingDeathEvent event) {
		var entity = event.getEntity();
		if (entity.level().isClientSide || !entity.hasEffect(DTEffects.INFECTION.get())) {
			return;
		}
		Level level = entity.level();

		// 死亡点向上找一个可放置位置(通常就是脚下)
		BlockPos catalystPos = null;
		for (int i = 0; i < 4; i++) {
			BlockPos p = entity.blockPosition().above(i);
			if (level.getBlockState(p).canBeReplaced()) {
				catalystPos = p;
				break;
			}
		}
		if (catalystPos == null) {
			return;
		}

		level.setBlock(catalystPos, Blocks.SCULK_CATALYST.defaultBlockState(), 3);

		// 催生体上放置幽匿传感器
		BlockPos sensorPos = catalystPos.above();
		if (level.getBlockState(sensorPos).canBeReplaced()) {
			level.setBlock(sensorPos, Blocks.SCULK_SENSOR.defaultBlockState(), 3);
		}
	}
}