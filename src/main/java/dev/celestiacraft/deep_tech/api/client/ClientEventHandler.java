package dev.celestiacraft.deep_tech.api.client;

import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.common.block.machine.advanced.sculk_network.renderer.SNFluidPortRenderer;
import dev.celestiacraft.deep_tech.common.block.machine.advanced.sculk_network.renderer.SNItemPortRenderer;
import dev.celestiacraft.deep_tech.common.item.TestTubeItem;
import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import dev.celestiacraft.deep_tech.common.register.item.ToolItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = DeepTech.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventHandler {
	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> ItemProperties.register(
				ToolItems.TEST_TUBE.get(),
				DeepTech.loadResource("filled"),
				(stack, level, entity, seed) -> TestTubeItem.getFluid(stack).isEmpty() ? 0.0F : 1.0F
		));
	}

	@SubscribeEvent
	public static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
		event.register((stack, tintIndex) -> {
			if (tintIndex != 1) {
				return 0xFFFFFFFF;
			}

			var fluid = TestTubeItem.getFluid(stack);
			return IClientFluidTypeExtensions.of(fluid.getFluid()).getTintColor(fluid);
		}, ToolItems.TEST_TUBE.get());
	}

	@SubscribeEvent
	public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
		// 注册输入端口渲染器
		event.registerBlockEntityRenderer(
				DTBlockEntities.SN_ITEM_INPUT_PORT.get(),
				SNItemPortRenderer::new
		);

		// 注册输出端口渲染器
		event.registerBlockEntityRenderer(
				DTBlockEntities.SN_ITEM_OUTPUT_PORT.get(),
				SNItemPortRenderer::new
		);
		// 注册流体输入端口渲染器
		event.registerBlockEntityRenderer(
				DTBlockEntities.SN_FLUID_INPUT_PORT.get(),
				SNFluidPortRenderer::new
		);
		// 注册流体输出端口渲染器
		event.registerBlockEntityRenderer(
				DTBlockEntities.SN_FLUID_OUTPUT_PORT.get(),
				SNFluidPortRenderer::new
		);
	}
}
