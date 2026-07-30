package dev.celestiacraft.deep_tech.api.gui;

import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;


public class ProgressBarWidget extends Widget {
	private final Supplier<Integer> progressGetter;
	private final Supplier<Integer> maxProgressGetter;

	private final ResourceTexture background;
	private final ResourceTexture foreground;

	public ProgressBarWidget(
			int x,
			int y,
			int width,
			int height,
			Supplier<Integer> progressGetter,
			Supplier<Integer> maxProgressGetter,
			ResourceTexture background,
			ResourceTexture foreground
	) {
		super(x, y, width, height);
		this.progressGetter = progressGetter;
		this.maxProgressGetter = maxProgressGetter;
		this.background = background;
		this.foreground = foreground;
	}

	@Override
	public void drawInBackground(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		int progress = progressGetter.get();
		int maxProgress = maxProgressGetter.get();

		// ========== 1. 背景纹理 ==========
		background.draw(
				graphics,
				mouseX,
				mouseY,
				getPosition().x,
				getPosition().y,
				getSize().width,
				getSize().height
		);

		// ========== 2. 无进度时不显示前景 ==========
		if (progress <= 0 || maxProgress <= 0) {
			return;
		}

		// ========== 3. 计算进度比例 ==========
		float ratio = (float) progress / maxProgress;
		if (ratio > 1.0f) ratio = 1.0f;

		int displayWidth = (int) (getSize().width * ratio);

		// ========== 调试日志（只在客户端输出，避免刷屏） ==========
		if (progress % 10 == 0) {
		}

		// ========== 4. 裁剪绘制前景 ==========
		// 注意：enableScissor 的参数是 (x1, y1, x2, y2)，即左上角和右下角
		graphics.enableScissor(
				getPosition().x,
				getPosition().y,
				getPosition().x + displayWidth,
				getPosition().y + getSize().height
		);

		foreground.draw(
				graphics,
				mouseX,
				mouseY,
				getPosition().x,
				getPosition().y,
				getSize().width,
				getSize().height
		);

		graphics.disableScissor();
	}
}