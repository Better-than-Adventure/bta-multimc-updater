package net.betterthanadventure.updater.frontend.theme;

import net.betterthanadventure.updater.backend.IProgressReporter;
import net.betterthanadventure.updater.frontend.Constants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class MinecraftJProgressBar extends JProgressBar implements IProgressReporter {
    private static final int COLOR_FOREGROUND = 0x80FF80;
    private static final int COLOR_BACKGROUND = 0x808080;

    private float progress = 0.0f;
    private boolean done = false;

    public MinecraftJProgressBar() {
        setMaximumSize(new Dimension(100 * Constants.GUI_SCALE, 2 * Constants.GUI_SCALE));
        setPreferredSize(new Dimension(100 * Constants.GUI_SCALE, 2 * Constants.GUI_SCALE));
        setBackground(new Color(0x00000000, true));
    }

    @Override
    public void initialize() {
        setProgress(0.0f);
        setDone(false);
    }

    @Override
    public void setProgress(float progress) {
        this.progress = progress;
        this.repaint();
    }

    @Override
    public void setDone(boolean done) {
        this.done = done;
    }

    @Override
    public boolean getDone() {
        return this.done;
    }

    @Override
    public void setProgressMessage(final @NotNull ProgressMessage message, final @Nullable String context) {
        // Do nothing
    }

    @Override
    protected void paintBorder(final Graphics g) {
        return;
    }

    @Override
    protected void paintComponent(Graphics g) {
        final int width = getWidth();
        final int height = getHeight();
        final int x = getWidth() / 2 - width / 2;
        final int y = 0;

        g.setColor(new Color(COLOR_BACKGROUND));
        g.fillRect(x, y, width, height);
        g.setColor(new Color(COLOR_FOREGROUND));
        g.fillRect(x, y, (int) (width * this.progress), height);
    }
}
