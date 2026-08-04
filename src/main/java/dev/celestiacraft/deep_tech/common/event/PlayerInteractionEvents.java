package dev.celestiacraft.deep_tech.common.event;

import dev.celestiacraft.deep_tech.DeepTech;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DeepTech.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerInteractionEvents {
	@SubscribeEvent
	public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
		if (InteractionHandler.process(event.getLevel(), event.getPos(), event.getEntity(), false)) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		if (InteractionHandler.process(event.getLevel(), event.getPos(), event.getEntity(), true)) {
			event.setCanceled(true);
		}
	}
}