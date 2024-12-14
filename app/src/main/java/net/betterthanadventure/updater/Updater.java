package net.betterthanadventure.updater;

import net.betterthanadventure.updater.backend.BackendManager;
import net.betterthanadventure.updater.frontend.MainWindow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.ResourceBundle;

public class Updater {
    private final @NotNull ResourceBundle resourceBundle;

    public Updater() {
        this.resourceBundle = ResourceBundle.getBundle("Language", Locale.getDefault());

        final @Nullable BackendManager backendManager = BackendManager.createBackendManager();
        final @Nullable MainWindow mainWindow = MainWindow.createMainWindow(this.resourceBundle, backendManager);
    }
}
