package dev.celestiacraft.deep_tech.api.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;

public class DTRenderTypes extends RenderType {

	private static final RenderType LASER_OCCLUDED = create(
			"deep_tech_laser_occluded",
			DefaultVertexFormat.POSITION_COLOR,
			VertexFormat.Mode.QUADS,
			TRANSIENT_BUFFER_SIZE,
			false,
			false,
			CompositeState.builder()
					.setShaderState(POSITION_COLOR_SHADER)
					.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
					.setDepthTestState(NO_DEPTH_TEST)
					.setCullState(NO_CULL)
					.setWriteMaskState(COLOR_WRITE)
					.setOutputState(PARTICLES_TARGET)
					.createCompositeState(false)
	);

	private static final RenderType LASER_CORE = create(
			"deep_tech_laser_core",
			DefaultVertexFormat.POSITION_COLOR,
			VertexFormat.Mode.QUADS,
			TRANSIENT_BUFFER_SIZE,
			false,
			false,
			CompositeState.builder()
					.setShaderState(POSITION_COLOR_SHADER)
					.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
					.setCullState(NO_CULL)
					.setWriteMaskState(COLOR_WRITE)
					.setOutputState(PARTICLES_TARGET)
					.createCompositeState(false)
	);

	private static final RenderType LASER_GLOW = create(
			"deep_tech_laser_glow",
			DefaultVertexFormat.POSITION_COLOR,
			VertexFormat.Mode.QUADS,
			TRANSIENT_BUFFER_SIZE,
			false,
			false,
			CompositeState.builder()
					.setShaderState(POSITION_COLOR_SHADER)
					.setTransparencyState(ADDITIVE_TRANSPARENCY)
					.setCullState(NO_CULL)
					.setWriteMaskState(COLOR_WRITE)
					.setOutputState(PARTICLES_TARGET)
					.createCompositeState(false)
	);

	private DTRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean crumbling, boolean sorting, Runnable setup, Runnable clear) {
		super(name, format, mode, bufferSize, crumbling, sorting, setup, clear);
		throw new IllegalStateException("DTRenderTypes must not be instantiated");
	}

	/** 穿墙显示的微弱轮廓 */
	public static RenderType laserOccluded() {
		return LASER_OCCLUDED;
	}

	/** 激光主体, 半透明混合 */
	public static RenderType laserCore() {
		return LASER_CORE;
	}

	/** 激光外发光, 叠加混合 */
	public static RenderType laserGlow() {
		return LASER_GLOW;
	}
}
