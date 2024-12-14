package net.betterthanadventure.updater.frontend.theme;

import net.betterthanadventure.updater.frontend.Constants;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;

public class FontRenderer {
    private static final @NotNull FontRenderer INSTANCE = new FontRenderer("/fonts/MinecraftRegular.otf");
    public static @NotNull FontRenderer getInstance() {
        return INSTANCE;
    }

    private final @NotNull Font font;
    private final int height;

    private FontRenderer(final @NotNull String path) {
        try {
            this.font = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream(path))
                .deriveFont(10.0f * Constants.GUI_SCALE);
            this.height = 8 * Constants.GUI_SCALE;
        } catch (final @NotNull Exception e) {
            throw new RuntimeException("Could not initialize font!");
        }
    }

    public @NotNull Font getFont() {
        return this.font;
    }

    public double getStringHeight() {
        return this.height;
    }

    public double getStringWidth(final @NotNull Graphics g, final @NotNull String string) {
        return this.font.getStringBounds(string, g.getFontMetrics().getFontRenderContext()).getWidth();
    }

    public void drawString(final @NotNull Graphics g, final @NotNull String string, final int x, final int y, final int rgb) {
        final @NotNull Font oldFont = g.getFont();
        final @NotNull Color oldColor = g.getColor();

        g.setFont(this.font);
        g.setColor(new Color(rgb));
        g.drawString(string, x, y + this.height - Constants.GUI_SCALE);

        g.setFont(oldFont);
        g.setColor(oldColor);
    }

    public void drawStringShadowed(final @NotNull Graphics g, final @NotNull String string, final int x, final int y, final int rgb) {
        drawString(g, string, x + Constants.GUI_SCALE, y + Constants.GUI_SCALE, (rgb & 0xFCFCFC) >> 2);
        drawString(g, string, x, y, rgb);
    }
}
