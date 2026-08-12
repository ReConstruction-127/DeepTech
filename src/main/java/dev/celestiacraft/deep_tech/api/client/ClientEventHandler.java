package dev.celestiacraft.deep_tech.api.client;

import dev.celestiacraft.deep_tech.api.client.renderer.SNFluidPortRenderer;
import dev.celestiacraft.deep_tech.api.client.renderer.SNItemPortRenderer;
import dev.celestiacraft.deep_tech.common.register.DTBlockEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "deep_tech", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventHandler {

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