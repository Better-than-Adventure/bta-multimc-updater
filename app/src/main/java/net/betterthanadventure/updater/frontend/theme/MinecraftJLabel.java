package net.betterthanadventure.updater.frontend.theme;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class MinecraftJLabel extends JLabel {
    private int rgb;
    private final boolean shadowed;

    public MinecraftJLabel(final @NotNull String text, final int rgb, final boolean shadowed) {
        super(text);
        this.rgb = rgb;
        this.shadowed = shadowed;
        this.setFont(FontRenderer.getInstance().getFont());
        this.setPreferredSize(null);
    }

    public void setColor(final int rgb) {
        this.rgb = rgb;
    }

    @Override
    protected void paintComponent(final Graphics g) {
        final @NotNull FontRenderer fr = FontRenderer.getInstance();
        if (this.shadowed) {
            fr.drawStringShadowed(g, getText(), 0, 0, this.rgb);
        } else {
            fr.drawString(g, getText(), 0, 0, this.rgb);
        }
    }
}
