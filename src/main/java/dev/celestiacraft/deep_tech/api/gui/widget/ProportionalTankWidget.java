package dev.celestiacraft.deep_tech.api.gui.widget;

import com.lowdragmc.lowdraglib.gui.util.DrawerHelper;
import com.lowdragmc.lowdraglib.gui.widget.TankWidget;
import com.lowdragmc.lowdraglib.side.fluid.FluidStack;
import com.lowdragmc.lowdraglib.side.fluid.IFluidTransfer;
import net.minecraft.client.gui.GuiGraphics;

/**
 * 比例填充式流体槽:TankWidget 的桶点击互动 + 传统"按流体量填充高度"的渲染. 
 * 取代 LDLib 默认的全格填充 + 数字显示. 
 * <p>
 * 渲染数据来自父类自带的同步缓存 lastFluidInTank/lastTankCapacity
 * (服务端 detectAndSendChanges -> writeUpdateInfo,客户端 readUpdateInfo 恢复),
 * 不要直接读客户端 BE,BE 数据不经过该同步链路,会是空值. 
 */
public class ProportionalTankWidget extends TankWidget {

	public ProportionalTankWidget(IFluidTransfer fluidTank, int tank, int x, int y, int width, int height,
	                              boolean allowClickFilled, boolean allowClickDrained) {
		super(fluidTank, tank, x, y, width, height, allowClickFilled, allowClickDrained);
	}

	@Override
	public void drawInBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		// 背景贴图
		drawBackgroundTexture(graphics, mouseX, mouseY);

		if (lastFluidInTank == null || lastFluidInTank.isEmpty()) {
			return;
		}

		int x = getPosition().x;
		int y = getPosition().y;
		int width = getSize().width;
		int height = getSize().height;

		long capacity = Math.max(1L, lastTankCapacity);
		float ratio = (float) lastFluidInTank.getAmount() / capacity;
		if (ratio > 1.0f) {
			ratio = 1.0f;
		}
		float fillHeight = height * ratio;
		if (fillHeight < 1.0f) {
			return; // 不足 1 像素时不绘制
		}
		float fillY = y + height - fillHeight;
		DrawerHelper.drawFluidForGui(graphics, lastFluidInTank, x, fillY, width, fillHeight);
	}
}