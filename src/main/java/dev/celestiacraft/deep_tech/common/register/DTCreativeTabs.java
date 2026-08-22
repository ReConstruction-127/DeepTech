package dev.celestiacraft.deep_tech.common.register;

import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.common.register.block.MachineBlocks;
import dev.celestiacraft.deep_tech.common.register.item.MaterialItems;
import dev.celestiacraft.deep_tech.common.register.item.ToolItems;
import dev.celestiacraft.libs.register.NebulaRegistrate;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class DTCreativeTabs {
	public static final DeferredRegister<CreativeModeTab> TABS;

	public static final RegistryObject<CreativeModeTab>
			MATERIAL,
			MACHINE,
			TOOL;

	static {
		TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DeepTech.MODID);

		MATERIAL = addCreativeModeTab("material", () -> MaterialItems.SCULK_CHUNK.asStack());
		MACHINE = addCreativeModeTab("machine", () -> MachineBlocks.CRUSHER.asStack());
		TOOL = addCreativeModeTab("tool", () -> ToolItems.WRENCH.asStack());
	}

	private static RegistryObject<CreativeModeTab> addCreativeModeTab(String name, Supplier<ItemStack> icon) {
		return TABS.register(name, () -> {
			String tranKey = String.format("itemGroup.%s.%s", DeepTech.MODID, name);
			return CreativeModeTab.builder()
					.icon(icon)
					.title(Component.translatable(tranKey))
					.build();
		});
	}

	public static NebulaRegistrate getTab(String name) {
		return DeepTech.REGISTRATE.defaultCreativeTab(getTabKey(name));
	}

	public static ResourceKey<CreativeModeTab> getTabKey(String name) {
		return ResourceKey.create(
				Registries.CREATIVE_MODE_TAB,
				DeepTech.loadResource(name)
		);
	}

	public static void register(IEventBus bus) {
		TABS.register(bus);
		DeepTech.registerLog("Creative Tabs");
	}
}