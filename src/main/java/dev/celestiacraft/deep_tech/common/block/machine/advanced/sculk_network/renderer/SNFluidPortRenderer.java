package dev.celestiacraft.deep_tech.common.block.machine.advanced.sculk_network.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.celestiacraft.deep_tech.common.block.machine.advanced.sculk_network.port.SNFluidInputPortBlockEntity;
import dev.celestiacraft.deep_tech.common.block.machine.advanced.sculk_network.port.SNFluidOutputPortBlockEntity;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import org.jetbrains.annotations.NotNull;

public class SNFluidPortRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
	public SNFluidPortRenderer(BlockEntityRendererProvider.Context context) {
	}

	@Override
	public void render(
			@NotNull T blockEntity,
			float partialTick,
			@NotNull PoseStack stack,
			@NotNull MultiBufferSource source,
			int packedLight,
			int packedOverlay
	) {
		// ----- 1. 获取过滤流体, 并转换成物品(桶)用于渲染 -----
		FluidStack filterFluid = FluidStack.EMPTY;
		if (blockEntity instanceof SNFluidInputPortBlockEntity input) {
			filterFluid = input.getFilter();
		} else if (blockEntity instanceof SNFluidOutputPortBlockEntity output) {
			filterFluid = output.getFilter();
		}
		if (filterFluid.isEmpty()) {
			return;
		}

		// 将流体转换成物品(桶), 用于渲染
		ItemStack filterItem = FluidUtil.getFilledBucket(filterFluid);
		// 注意: 如果流体没有桶装形态, getFilledBucket 可能返回空, 此时需要回退方案.
		// 可以使用 FluidHelper 或直接渲染流体材质, 但为了简化, 我们先假设流体有桶.
		if (filterItem.isEmpty()) {
			// 如果拿不到桶, 可以尝试用 FluidStack 的渲染, 但比较复杂, 这里简单跳过
			return;
		}

		// ----- 2. 获取方块朝向(六面) -----
		BlockState state = blockEntity.getBlockState();
		if (!state.hasProperty(BlockStateProperties.FACING)) {
			return;
		}
		Direction facing = state.getValue(BlockStateProperties.FACING);

		// ----- 3. 计算渲染位置 -----
		Vec3 centerOffset = Vec3.atLowerCornerOf(facing.getNormal()).scale(0.5);
		Vec3 inwardOffset = Vec3.atLowerCornerOf(facing.getNormal()).scale(-0.1875);
		Vec3 renderPos = new Vec3(0.5, 0.5, 0.5).add(centerOffset).add(inwardOffset);

		stack.pushPose();
		stack.translate(renderPos.x, renderPos.y, renderPos.z);

		// ----- 4. 旋转使桶朝外 -----
		if (facing.getAxis().isHorizontal()) {
			float rotation = switch (facing) {
				case NORTH -> 180;
				case WEST -> 90;
				case EAST -> -90;
				default -> 0;
			};
			stack.mulPose(Axis.YP.rotationDegrees(rotation));
		} else if (facing == Direction.UP) {
			// 朝上:桶平放在顶面
			stack.mulPose(Axis.XP.rotationDegrees(-90));
		} else {
			// 朝下:桶平放在底面并翻转
			stack.mulPose(Axis.XP.rotationDegrees(-90));
			stack.mulPose(Axis.YP.rotationDegrees(180));
		}

		// ----- 5. 缩放 (4x4像素) -----
		float scale = 0.25f;
		stack.scale(scale, scale, scale);

		// ----- 6. 渲染桶(包含流体纹理) -----
		Minecraft.getInstance().getItemRenderer().renderStatic(
				filterItem,
				ItemDisplayContext.FIXED,
				packedLight,
				OverlayTexture.NO_OVERLAY,
				stack,
				source,
				blockEntity.getLevel(),
				0
		);

		stack.popPose();
	}
}