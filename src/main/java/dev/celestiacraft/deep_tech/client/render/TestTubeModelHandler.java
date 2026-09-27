package dev.celestiacraft.deep_tech.client.render;

import dev.celestiacraft.deep_tech.DeepTech;
import dev.celestiacraft.deep_tech.common.item.TestTubeItem;
import dev.celestiacraft.deep_tech.common.register.item.ToolItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 试管(item.deep_tech.test_tube)的动态外观:
 * <ul>
 *     <li>空试管: 只绘制 {@code test_tube.png}, 也就是把模型里的 Overlay 层(layer1)整层丢掉;</li>
 *     <li>装了流体: 把 Overlay 层的面裁进试管内腔, 并换成该流体 still 贴图的 sprite,
 *     sprite 本身带 .mcmeta 动画, 所以帧动画会自动播放。</li>
 * </ul>
 * <p>
 * "试管内腔"不是写死的坐标, 而是 Overlay 贴图里非透明像素的包围盒,
 * 即"Overlay 画到哪里, 流体就填到哪里", 改贴图不用改代码。
 * <p>
 * 之所以在 {@link BakedModel} 层做, 是因为 {@code item/generated} 模型的几何由
 * {@code ItemModelGenerator} 从贴图 alpha 生成, JSON 里写不了自定义 elements;
 * 但它的 layer1 面正好可以用作"流体面"的模板。
 * <p>
 * item 模型在模型管理器里的键是 {@code <物品 id>#inventory}(见
 * {@code ModelBakery.loadTop}), 而不是模型文件路径, 所以这里按
 * {@code deep_tech:test_tube#inventory} 匹配。
 */
@Mod.EventBusSubscriber(
		modid = DeepTech.MODID,
		value = Dist.CLIENT,
		bus = Mod.EventBusSubscriber.Bus.MOD
)
public final class TestTubeModelHandler {
	/** item/generated 中 layer1 对应的 tintIndex */
	private static final int OVERLAY_TINT = 1;
	/** 物品模型在模型管理器里的键是 <物品 id>#inventory */
	private static final ResourceLocation TEST_TUBE_ID = DeepTech.loadResource("test_tube");
	private static final String INVENTORY_VARIANT = "inventory";

	/** Overlay 贴图里非透明区域的包围盒缓存(换图集时会换成新的 sprite 实例) */
	private static TextureAtlasSprite cachedOverlaySprite;
	private static float[] cachedInnerRect;

	private TestTubeModelHandler() {
	}

	@SubscribeEvent
	public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
		event.getModels().replaceAll((location, model) ->
				isTestTubeModel(location) ? new TestTubeBakedModel(model) : model);
	}

	private static boolean isTestTubeModel(ResourceLocation location) {
		// ModelBakery.loadTop 为每个物品注册 new ModelResourceLocation(物品 id, "inventory")
		if (!(location instanceof ModelResourceLocation modelLocation)) {
			return false;
		}
		return TEST_TUBE_ID.getNamespace().equals(modelLocation.getNamespace())
				&& TEST_TUBE_ID.getPath().equals(modelLocation.getPath())
				&& INVENTORY_VARIANT.equals(modelLocation.getVariant());
	}

	/**
	 * 流体贴图大多是灰度的(比如原版水), 需要额外乘上流体自己的 tint 颜色。
	 * tintIndex 0 是试管本体, 返回白色即为"不染色"。
	 */
	@SubscribeEvent
	public static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
		event.register((ItemStack stack, int tintIndex) -> {
			if (tintIndex != OVERLAY_TINT) {
				return 0xFFFFFFFF;
			}

			FluidStack fluid = TestTubeItem.getFluid(stack);
			if (fluid.isEmpty()) {
				return 0xFFFFFFFF;
			}

			int color = IClientFluidTypeExtensions.of(fluid.getFluid()).getTintColor(fluid);
			return color == -1 ? 0xFFFFFFFF : color;
		}, ToolItems.TEST_TUBE.get());
	}

	/**
	 * 取流体 still 贴图在方块图集里的 sprite, 取不到(没贴图/不在图集里)时返回 null,
	 * 调用方按"空试管"处理。
	 */
	@Nullable
	private static TextureAtlasSprite fluidSprite(@NotNull ItemStack stack) {
		FluidStack fluid = TestTubeItem.getFluid(stack);
		if (fluid.isEmpty()) {
			return null;
		}

		ResourceLocation texture = IClientFluidTypeExtensions.of(fluid.getFluid()).getStillTexture(fluid);
		if (texture == null) {
			return null;
		}

		// FluidHandlerItemStack 在客户端同样可用, 但图集只在客户端加载
		TextureAtlasSprite sprite = Minecraft.getInstance()
				.getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
				.apply(texture);

		if (sprite == null || MissingTextureAtlasSprite.getLocation().equals(sprite.contents().name())) {
			return null;
		}
		return sprite;
	}

	/**
	 * 丢掉 Overlay 层的所有面, 只保留试管本体。
	 */
	private static List<BakedQuad> keepTubeOnly(List<BakedQuad> quads) {
		if (quads.isEmpty()) {
			return quads;
		}

		boolean hasOverlay = false;
		for (BakedQuad quad : quads) {
			if (quad.getTintIndex() == OVERLAY_TINT) {
				hasOverlay = true;
				break;
			}
		}
		if (!hasOverlay) {
			return quads;
		}

		List<BakedQuad> result = new ArrayList<>(quads.size());
		for (BakedQuad quad : quads) {
			if (quad.getTintIndex() != OVERLAY_TINT) {
				result.add(quad);
			}
		}
		return result;
	}

	/**
	 * 试管内腔 = Overlay 贴图(test_tube_overlay.png)里非透明像素的包围盒,
	 * 也就是说"Overlay 画到哪里, 流体就填到哪里", 改贴图不需要改代码。
	 * <p>
	 * 返回值是 {x0, y0, x1, y1}, 都以 quad 自身包围盒的比例表示;
	 * 模型空间 y 向上, 而贴图行号向下, 所以 y = 1 - 行号 / 高。
	 * 贴图里没有不透明像素(比如忘记画 Overlay)时返回 null。
	 */
	@Nullable
	private static float[] innerRect(@NotNull TextureAtlasSprite overlaySprite) {
		if (overlaySprite == cachedOverlaySprite) {
			return cachedInnerRect;
		}

		SpriteContents contents = overlaySprite.contents();
		int width = contents.width();
		int height = contents.height();

		int minX = width;
		int maxX = -1;
		int minY = height;
		int maxY = -1;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				// 参数顺序是 (帧号, x, y): 传成 (x, y, 0) 会永远去读同一行, 结果全被判成透明
				if (contents.isTransparent(0, x, y)) {
					continue;
				}
				minX = Math.min(minX, x);
				maxX = Math.max(maxX, x);
				minY = Math.min(minY, y);
				maxY = Math.max(maxY, y);
			}
		}

		float[] rect = maxX < 0 || width == 0 || height == 0
				? null
				: new float[]{
						(float) minX / width,
						(float) (height - maxY - 1) / height,
						(float) (maxX + 1) / width,
						(float) (height - minY) / height
				};

		cachedOverlaySprite = overlaySprite;
		cachedInnerRect = rect;
		return rect;
	}

	/**
	 * 把 Overlay 层的面裁进试管内腔并换成流体贴图。
	 * <p>
	 * Overlay 还有一个 1px 厚的"侧面"(由 ItemModelGenerator 按贴图 alpha 生成,
	 * 朝向 UP/DOWN/EAST/WEST), 它们是占位贴图形状的描边, 直接丢掉; 试管本身的
	 * 描边来自 layer0, 不受影响。
	 */
	@Nullable
	private static BakedQuad cropToInnerRect(@NotNull BakedQuad quad, @NotNull TextureAtlasSprite fluidSprite) {
		Direction direction = quad.getDirection();
		if (direction != Direction.NORTH && direction != Direction.SOUTH) {
			return null;
		}

		float[] innerRect = innerRect(quad.getSprite());
		if (innerRect == null) {
			return null;
		}

		int[] vertices = quad.getVertices().clone();

		float minX = Float.MAX_VALUE;
		float maxX = -Float.MAX_VALUE;
		float minY = Float.MAX_VALUE;
		float maxY = -Float.MAX_VALUE;
		for (int i = 0; i < 4; i++) {
			int base = i * 8;
			float x = Float.intBitsToFloat(vertices[base]);
			float y = Float.intBitsToFloat(vertices[base + 1]);
			minX = Math.min(minX, x);
			maxX = Math.max(maxX, x);
			minY = Math.min(minY, y);
			maxY = Math.max(maxY, y);
		}

		float spanX = maxX - minX;
		float spanY = maxY - minY;

		TextureAtlasSprite oldSprite = quad.getSprite();
		float oldU0 = oldSprite.getU0();
		float oldV0 = oldSprite.getV0();
		float oldUSpan = oldSprite.getU1() - oldU0;
		float oldVSpan = oldSprite.getV1() - oldV0;

		float newU0 = fluidSprite.getU0();
		float newV0 = fluidSprite.getV0();
		float newUSpan = fluidSprite.getU1() - newU0;
		// 动画贴图的 sprite 在纵向覆盖所有帧, getV1() 已经只到第一帧, 这里直接按第一帧取
		float newVSpan = fluidSprite.getV1() - newV0;

		for (int i = 0; i < 4; i++) {
			int base = i * 8;
			float x = Float.intBitsToFloat(vertices[base]);
			float y = Float.intBitsToFloat(vertices[base + 1]);
			float u = Float.intBitsToFloat(vertices[base + 4]);
			float v = Float.intBitsToFloat(vertices[base + 5]);

			float tx = spanX == 0 ? 0F : (x - minX) / spanX;
			float ty = spanY == 0 ? 0F : (y - minY) / spanY;

			// 位置按比例缩到内腔
			float newX = minX + spanX * (innerRect[0] + tx * (innerRect[2] - innerRect[0]));
			float newY = minY + spanY * (innerRect[1] + ty * (innerRect[3] - innerRect[1]));

			// UV 沿用原贴图的归一化比例, 保证正反面朝向与原来的 Overlay 一致
			float tu = oldUSpan == 0F ? 0F : (u - oldU0) / oldUSpan;
			float tv = oldVSpan == 0F ? 0F : (v - oldV0) / oldVSpan;

			vertices[base] = Float.floatToRawIntBits(newX);
			vertices[base + 1] = Float.floatToRawIntBits(newY);
			vertices[base + 4] = Float.floatToRawIntBits(newU0 + tu * newUSpan);
			vertices[base + 5] = Float.floatToRawIntBits(newV0 + tv * newVSpan);
		}

		return new BakedQuad(vertices, OVERLAY_TINT, direction, fluidSprite, quad.isShade());
	}

	/**
	 * 通用委托, 只覆写需要改的部分; 渲染类型等必须跟着原模型走,
	 * 否则物品的透明通道会画错。
	 */
	private abstract static class DelegateModel implements BakedModel {
		protected final BakedModel base;

		protected DelegateModel(BakedModel base) {
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

		@Override
		public boolean useAmbientOcclusion() {
			return base.useAmbientOcclusion();
		}

		@Override
		public boolean isGui3d() {
			return base.isGui3d();
		}

		@Override
		public boolean usesBlockLight() {
			return base.usesBlockLight();
		}

		@Override
		public boolean isCustomRenderer() {
			return base.isCustomRenderer();
		}

		@Override
		public @NotNull TextureAtlasSprite getParticleIcon() {
			return base.getParticleIcon();
		}

		@Override
		public @NotNull ItemTransforms getTransforms() {
			return base.getTransforms();
		}

		@Override
		public @NotNull ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
			return base.getRenderTypes(state, rand, data);
		}

		@Override
		public @NotNull List<RenderType> getRenderTypes(@NotNull ItemStack stack, boolean fabulous) {
			return base.getRenderTypes(stack, fabulous);
		}
	}

	/**
	 * 默认(未被 {@link ItemOverrides#resolve} 处理时)的模型: 空试管。
	 */
	private static final class TestTubeBakedModel extends DelegateModel {
		private final ItemOverrides overrides;

		private TestTubeBakedModel(BakedModel base) {
			super(base);
			// 传原始模型: Overlay 面要从它上面取
			this.overrides = new FluidOverrides(base);
		}

		@Override
		public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand) {
			return keepTubeOnly(base.getQuads(state, side, rand));
		}

		@Override
		public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType) {
			return keepTubeOnly(base.getQuads(state, side, rand, data, renderType));
		}

		@Override
		public @NotNull ItemOverrides getOverrides() {
			return overrides;
		}
	}

	private static final class FluidOverrides extends ItemOverrides {
		/** 未被处理的原始模型, 流体面要用它原始的 Overlay 面当模板 */
		private final BakedModel rawModel;

		private FluidOverrides(BakedModel rawModel) {
			this.rawModel = rawModel;
		}

		@Override
		public @Nullable BakedModel resolve(@NotNull BakedModel model, @NotNull ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
			TextureAtlasSprite sprite = fluidSprite(stack);
			if (sprite == null) {
				// 空试管, 或者流体没有可用的贴图: 交回默认模型(已经去掉 Overlay)
				return model;
			}
			return new FilledTubeModel(rawModel, sprite);
		}
	}

	/**
	 * 装了流体的试管, 对每一组面单独构建, 结果不缓存(每个流体一个 sprite, 开销很小)。
	 */
	private static final class FilledTubeModel extends DelegateModel {
		private final TextureAtlasSprite fluidSprite;

		private FilledTubeModel(BakedModel base, TextureAtlasSprite fluidSprite) {
			super(base);
			this.fluidSprite = fluidSprite;
		}

		@Override
		public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand) {
			return replaceOverlay(base.getQuads(state, side, rand));
		}

		@Override
		public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType) {
			return replaceOverlay(base.getQuads(state, side, rand, data, renderType));
		}

		@Override
		public @NotNull ItemOverrides getOverrides() {
			return ItemOverrides.EMPTY;
		}

		private List<BakedQuad> replaceOverlay(List<BakedQuad> quads) {
			if (quads.isEmpty()) {
				return quads;
			}

			List<BakedQuad> result = new ArrayList<>(quads.size());
			for (BakedQuad quad : quads) {
				if (quad.getTintIndex() != OVERLAY_TINT) {
					result.add(quad);
					continue;
				}

				BakedQuad fluidQuad = cropToInnerRect(quad, fluidSprite);
				if (fluidQuad != null) {
					result.add(fluidQuad);
				}
			}
			return result;
		}
	}
}
