package dev.celestiacraft.deep_tech.common.register;

import com.tterrag.registrate.Registrate;
import dev.celestiacraft.deep_tech.DeepTech;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class DTCreativeTabs {
	public static final DeferredRegister<CreativeModeTab> TABS;

	public static final Supplier<CreativeModeTab>
			MATERIAL,
			MACHINE;

	static {
		TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DeepTech.MODID);

		MATERIAL = addCreativeModeTab("material", DTItems.SCULK_CHUNK::asStack);
		MACHINE = addCreativeModeTab("machine", DTBlocks.MACHINE_CRUSHER::asStack);
	}

	private static Supplier<CreativeModeTab> addCreativeModeTab(String name, Supplier<ItemStack> icon) {
		return TABS.register(name, () -> {
			String tranKey = String.format("itemGroup.%s.%s", DeepTech.MODID, name);
			return CreativeModeTab.builder()
					.icon(icon)
					.title(Component.translatable(tranKey))
					.build();
		});
	}

	public static Registrate getTab(String name) {
		return DeepTech.REGISTRATE.defaultCreativeTab(ResourceKey.create(
				Registries.CREATIVE_MODE_TAB,
				DeepTech.loadResource(name)
		));
	}

	public static ResourceKey<CreativeModeTab> getTabKey(String name) {
		return ResourceKey.create(
				Registries.CREATIVE_MODE_TAB,
				DeepTech.loadResource(name)
		);
	}

	public static void register(IEventBus bus) {
		DeepTech.registerLog("Creative Tab");
		TABS.register(bus);
	}
}