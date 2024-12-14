package net.betterthanadventure.updater.frontend.theme;

import net.betterthanadventure.updater.frontend.Constants;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class TexturedJPanel extends JPanel {
    private final float brightness;
    private final int scale;
    private final @NotNull Image background;

    public TexturedJPanel(final @NotNull String path, final float brightness, final int scale) {
        this.brightness = brightness;
        this.scale = scale;
        try {
            this.background = ImageIO.read(getClass().getResource(path));
        } catch (final @NotNull Exception e) {
            throw new RuntimeException("Could not initialize texture!");
        }
    }

    @Override
    protected void paintComponent(final Graphics g) {
        final int scale = Constants.GUI_SCALE;
        final int width = 16;
        final int height = 16;
        final int scaledWidth = width * scale * this.scale;
        final int scaledHeight = height * scale * this.scale;

        for (int x = 0; x <= this.getWidth() / scaledWidth; x++) {
            for (int y = 0; y <= this.getHeight() / scaledHeight; y++) {
                g.drawImage(this.background, x * scaledWidth, y * scaledHeight, scaledWidth, scaledHeight, this);
            }
        }

        g.setColor(new Color(0, 0, 0, (int) (255 * (1.0f - this.brightness))));
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}
