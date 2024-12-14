package net.betterthanadventure.updater.frontend;

import net.betterthanadventure.updater.LauncherType;
import net.betterthanadventure.updater.backend.BackendManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;

public final class MainWindow {
    public static @Nullable MainWindow createMainWindow(final @NotNull ResourceBundle resources, final @Nullable BackendManager backendManager) {
        if (backendManager == null) {
            JOptionPane.showMessageDialog(null, "Could not connect to download repository!", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        return new MainWindow(resources, backendManager);
    }

    public enum DisplayMode {
        SIMPLE,
        ADVANCED
    }

    private final @NotNull ResourceBundle resources;
    private final @NotNull BackendManager backendManager;

    private final @NotNull JFrame frame;
    private final @NotNull JPanel mainPanel;

    private final @NotNull JPanel displayModeOuterPanel;
    private final @NotNull Map<@NotNull DisplayMode, @NotNull JPanel> displayModePanels = new EnumMap<>(DisplayMode.class);

    private @NotNull DisplayMode displayMode = DisplayMode.SIMPLE;

    private MainWindow(final @NotNull ResourceBundle resources, final @NotNull BackendManager backendManager) {
        this.resources = resources;
        this.backendManager = backendManager;

        final @NotNull String title;
        {
            final @Nullable String name = backendManager.getLauncherType().getName();
            if (name == null) {
                title = resources.getString("frontend.main_window.title.default");
            } else {
                title = String.format(resources.getString("frontend.main_window.title"), name);
            }
        }

        this.frame = new JFrame(title);
        this.frame.setSize(300 * Constants.GUI_SCALE, 200 * Constants.GUI_SCALE);
        this.frame.setResizable(false);
        this.frame.setLocationRelativeTo(null);

        // Init main panel
        this.mainPanel = new JPanel();
        this.mainPanel.setLayout(new BorderLayout());
        this.frame.add(this.mainPanel);

        // Init mode panels
        this.displayModeOuterPanel = new JPanel();
        this.displayModeOuterPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        this.displayModeOuterPanel.setLayout(new GridLayout(1, 1));
        this.mainPanel.add(this.displayModeOuterPanel, BorderLayout.CENTER);

        final @NotNull JPanel simpleModePanel = new SimpleModePanel(resources, backendManager);
        this.displayModePanels.put(DisplayMode.SIMPLE, simpleModePanel);
        final @NotNull JPanel advancedModePanel = new AdvancedModePanel(resources, backendManager);
        this.displayModePanels.put(DisplayMode.ADVANCED, advancedModePanel);

        this.setDisplayMode(DisplayMode.SIMPLE);

        // Init simple/advanced checkbox
        final @NotNull JCheckBox simpleAdvancedCheckBox = new JCheckBox(resources.getString("frontend.main_window.checkbox.label"), true);
        simpleAdvancedCheckBox.addActionListener(e -> {
            setDisplayMode(simpleAdvancedCheckBox.isSelected() ? DisplayMode.SIMPLE : DisplayMode.ADVANCED);
        });
        this.mainPanel.add(simpleAdvancedCheckBox, BorderLayout.SOUTH);

        this.frame.setVisible(true);
    }

    public void setDisplayMode(final @NotNull DisplayMode displayMode) {
        this.displayModeOuterPanel.remove(this.displayModePanels.get(this.displayMode));
        this.displayMode = displayMode;
        this.displayModeOuterPanel.add(this.displayModePanels.get(this.displayMode));
    }
}
