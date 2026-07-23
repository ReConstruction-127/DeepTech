package dev.celestiacraft.deep_tech.common.gui;

import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import net.minecraft.client.gui.GuiGraphics;

import java.util.function.Supplier;

public class ProgressBarWidget extends Widget {
    private final Supplier<Integer> progressGetter;
    private final Supplier<Integer> maxProgressGetter;

    private final ResourceTexture background;
    private final ResourceTexture foreground;

    /**
     * 水平进度条
     * @param x X坐标
     * @param y Y坐标
     * @param width 宽度
     * @param height 高度
     * @param progressGetter 当前进度
     * @param maxProgressGetter 最大进度
     * @param background 背景纹理
     * @param foreground 前景纹理
     */
    public ProgressBarWidget(int x, int y, int width, int height,
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
        if (maxProgress <= 0) return;

        // 绘制背景
        background.draw(graphics, mouseX, mouseY,
                getPosition().x, getPosition().y,
                getSize().width, getSize().height);

        // 计算进度宽度
        int progressWidth = (int) ((float) progress / maxProgress * getSize().width);
        if (progressWidth <= 0) return;

        // 裁剪：只显示前景的左侧部分
        graphics.enableScissor(
                getPosition().x,
                getPosition().y,
                getPosition().x + progressWidth,
                getPosition().y + getSize().height
        );

        foreground.draw(graphics, mouseX, mouseY,
                getPosition().x, getPosition().y,
                getSize().width, getSize().height);

        graphics.disableScissor();
    }
}