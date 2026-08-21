package dev.celestiacraft.deep_tech.api.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.celestiacraft.deep_tech.common.block.machine.advanced.sculk_network.port.SNItemInputPortBlockEntity;
import dev.celestiacraft.deep_tech.common.block.machine.advanced.sculk_network.port.SNItemOutputPortBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

public class SNItemPortRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {

	public SNItemPortRenderer(BlockEntityRendererProvider.Context context) {
		// 可以在这里获取一些渲染资源, 但本例不需要
	}

	@Override
	public void render(T be, float partialTick, PoseStack poseStack,
	                   MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

		// ----- 1. 获取过滤物品 -----
		ItemStack filter = ItemStack.EMPTY;
		if (be instanceof SNItemInputPortBlockEntity input) {
			filter = input.getFilter();
		} else if (be instanceof SNItemOutputPortBlockEntity output) {
			filter = output.getFilter();
		}
		if (filter.isEmpty()) return;

		// ----- 2. 获取方块朝向(六面) -----
		BlockState state = be.getBlockState();
		if (!state.hasProperty(BlockStateProperties.FACING)) return;
		Direction facing = state.getValue(BlockStateProperties.FACING);

		// ----- 3. 计算渲染位置 -----
		// 方块中心 (0.5, 0.5, 0.5) + 朝向面中心偏移 (0.5 * normal) + 向内偏移 3像素 (0.1875 * -normal)
		Vec3 centerOffset = Vec3.atLowerCornerOf(facing.getNormal()).scale(0.5);
		Vec3 inwardOffset = Vec3.atLowerCornerOf(facing.getNormal()).scale(-0.1875);
		Vec3 renderPos = new Vec3(0.5, 0.5, 0.5).add(centerOffset).add(inwardOffset);

		poseStack.pushPose();
		poseStack.translate(renderPos.x, renderPos.y, renderPos.z);

		// ----- 4. 旋转使物品朝外 -----
		if (facing.getAxis().isHorizontal()) {
			float rotation = switch (facing) {
				case NORTH -> 180;
				case SOUTH -> 0;
				case WEST -> 90;
				case EAST -> -90;
				default -> 0;
			};
			poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(rotation));
		} else if (facing == Direction.UP) {
			// 朝上:物品平放在顶面
			poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-90));
		} else {
			// 朝下:物品平放在底面并翻转
			poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-90));
			poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180));
		}

		// ----- 5. 缩放 (4x4像素 ≈ 0.25倍) -----
		float scale = 0.5f;
		poseStack.scale(scale, scale, scale);

		// ----- 6. 渲染物品 -----
		Minecraft.getInstance().getItemRenderer().renderStatic(
				filter,
				ItemDisplayContext.FIXED,
				packedLight,
				OverlayTexture.NO_OVERLAY,
				poseStack,
				bufferSource,
				be.getLevel(),
				0
		);

		poseStack.popPose();
	}
}