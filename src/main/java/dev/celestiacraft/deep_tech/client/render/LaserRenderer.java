package dev.celestiacraft.deep_tech.client.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class LaserRenderer {
	private static final float SEGMENTS_PER_BLOCK = 2.0F;
	private static final int MIN_SEGMENTS = 2;
	private static final int MAX_SEGMENTS = 48;
	private static final float PULSE_DENSITY = 0.35F;
	private static final float PULSE_RATE = 2.1F;
	private static final int PULSE_CYCLE_TICKS = 200;
	private static final float PULSE_BASE = 0.35F;
	private static final int POINT_SECTORS = 8;
	private static final double EPSILON = 1.0E-6D;

	public static float pulsePhase(long gameTime, float partialTick) {
		return ((gameTime % PULSE_CYCLE_TICKS) + partialTick) / 20.0F * PULSE_RATE;
	}

	public static void beam(
			VertexConsumer consumer,
			Matrix4f pose,
			double fromX,
			double fromY,
			double fromZ,
			double toX,
			double toY,
			double toZ,
			float halfWidth,
			int color,
			float alpha,
			float phase
	) {
		double deltaX = toX - fromX;
		double deltaY = toY - fromY;
		double deltaZ = toZ - fromZ;
		double length = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
		if (length < EPSILON) {
			return;
		}

		double dirX = deltaX / length;
		double dirY = deltaY / length;
		double dirZ = deltaZ / length;

		int segments = Mth.clamp((int) (length * SEGMENTS_PER_BLOCK), MIN_SEGMENTS, MAX_SEGMENTS);
		float pulses = Math.max(1.0F, (float) length * PULSE_DENSITY);
		boolean pulsing = phase >= 0.0F;

		int red = FastColor.ARGB32.red(color);
		int green = FastColor.ARGB32.green(color);
		int blue = FastColor.ARGB32.blue(color);

		float headAlpha = pulsing ? intensity(0.0F, pulses, phase) * alpha : alpha;
		for (int segment = 0; segment < segments; segment++) {
			float head = (float) segment / segments;
			float tail = (float) (segment + 1) / segments;

			double headX = fromX + deltaX * head;
			double headY = fromY + deltaY * head;
			double headZ = fromZ + deltaZ * head;
			double tailX = fromX + deltaX * tail;
			double tailY = fromY + deltaY * tail;
			double tailZ = fromZ + deltaZ * tail;

			double midX = (headX + tailX) * 0.5D;
			double midY = (headY + tailY) * 0.5D;
			double midZ = (headZ + tailZ) * 0.5D;
			double sideX = dirY * midZ - dirZ * midY;
			double sideY = dirZ * midX - dirX * midZ;
			double sideZ = dirX * midY - dirY * midX;
			double sideLength = Math.sqrt(sideX * sideX + sideY * sideY + sideZ * sideZ);
			if (sideLength < EPSILON) {
				sideX = dirY;
				sideY = -dirX;
				sideZ = 0.0D;
				sideLength = Math.sqrt(sideX * sideX + sideY * sideY);
				if (sideLength < EPSILON) {
					sideX = 1.0D;
					sideY = 0.0D;
					sideLength = 1.0D;
				}
			}
			double scale = halfWidth / sideLength;
			sideX *= scale;
			sideY *= scale;
			sideZ *= scale;

			float tailAlpha = pulsing ? intensity(tail, pulses, phase) * alpha : alpha;
			int headColor = FastColor.ARGB32.color(alphaByte(headAlpha), red, green, blue);
			int tailColor = FastColor.ARGB32.color(alphaByte(tailAlpha), red, green, blue);

			vertex(consumer, pose, headX - sideX, headY - sideY, headZ - sideZ, headColor);
			vertex(consumer, pose, tailX - sideX, tailY - sideY, tailZ - sideZ, tailColor);
			vertex(consumer, pose, tailX + sideX, tailY + sideY, tailZ + sideZ, tailColor);
			vertex(consumer, pose, headX + sideX, headY + sideY, headZ + sideZ, headColor);

			headAlpha = tailAlpha;
		}
	}

	public static void glowPoint(
			VertexConsumer consumer,
			Matrix4f pose,
			double atX,
			double atY,
			double atZ,
			float radius,
			int color,
			float alpha,
			Vector3f right,
			Vector3f up
	) {
		int red = FastColor.ARGB32.red(color);
		int green = FastColor.ARGB32.green(color);
		int blue = FastColor.ARGB32.blue(color);
		int center = FastColor.ARGB32.color(alphaByte(alpha), red, green, blue);
		int edge = FastColor.ARGB32.color(0, red, green, blue);

		for (int sector = 0; sector < POINT_SECTORS; sector++) {
			float startAngle = Mth.TWO_PI * sector / POINT_SECTORS;
			float endAngle = Mth.TWO_PI * (sector + 1) / POINT_SECTORS;

			float startCos = Mth.cos(startAngle) * radius;
			float startSin = Mth.sin(startAngle) * radius;
			float endCos = Mth.cos(endAngle) * radius;
			float endSin = Mth.sin(endAngle) * radius;

			vertex(consumer, pose, atX, atY, atZ, center);
			vertex(consumer, pose,
					atX + right.x * startCos + up.x * startSin,
					atY + right.y * startCos + up.y * startSin,
					atZ + right.z * startCos + up.z * startSin,
					edge);
			vertex(consumer, pose,
					atX + right.x * endCos + up.x * endSin,
					atY + right.y * endCos + up.y * endSin,
					atZ + right.z * endCos + up.z * endSin,
					edge);
			vertex(consumer, pose, atX, atY, atZ, center);
		}
	}

	private static float intensity(float progress, float pulses, float phase) {
		float trail = 1.0F - Mth.frac(progress * pulses - phase);
		return PULSE_BASE + (1.0F - PULSE_BASE) * trail * trail * trail;
	}

	private static int alphaByte(float alpha) {
		return (int) (Mth.clamp(alpha, 0.0F, 1.0F) * 255.0F);
	}

	private static void vertex(VertexConsumer consumer, Matrix4f pose, double x, double y, double z, int argb) {
		consumer.vertex(pose, (float) x, (float) y, (float) z).color(argb).endVertex();
	}
}
