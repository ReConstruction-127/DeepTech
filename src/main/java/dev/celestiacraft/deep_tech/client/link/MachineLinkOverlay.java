package dev.celestiacraft.deep_tech.client.link;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.common.register.DTRenderTypes;
import dev.celestiacraft.deep_tech.client.render.LaserRenderer;
import dev.celestiacraft.deep_tech.config.client.MachineLinkConfig;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@Mod.EventBusSubscriber(modid = DeepTech.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class MachineLinkOverlay {
	private static final float GLOW_WIDTH_SCALE = 2.6F;
	private static final float GLOW_ALPHA_SCALE = 0.28F;
	private static final float OCCLUDED_ALPHA_SCALE = 0.18F;
	private static final float NODE_RADIUS_SCALE = 5.0F;
	private static final float HUB_RADIUS_SCALE = 1.8F;
	private static final int NODE_PULSE_TICKS = 40;
	private static final float NODE_PULSE_AMPLITUDE = 0.25F;

	private static final Vector3f RIGHT = new Vector3f();
	private static final Vector3f UP = new Vector3f();

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END || Minecraft.getInstance().isPaused()) {
			return;
		}
		MachineLinkScanner.clientTick();
	}

	@SubscribeEvent
	public static void onRenderLevel(RenderLevelStageEvent event) {
		if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
			return;
		}
		if (MachineLinkScanner.links().isEmpty() && MachineLinkScanner.nodes().isEmpty()) {
			return;
		}

		Minecraft minecraft = Minecraft.getInstance();
		Level level = minecraft.level;
		if (level == null) {
			return;
		}

		float partialTick = event.getPartialTick();
		float fade = MachineLinkScanner.fade(partialTick);
		if (fade <= 0.0F) {
			return;
		}

		Camera camera = event.getCamera();
		Vec3 cameraPos = camera.getPosition();
		float halfWidth = (float) (double) MachineLinkConfig.BEAM_WIDTH.get() * 0.5F;
		float alpha = (float) (double) MachineLinkConfig.OPACITY.get() * fade;
		float phase = LaserRenderer.pulsePhase(level.getGameTime(), partialTick);
		float nodeAlpha = alpha * nodeBreath(level.getGameTime(), partialTick);

		PoseStack poseStack = event.getPoseStack();
		Matrix4f pose = poseStack.last().pose();
		MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
		Frustum frustum = event.getFrustum();

		RIGHT.set(camera.getLeftVector()).mul(-1.0F);
		UP.set(camera.getUpVector());

		if (MachineLinkConfig.SHOW_THROUGH_BLOCKS.get()) {
			drawLinks(bufferSource, DTRenderTypes.laserOccluded(), pose, frustum, cameraPos, halfWidth, alpha * OCCLUDED_ALPHA_SCALE, phase);
		}
		drawLinks(bufferSource, DTRenderTypes.laserGlow(), pose, frustum, cameraPos, halfWidth * GLOW_WIDTH_SCALE, alpha * GLOW_ALPHA_SCALE, phase);
		drawNodes(bufferSource, DTRenderTypes.laserGlow(), pose, frustum, cameraPos, halfWidth * NODE_RADIUS_SCALE, nodeAlpha * GLOW_ALPHA_SCALE);
		drawLinks(bufferSource, DTRenderTypes.laserCore(), pose, frustum, cameraPos, halfWidth, alpha, phase);
		drawNodes(bufferSource, DTRenderTypes.laserCore(), pose, frustum, cameraPos, halfWidth * NODE_RADIUS_SCALE * 0.5F, nodeAlpha);
	}

	private static void drawLinks(
			MultiBufferSource.BufferSource bufferSource,
			RenderType renderType,
			Matrix4f pose,
			Frustum frustum,
			Vec3 cameraPos,
			float halfWidth,
			float alpha,
			float phase
	) {
		VertexConsumer consumer = bufferSource.getBuffer(renderType);
		for (MachineLinkScanner.Link link : MachineLinkScanner.links()) {
			if (!frustum.isVisible(link.bounds())) {
				continue;
			}
			Vec3 from = link.from();
			Vec3 to = link.to();
			LaserRenderer.beam(
					consumer,
					pose,
					from.x - cameraPos.x, from.y - cameraPos.y, from.z - cameraPos.z,
					to.x - cameraPos.x, to.y - cameraPos.y, to.z - cameraPos.z,
					halfWidth,
					link.color(),
					alpha,
					phase
			);
		}
		bufferSource.endBatch(renderType);
	}

	private static void drawNodes(
			MultiBufferSource.BufferSource bufferSource,
			RenderType renderType,
			Matrix4f pose,
			Frustum frustum,
			Vec3 cameraPos,
			float radius,
			float alpha
	) {
		VertexConsumer consumer = bufferSource.getBuffer(renderType);
		for (MachineLinkScanner.Node node : MachineLinkScanner.nodes()) {
			if (!frustum.isVisible(node.bounds())) {
				continue;
			}
			Vec3 at = node.at();
			LaserRenderer.glowPoint(
					consumer,
					pose,
					at.x - cameraPos.x, at.y - cameraPos.y, at.z - cameraPos.z,
					node.hub() ? radius * HUB_RADIUS_SCALE : radius,
					node.color(),
					alpha,
					RIGHT,
					UP
			);
		}
		bufferSource.endBatch(renderType);
	}

	private static float nodeBreath(long gameTime, float partialTick) {
		float progress = ((gameTime % NODE_PULSE_TICKS) + partialTick) / NODE_PULSE_TICKS;
		return 1.0F - NODE_PULSE_AMPLITUDE + NODE_PULSE_AMPLITUDE * Mth.cos(progress * Mth.TWO_PI);
	}
}