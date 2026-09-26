package dev.celestiacraft.deep_tech.client.render;

import dev.celestiacraft.deep_tech.common.item.TestTubeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(
		modid = "deep_tech",
		value = Dist.CLIENT,
		bus = Mod.EventBusSubscriber.Bus.MOD
)
public final class TestTubeModelHandler {
	/** 覆盖层所在的 tintIndex（layer1 = 1） */
	private static final int OVERLAY_TINT = 1;
	/** 模型在模型管理器中的完整键名 */
	private static final String MODEL_KEY = "deep_tech:test_tube#inventory";

	private TestTubeModelHandler() {
	}

	@SubscribeEvent
	public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
		event.getModels().replaceAll((location, model) ->
				location.toString().equals(MODEL_KEY) ? new OverridingModel(model) : model
		);
	}

	private static final class OverridingModel implements BakedModel {
		private final BakedModel base;

		OverridingModel(BakedModel base) {
			this.base = base;
		}

		@Override
		public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand) {
			return base.getQuads(state, side, rand);
		}

		@Override
		public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType) {
			return base.getQuads(state, side, rand, data, renderType);
		}

		@Override public boolean useAmbientOcclusion() { return base.useAmbientOcclusion(); }
		@Override public boolean isGui3d() { return base.isGui3d(); }
		@Override public boolean usesBlockLight() { return base.usesBlockLight(); }
		@Override public boolean isCustomRenderer() { return base.isCustomRenderer(); }
		@Override public @NotNull TextureAtlasSprite getParticleIcon() { return base.getParticleIcon(); }
		@Override public @NotNull ItemTransforms getTransforms() { return base.getTransforms(); }

		@Override
		public @NotNull ItemOverrides getOverrides() {
			return new FluidOverrides();
		}
	}

	private static final class FluidOverrides extends ItemOverrides {
		@Override
		public @Nullable BakedModel resolve(@NotNull BakedModel model, @NotNull ItemStack stack,
		                                    @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
			FluidStack fluid = TestTubeItem.getFluid(stack);
			if (fluid.isEmpty()) {
				return model;
			}

			TextureAtlasSprite sprite = Minecraft.getInstance()
					.getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
					.apply(IClientFluidTypeExtensions.of(fluid.getFluid()).getStillTexture(fluid));

			if (sprite == null) {
				return model;
			}

			return new FluidOverlayModel(model, sprite);
		}
	}

	private static final class FluidOverlayModel implements BakedModel {
		private final BakedModel base;
		private final TextureAtlasSprite fluidSprite;

		FluidOverlayModel(BakedModel base, TextureAtlasSprite fluidSprite) {
			this.base = base;
			this.fluidSprite = fluidSprite;
		}

		@Override
		public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand) {
			return replace(base.getQuads(state, side, rand));
		}

		@Override
		public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType) {
			return replace(base.getQuads(state, side, rand, data, renderType));
		}

		private List<BakedQuad> replace(List<BakedQuad> quads) {
			if (quads.isEmpty()) {
				return quads;
			}
			List<BakedQuad> result = new ArrayList<>(quads.size());
			for (BakedQuad quad : quads) {
				if (quad.getTintIndex() == OVERLAY_TINT) {
					int[] vertices = remapUV(quad.getVertices(), quad.getSprite(), fluidSprite);
					result.add(new BakedQuad(
							vertices,
							OVERLAY_TINT,
							quad.getDirection(),
							fluidSprite,
							quad.isShade()
					));
				} else {
					result.add(quad);
				}
			}
			return result;
		}

		/**
		 * BakedQuad 顶点的 UV 是 atlas 绝对坐标，不是相对 sprite 的 0-16。
		 * 换 sprite 必须把 UV 从旧 sprite 范围线性映射到新 sprite 范围。
		 * 动画纹理的 sprite 在 atlas 中覆盖所有帧，这里只取第一帧。
		 */
		private int[] remapUV(int[] vertices, TextureAtlasSprite oldSprite, TextureAtlasSprite newSprite) {
			int[] result = vertices.clone();

			float oldU0 = oldSprite.getU0();
			float oldU1 = oldSprite.getU1();
			float oldV0 = oldSprite.getV0();
			float oldV1 = oldSprite.getV1();

			float newU0 = newSprite.getU0();
			float newU1 = newSprite.getU1();
			float newV0 = newSprite.getV0();
			float newV1 = newSprite.getV1();

			// 动画纹理的 sprite 在 atlas 中覆盖所有帧。
			// 流体纹理的帧都是正方形，所以单帧的 V 跨度 = U 跨度。
			float singleFrameVSpan = Math.min(newV1 - newV0, newU1 - newU0);
			newV1 = newV0 + singleFrameVSpan;

			float oldUSpan = oldU1 - oldU0;
			float oldVSpan = oldV1 - oldV0;

			for (int i = 0; i < 4; i++) {
				int base = i * 8;
				float u = Float.intBitsToFloat(result[base + 4]);
				float v = Float.intBitsToFloat(result[base + 5]);

				float tU = oldUSpan == 0 ? 0 : (u - oldU0) / oldUSpan;
				float tV = oldVSpan == 0 ? 0 : (v - oldV0) / oldVSpan;

				float newU = newU0 + tU * (newU1 - newU0);
				float newV = newV0 + tV * (newV1 - newV0);

				result[base + 4] = Float.floatToRawIntBits(newU);
				result[base + 5] = Float.floatToRawIntBits(newV);
			}
			return result;
		}

		@Override public boolean useAmbientOcclusion() { return base.useAmbientOcclusion(); }
		@Override public boolean isGui3d() { return base.isGui3d(); }
		@Override public boolean usesBlockLight() { return base.usesBlockLight(); }
		@Override public boolean isCustomRenderer() { return base.isCustomRenderer(); }
		@Override public @NotNull TextureAtlasSprite getParticleIcon() { return base.getParticleIcon(); }
		@Override public @NotNull ItemTransforms getTransforms() { return base.getTransforms(); }

		@Override
		public @NotNull ItemOverrides getOverrides() {
			return ItemOverrides.EMPTY;
		}
	}
}