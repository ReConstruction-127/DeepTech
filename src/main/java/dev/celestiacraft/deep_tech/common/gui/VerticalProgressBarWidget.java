package dev.celestiacraft.deep_tech.common.gui;

import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import net.minecraft.client.gui.GuiGraphics;

import java.util.function.Supplier;

/**
 * 垂直进度条（从下往上填充）
 */
public class VerticalProgressBarWidget extends Widget {
    private final Supplier<Integer> progressGetter;
    private final Supplier<Integer> maxProgressGetter;

    private final ResourceTexture background;
    private final ResourceTexture foreground;

    public VerticalProgressBarWidget(int x, int y, int width, int height,
                                     Supplier<Integer> progressGetter,
                                     Supplier<Integer> maxProgressGetter,
                                     ResourceTexture background,
                                     ResourceTexture foreground) {
        super(x, y, width, height);
        this.progressGetter = progressGetter;
        this.maxProgressGetter = maxProgressGetter;
        this.background = background;
        this.foreground = foreground;
    }

    @Override
    public void drawInBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int progress = progressGetter.get();
        int maxProgress = maxProgressGetter.get();

        // 背景纹理
        background.draw(graphics, mouseX, mouseY,
                getPosition().x, getPosition().y,
                getSize().width, getSize().height);

        if (progress <= 0 || maxProgress <= 0) {
            return;
        }

        // 计算比例（从 0 到 1）
        float ratio = (float) progress / maxProgress;
        if (ratio > 1.0f) ratio = 1.0f;

        // ✅ 垂直方向：从下往上计算显示高度
        int displayHeight = (int) (getSize().height * ratio);

        // ✅ 裁剪区域：从底部向上
        graphics.enableScissor(
                getPosition().x,
                getPosition().y + getSize().height - displayHeight,
                getPosition().x + getSize().width,
                getPosition().y + getSize().height
        );

        foreground.draw(graphics, mouseX, mouseY,
                getPosition().x, getPosition().y,
                getSize().width, getSize().height);

        graphics.disableScissor();
    }
}