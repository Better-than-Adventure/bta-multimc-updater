package net.betterthanadventure.updater.frontend.theme;

import net.betterthanadventure.updater.frontend.Constants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MinecraftJButton extends JButton {
    private final @NotNull Image background;
    private final @NotNull Image backgroundHovered;
    private final @NotNull Image backgroundDisabled;

    private boolean mouseOver = false;

    public MinecraftJButton(final String text) {
        super(text);
        try {
            this.background = ImageIO.read(getClass().getResource("/image/button.png"));
            this.backgroundHovered = ImageIO.read(getClass().getResource("/image/button_highlighted.png"));
            this.backgroundDisabled = ImageIO.read(getClass().getResource("/image/button_disabled.png"));
        } catch (final @NotNull Exception e) {
            throw new RuntimeException("Could not initialize texture!");
        }

        setFont(FontRenderer.getInstance().getFont());
        setPreferredSize(new Dimension(getPreferredSize().width + Constants.GUI_SCALE * 14, 20 * Constants.GUI_SCALE));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(final MouseEvent e) {
                MinecraftJButton.this.mouseOver = true;
                MinecraftJButton.this.repaint();
            }

            @Override
            public void mouseExited(final MouseEvent e) {
                MinecraftJButton.this.mouseOver = false;
                MinecraftJButton.this.repaint();
            }

            @Override
            public void mousePressed(final MouseEvent e) {
                if (!MinecraftJButton.this.isEnabled()) {
                    return;
                }
                try (final @NotNull AudioInputStream stream = AudioSystem.getAudioInputStream(MinecraftJButton.class.getResourceAsStream("/sound/click.wav"))) {
                    final @NotNull Clip clip = AudioSystem.getClip();
                    clip.open(stream);
                    clip.addLineListener(event -> {
                        if (event.getType().equals(LineEvent.Type.STOP)) {
                            event.getLine().close();
                        }
                    });
                    final @NotNull FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    control.setValue(control.getMinimum() + ((control.getMaximum() - control.getMinimum()) * 0.75f));
                    clip.start();
                } catch (final @NotNull Exception ignored) { }
            }
        });
    }

    @Override
    protected void paintBorder(final Graphics g) {
        return;
    }

    @Override
    protected void paintComponent(final Graphics g) {
        final int scale = Constants.GUI_SCALE;
        final int width = 200;
        final int height = 20;
        final int leftPart = 2;
        final int rightPart = 198;
        final int innerWidth = rightPart - leftPart;

        // Determine background
        final @NotNull Image background = getButtonBackground();

        // Draw inner
        final int componentWidth = this.getWidth();
        final int widthToPaint = componentWidth - (leftPart + (width - rightPart)) * scale;
        for (int x = 0; x <= (widthToPaint / (innerWidth * scale)) + 1; x++) {
            g.drawImage(background,
                x * innerWidth * scale, 0, (x * innerWidth * scale) + (innerWidth * scale), height * scale,
                leftPart, 0, rightPart, height,
                this
            );
        }

        // Draw left part
        g.drawImage(background,
            0, 0, leftPart * scale, height * scale,
            0, 0, leftPart, height,
            this
        );

        // Draw right part
        g.drawImage(background,
            componentWidth - ((width - rightPart) * scale), 0, componentWidth, height * scale,
            rightPart, 0, width, height,
            this
        );

        // Draw font
        final @NotNull FontRenderer fr = FontRenderer.getInstance();
        final double stringHeight = fr.getStringHeight();
        final double stringWidth = fr.getStringWidth(g, getText());
        final double xStr = componentWidth / 2.0 - stringWidth / 2.0;
        final double yStr = (height * scale) / 2.0 - stringHeight / 2.0;
        if (isEnabled()) {
            fr.drawStringShadowed(g, getText(), (int) xStr, (int) yStr, getTextColor());
        } else {
            fr.drawString(g, getText(), (int) xStr, (int) yStr, getTextColor());
        }
    }

    private @NotNull Image getButtonBackground() {
        if (!isEnabled()) {
            return this.backgroundDisabled;
        } else if (this.mouseOver) {
            return this.backgroundHovered;
        } else {
            return this.background;
        }
    }

    private int getTextColor() {
        if (!isEnabled()) {
            return 0xA0A0A0;
        } else if (this.mouseOver) {
            return 0xFFFFA0;
        } else {
            return 0xE0E0E0;
        }
    }
}
