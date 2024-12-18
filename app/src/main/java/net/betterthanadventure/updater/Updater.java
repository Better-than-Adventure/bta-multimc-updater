package net.betterthanadventure.updater;

import net.betterthanadventure.updater.backend.BackendManager;
import net.betterthanadventure.updater.frontend.MainWindow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Locale;
import java.util.ResourceBundle;

public class Updater {
    private final @NotNull ResourceBundle resourceBundle;

    private final @NotNull BackendManager backendManager;
    private final @NotNull MainWindow mainWindow;

    public Updater() {
        this.resourceBundle = ResourceBundle.getBundle("Language", Locale.getDefault());

        final @Nullable BackendManager backendManager = BackendManager.createBackendManager();
        if (backendManager == null) {
            JOptionPane.showMessageDialog(null, "Could not connect to download repository!", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        this.backendManager = backendManager;
        this.mainWindow = new MainWindow(this.resourceBundle, this.backendManager);
    }

    public void run() {
        this.mainWindow.show();
    }
}
