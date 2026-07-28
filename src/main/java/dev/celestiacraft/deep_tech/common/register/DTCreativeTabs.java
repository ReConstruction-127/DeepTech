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

	public static final Supplier<CreativeModeTab> MATERIAL;
	public static final Supplier<CreativeModeTab> MACHINE;

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
					.displayItems((params, output) -> {
						// ✅ 根据标签页名称添加对应的物品
						if (name.equals("material")) {
							output.accept(DTItems.SCULK_CHUNK.get());
							output.accept(DTItems.SCULK_ALLOY.get());
							// 后续添加新材料时在这里继续添加
						} else if (name.equals("machine")) {
							output.accept(DTBlocks.MACHINE_FRAME.get().asItem());
							output.accept(DTBlocks.MACHINE_CRUSHER.get().asItem());
							output.accept(DTBlocks.MACHINE_SCULK_FURNACE.get().asItem());
							// 后续添加新机器时在这里继续添加
						}
					})
					.build();
		});
	}

	public static Registrate getTab(String name) {
		return DeepTech.REGISTRATE.defaultCreativeTab(getTabKey(name));
	}

	public static ResourceKey<CreativeModeTab> getTabKey(String name) {
		return ResourceKey.create(
				Registries.CREATIVE_MODE_TAB,
				DeepTech.loadResource(name)
		);
	}

	public static void register(IEventBus bus) {
		DeepTech.registerLog("Creative Tabs");
		TABS.register(bus);
	}
}