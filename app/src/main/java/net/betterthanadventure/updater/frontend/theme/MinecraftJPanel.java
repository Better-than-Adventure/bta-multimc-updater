package net.betterthanadventure.updater.frontend.theme;

import org.jetbrains.annotations.NotNull;

public class MinecraftJPanel extends TexturedJPanel {
    public MinecraftJPanel(final float brightness) {
        super("/image/background.png", brightness, 2);
    }
}
