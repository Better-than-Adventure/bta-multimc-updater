package net.betterthanadventure.updater.backend;

import net.betterthanadventure.updater.LauncherType;
import net.betterthanadventure.updater.backend.config.Config;
import net.betterthanadventure.updater.backend.downloads.Project;
import net.betterthanadventure.updater.backend.instance.InstanceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public final class BackendManager {
    private static final @NotNull String ROOT_URL = "https://downloads.betterthanadventure.net/";
    private static final @NotNull String PROJECT_NAME = "bta-client";

    public static @Nullable BackendManager createBackendManager() {
        try {
            return new BackendManager();
        } catch (final @NotNull Exception e) {
            return null;
        }
    }

    private final @NotNull LauncherType launcherType;
    private final @NotNull Project project;
    private final @NotNull InstanceManager instanceManager;
    private final @NotNull Config config;

    private BackendManager() throws IllegalStateException {
        this.launcherType = LauncherType.getLauncherType();
        try {
            final @Nullable Project project = Project.readProjectFromUrl(PROJECT_NAME, new URL(ROOT_URL + PROJECT_NAME + "/"));
            if (project == null) {
                throw new IllegalStateException();
            }
            this.project = project;
        } catch (final @NotNull Exception e) {
            throw new IllegalStateException(e);
        }
        this.instanceManager = new InstanceManager(this.launcherType, "BTA_MANAGED_INSTANCE", this.project.getDefaultChannel().getId());
        this.config = new Config(new File("."));
        if (!this.config.read()) {
            this.config.initFromChannels(this.project.getChannels(), this.project.getDefaultChannel());
            this.config.write();
        }
    }

    public @NotNull LauncherType getLauncherType() {
        return this.launcherType;
    }

    public @NotNull InstanceManager getInstanceManager() {
        return this.instanceManager;
    }

    public @NotNull Project getDownloadManager() {
        return this.project;
    }

    public @NotNull Config getConfig() {
        return this.config;
    }
}
