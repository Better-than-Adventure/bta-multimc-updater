package net.betterthanadventure.updater.frontend;

import net.betterthanadventure.updater.backend.BackendManager;
import net.betterthanadventure.updater.backend.downloads.Channel;
import net.betterthanadventure.updater.backend.downloads.Version;
import net.betterthanadventure.updater.frontend.theme.MinecraftJButton;
import net.betterthanadventure.updater.frontend.theme.MinecraftJLabel;
import net.betterthanadventure.updater.frontend.theme.MinecraftJPanel;
import net.betterthanadventure.updater.frontend.theme.MinecraftJProgressBar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ResourceBundle;

public class SimpleModePanel extends MinecraftJPanel {
    private enum PanelState {
        MAIN_PANEL_VISIBLE,
        HELP_PANEL_VISIBLE
    }

    private static final int COLOR_YELLOW = 0xFFFF00;
    private static final int COLOR_RED = 0xFF0000;
    private static final int COLOR_GREEN = 0x00FF00;

    private final @NotNull ResourceBundle resources;
    private final @NotNull BackendManager backendManager;

    private final @NotNull JLabel installedLabel;
    private final @NotNull MinecraftJLabel statusLabel;
    private final @NotNull JButton installUpdateButton;
    private final @NotNull JButton helpButton;
    private final @NotNull MinecraftJProgressBar progressBar;
    private final @NotNull JPanel mainPanel;
    private final @NotNull JPanel helpPanel;
    private final @NotNull Timer timer;

    private @NotNull PanelState panelState = PanelState.MAIN_PANEL_VISIBLE;
    private @NotNull String latestVersion = "";
    private @NotNull String installedVersion = "";
    private boolean forcedUpdate = false;
    private boolean hasUpdated = false;

    public SimpleModePanel(final @NotNull ResourceBundle resources, final @NotNull BackendManager backendManager) {
        super(0.25f);

        this.resources = resources;
        this.backendManager = backendManager;

        this.setLayout(new BorderLayout());

        // Upper panel
        final @NotNull JPanel upperPanel = new JPanel();
        upperPanel.setLayout(new BorderLayout());
        upperPanel.setBackground(new Color(0x00000000, true));
        upperPanel.setBorder(new EmptyBorder(2 * Constants.GUI_SCALE, 2 * Constants.GUI_SCALE, 2 * Constants.GUI_SCALE, 2 * Constants.GUI_SCALE));
        add(upperPanel, BorderLayout.NORTH);

        final @NotNull JLabel versionLabel = new MinecraftJLabel(resources.getString("frontend.main_window.simple.version_label.label"), 0x505050, true);
        upperPanel.add(versionLabel, BorderLayout.LINE_START);

        this.helpButton = new MinecraftJButton("?");
        this.helpButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent e) {
                if (!SimpleModePanel.this.helpButton.isEnabled()) {
                    return;
                }

                if (SimpleModePanel.this.panelState == PanelState.MAIN_PANEL_VISIBLE) {
                    SimpleModePanel.this.remove(SimpleModePanel.this.mainPanel);
                    SimpleModePanel.this.add(SimpleModePanel.this.helpPanel, BorderLayout.CENTER);
                    SimpleModePanel.this.panelState = PanelState.HELP_PANEL_VISIBLE;
                } else if (SimpleModePanel.this.panelState == PanelState.HELP_PANEL_VISIBLE) {
                    SimpleModePanel.this.remove(SimpleModePanel.this.helpPanel);
                    SimpleModePanel.this.add(SimpleModePanel.this.mainPanel, BorderLayout.CENTER);
                    SimpleModePanel.this.panelState = PanelState.MAIN_PANEL_VISIBLE;
                }
                SimpleModePanel.this.revalidate();
                SimpleModePanel.this.repaint();
            }
        });
        upperPanel.add(this.helpButton, BorderLayout.LINE_END);

        // Main panel
        this.mainPanel = new JPanel();
        this.mainPanel.setBackground(new Color(0x00000000, true));
        this.mainPanel.setLayout(new BoxLayout(this.mainPanel, BoxLayout.Y_AXIS));
        add(this.mainPanel, BorderLayout.CENTER);

        try {
            final @NotNull BufferedImage image = ImageIO.read(getClass().getResource("/image/logo-header.png"));
            final @NotNull Image scaledImage = image.getScaledInstance((int) (image.getWidth() / (2.0 / Constants.GUI_SCALE)), (int) (image.getHeight() / (2.0 / Constants.GUI_SCALE)), Image.SCALE_FAST);
            final @NotNull ImageIcon icon = new ImageIcon(scaledImage);
            final @NotNull JLabel imageLabel = new JLabel(icon);
            imageLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
            this.mainPanel.add(imageLabel);
        } catch (final @NotNull IOException ignored) { }

        this.installedLabel = new MinecraftJLabel("", 0xFFFFFF, true);
        this.installedLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        this.mainPanel.add(this.installedLabel);

        this.statusLabel = new MinecraftJLabel("", COLOR_YELLOW, true);
        this.statusLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        this.mainPanel.add(this.statusLabel);
        this.mainPanel.add(Box.createVerticalGlue());

        this.installUpdateButton = new MinecraftJButton("");
        this.installUpdateButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        this.mainPanel.add(this.installUpdateButton);

        this.progressBar = new MinecraftJProgressBar();
        this.progressBar.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        this.progressBar.setVisible(false);
        this.mainPanel.add(this.progressBar);
        this.mainPanel.add(Box.createVerticalGlue());

        // Help panel
        this.helpPanel = new JPanel();
        this.helpPanel.setBackground(new Color(0x00000000, true));
        this.helpPanel.setBorder(new EmptyBorder(2 * Constants.GUI_SCALE, 2 * Constants.GUI_SCALE, 2 * Constants.GUI_SCALE, 2 * Constants.GUI_SCALE));
        this.helpPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        for (int i = 0; i <= 10; i++) {
            final @NotNull JLabel helpLabel = new MinecraftJLabel(resources.getString("frontend.main_window.simple.help_label.label." + i), 0xDFDFDF, true);
            this.helpPanel.add(helpLabel);
        }

        updateState();

        this.timer = new Timer(100, e -> {
            if (this.progressBar.getDone()) {
                SimpleModePanel.this.installUpdateButton.setVisible(true);
                SimpleModePanel.this.progressBar.setVisible(false);
                SimpleModePanel.this.timer.stop();
                SimpleModePanel.this.forcedUpdate = false;
                SimpleModePanel.this.updateState();
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0), "forced_toggle");
        getActionMap().put("forced_toggle", new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                SimpleModePanel.this.forcedUpdate = !SimpleModePanel.this.forcedUpdate;
                updateState();
            }
        });

        this.installUpdateButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent e) {
                if (!SimpleModePanel.this.installUpdateButton.isEnabled()) {
                    return;
                }

                final @NotNull Channel channel = SimpleModePanel.this.backendManager.getDownloadManager().getChannels().get(1);
                final @NotNull Version version = channel.getDefaultVersion();

                SimpleModePanel.this.installUpdateButton.setVisible(false);
                SimpleModePanel.this.progressBar.setVisible(true);

                SimpleModePanel.this.hasUpdated = false;
                SimpleModePanel.this.statusLabel.setText(resources.getString("frontend.main_window.simple.status_label.label.updating"));
                SimpleModePanel.this.statusLabel.setColor(COLOR_YELLOW);

                SimpleModePanel.this.repaint();

                final @NotNull Thread t = new Thread(() -> {
                    try {
                        SimpleModePanel.this.timer.setRepeats(true);
                        SimpleModePanel.this.timer.start();
                        SimpleModePanel.this.backendManager.getInstanceManager().synchronize(channel, version, SimpleModePanel.this.progressBar);
                        SimpleModePanel.this.statusLabel.setText(resources.getString("frontend.main_window.simple.status_label.label.update_complete"));
                        SimpleModePanel.this.statusLabel.setColor(COLOR_GREEN);
                        SimpleModePanel.this.hasUpdated = true;
                    } catch (final @NotNull Exception ex) {
                        ex.printStackTrace();
                        SimpleModePanel.this.statusLabel.setText(resources.getString("frontend.main_window.simple.status_label.label.update_failed"));
                        SimpleModePanel.this.statusLabel.setColor(COLOR_RED);
                        SimpleModePanel.this.hasUpdated = true;
                    }
                });
                t.setDaemon(true);
                t.start();
            }
        });
    }

    private void updateState() {
        this.latestVersion = this.backendManager.getDownloadManager().getChannels().get(1).getDefaultVersion().getId();
        if (!this.backendManager.getInstanceManager().exists()) {
            this.installedVersion = this.resources.getString("frontend.main_window.simple.no_version");
        } else {
            final @Nullable String installedVersion = this.backendManager.getInstanceManager().getVersion();
            if (installedVersion == null) {
                this.installedVersion = this.resources.getString("frontend.main_window.simple.unknown_version");
            } else {
                this.installedVersion = installedVersion;
            }
        }

        this.installedLabel.setText(String.format(this.resources.getString("frontend.main_window.simple.installed_version_label.label"), this.installedVersion));

        @Nullable String statusText = null;
        int statusColor = COLOR_YELLOW;

        if (this.installedVersion.equals(this.latestVersion) && !this.forcedUpdate) {
            statusText = this.resources.getString("frontend.main_window.simple.status_label.label.up_to_date");
            statusColor = COLOR_YELLOW;
            this.installUpdateButton.setText(this.resources.getString("frontend.main_window.simple.install_update_button.label.none_available"));
            this.installUpdateButton.setEnabled(false);
        } else {
            statusText = this.resources.getString("frontend.main_window.simple.status_label.label.update_available");
            statusColor = COLOR_YELLOW;
            if (this.backendManager.getInstanceManager().exists()) {
                this.installUpdateButton.setText(String.format(this.resources.getString("frontend.main_window.simple.install_update_button.label.update"), this.latestVersion));
            } else {
                this.installUpdateButton.setText(String.format(this.resources.getString("frontend.main_window.simple.install_update_button.label.install"), this.latestVersion));
            }
            this.installUpdateButton.setEnabled(true);
        }

        if (statusText != null && !this.hasUpdated) {
            this.statusLabel.setText(statusText);
            this.statusLabel.setColor(statusColor);
        }

        SimpleModePanel.this.repaint();
    }
}
