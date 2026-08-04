package dev.celestiacraft.deep_tech.common.event;

import dev.celestiacraft.deep_tech.DeepTech;
//import dev.celestiacraft.deep_tech.common.event.PlateCraft;
import dev.celestiacraft.deep_tech.common.event.SculkBoneRepair;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DeepTech.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerInteractionEvents {

//	@SubscribeEvent
//	public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
//		boolean handled = PlateCraft.process(
//				event.getLevel(),
//				event.getPos(),
//				event.getEntity()
//		);
//		if (handled) {
//			event.setCanceled(true);
//		}
//	}
//
//	@SubscribeEvent
//	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
//		boolean handled = SculkBoneRepair.process(
//				event.getLevel(),
//				event.getPos(),
//				event.getEntity()
//		);
//		if (handled) {
//			event.setCanceled(true); // 阻止原版使用物品动作（如放置方块、交互等）
//		}
//	}

	@SubscribeEvent
	public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
		if (InteractionHandler.process(event.getLevel(), event.getPos(), event.getEntity())) {
			event.setCanceled(true);
		}
	}
}