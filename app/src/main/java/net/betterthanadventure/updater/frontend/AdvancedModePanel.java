package net.betterthanadventure.updater.frontend;

import net.betterthanadventure.updater.backend.BackendManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ResourceBundle;

public class AdvancedModePanel extends JPanel {
    private final @NotNull ResourceBundle resources;
    private final @NotNull BackendManager backendManager;

    public AdvancedModePanel(final @NotNull ResourceBundle resources, final @NotNull BackendManager backendManager) {
        this.resources = resources;
        this.backendManager = backendManager;
    }
}
