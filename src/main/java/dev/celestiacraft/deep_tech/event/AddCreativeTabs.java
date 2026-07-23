package dev.celestiacraft.deep_tech.event;

import dev.celestiacraft.deep_tech.common.register.DTBlocks;
import dev.celestiacraft.deep_tech.common.register.DTCreativeTabs;
import dev.celestiacraft.deep_tech.common.register.DTItems;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class AddCreativeTabs {
	@SubscribeEvent
	public static void onCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
		ResourceKey<CreativeModeTab> key = event.getTabKey();

		if (key.equals(DTCreativeTabs.getTabKey("material"))) {
			event.accept(DTItems.SCULK_CHUNK.asStack());
		}

		if (key.equals(DTCreativeTabs.getTabKey("machine"))) {
			event.accept(DTBlocks.MACHINE_FRAME.asStack());
			event.accept(DTBlocks.MACHINE_CRUSHER.asStack());
		}
	}
}