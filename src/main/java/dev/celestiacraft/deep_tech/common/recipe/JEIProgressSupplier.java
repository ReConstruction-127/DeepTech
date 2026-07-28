package dev.celestiacraft.deep_tech.common.recipe;

public class JEIProgressSupplier {
    public static final JEIProgressSupplier INSTANCE = new JEIProgressSupplier();

    private long lastUpdate = 0;
    private int currentProgress = 0;

    private JEIProgressSupplier() {}

    public int getProgress() {
        long now = System.currentTimeMillis();
        if (now - lastUpdate > 50) {
            currentProgress = (currentProgress + 1) % 101; // 0 ~ 100 循环
            lastUpdate = now;
        }
        return currentProgress;
    }
}