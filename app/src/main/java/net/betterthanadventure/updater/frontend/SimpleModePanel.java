package net.betterthanadventure.updater.frontend;

import net.betterthanadventure.updater.backend.BackendManager;
import net.betterthanadventure.updater.backend.IProgressReporter;
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
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ResourceBundle;

public class SimpleModePanel extends MinecraftJPanel {
    private final @NotNull ResourceBundle resources;
    private final @NotNull BackendManager backendManager;

    private final @NotNull JLabel installedLabel;
    private final @NotNull JLabel updateAvailableLabel;
    private final @NotNull JButton installUpdateButton;
    private final @NotNull JButton helpButton;
    private final @NotNull MinecraftJProgressBar progressBar;
    private final @NotNull Timer timer;

    private @NotNull String latestVersion = "";
    private @NotNull String installedVersion = "";
    private boolean forcedUpdate = false;

    public SimpleModePanel(final @NotNull ResourceBundle resources, final @NotNull BackendManager backendManager) {
        super(0.25f);

        this.resources = resources;
        this.backendManager = backendManager;

        this.setLayout(new BorderLayout());

        final @NotNull JPanel upperPanel = new JPanel();
        upperPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        upperPanel.setBackground(new Color(0x00000000, true));
        final @NotNull JPanel lowerPanel = new JPanel();
        lowerPanel.setBackground(new Color(0x00000000, true));
        lowerPanel.setLayout(new BoxLayout(lowerPanel, BoxLayout.Y_AXIS));

        final @NotNull JLabel versionLabel = new MinecraftJLabel(resources.getString("frontend.main_window.simple.version_label.label"), 0x505050, true);
        versionLabel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        upperPanel.add(versionLabel);

        try {
            final @NotNull BufferedImage image = ImageIO.read(getClass().getResource("/image/logo-header.png"));
            final @NotNull Image scaledImage = image.getScaledInstance((int) (image.getWidth() / (4.0 / Constants.GUI_SCALE)), (int) (image.getHeight() / (4.0 / Constants.GUI_SCALE)), Image.SCALE_FAST);
            final @NotNull ImageIcon icon = new ImageIcon(scaledImage);
            final @NotNull JLabel imageLabel = new JLabel(icon);
            imageLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
            lowerPanel.add(imageLabel);
        } catch (final @NotNull IOException ignored) { }

        this.installedLabel = new MinecraftJLabel("", 0xFFFFFF, true);
        this.installedLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        lowerPanel.add(this.installedLabel);

        this.updateAvailableLabel = new MinecraftJLabel("", 0xFFFF00, true);
        this.updateAvailableLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        lowerPanel.add(this.updateAvailableLabel);
        lowerPanel.add(Box.createVerticalGlue());

        this.installUpdateButton = new MinecraftJButton("");
        this.installUpdateButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        lowerPanel.add(this.installUpdateButton);

        this.helpButton = new MinecraftJButton("?");
        this.installUpdateButton.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
        upperPanel.add(this.helpButton);

        this.progressBar = new MinecraftJProgressBar();
        this.progressBar.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        this.progressBar.setVisible(false);
        lowerPanel.add(this.progressBar);
        lowerPanel.add(Box.createVerticalGlue());

        add(upperPanel, BorderLayout.NORTH);
        add(lowerPanel, BorderLayout.CENTER);

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

        this.installUpdateButton.addActionListener(e -> {
            final @NotNull Channel channel = this.backendManager.getDownloadManager().getChannels().get(1);
            final @NotNull Version version = channel.getDefaultVersion();

            this.installUpdateButton.setVisible(false);
            this.progressBar.setVisible(true);
            this.repaint();

            final @NotNull Thread t = new Thread(() -> {
                try {
                    this.timer.setRepeats(true);
                    this.timer.start();
                    this.backendManager.getInstanceManager().synchronize(channel, version, this.progressBar);
                } catch (final @NotNull Exception ex) {
                    JOptionPane.showMessageDialog(this, "Could not download: " + ex, "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            t.setDaemon(true);
            t.start();
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

        if (this.installedVersion.equals(this.latestVersion) && !this.forcedUpdate) {
            this.updateAvailableLabel.setText("");
            this.installUpdateButton.setText(this.resources.getString("frontend.main_window.simple.install_update_button.label.none_available"));
            this.installUpdateButton.setEnabled(false);
        } else {
            if (this.backendManager.getInstanceManager().exists()) {
                this.updateAvailableLabel.setText(this.resources.getString("frontend.main_window.simple.update_available_label.label"));
            } else {
                this.updateAvailableLabel.setText("");
            }
            if (this.backendManager.getInstanceManager().exists()) {
                this.installUpdateButton.setText(String.format(this.resources.getString("frontend.main_window.simple.install_update_button.label.update"), this.latestVersion));
            } else {
                this.installUpdateButton.setText(String.format(this.resources.getString("frontend.main_window.simple.install_update_button.label.install"), this.latestVersion));
            }
            this.installUpdateButton.setEnabled(true);
        }

        SimpleModePanel.this.repaint();
    }
}
