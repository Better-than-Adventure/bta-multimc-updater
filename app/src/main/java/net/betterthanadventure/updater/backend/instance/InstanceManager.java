package net.betterthanadventure.updater.backend.instance;

import net.betterthanadventure.updater.LauncherType;
import net.betterthanadventure.updater.backend.IProgressReporter;
import net.betterthanadventure.updater.backend.downloads.Channel;
import net.betterthanadventure.updater.backend.downloads.Downloadable;
import net.betterthanadventure.updater.backend.downloads.Version;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.*;
import java.util.Comparator;
import java.util.Properties;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

public class InstanceManager {
    private static final @NotNull String WORK_DIR_NAME = "WORK_DIR";

    private final @NotNull String instanceDir;
    private final @NotNull LauncherType launcherType;
    private final @NotNull File instanceRoot;
    private final @NotNull InstanceProperties properties;

    public InstanceManager(final @NotNull LauncherType launcherType, final @NotNull String instanceDir, final @Nullable String defaultChannel) {
        this.instanceDir = instanceDir;
        this.launcherType = launcherType;
        if (launcherType.isMultiMcLike()) {
            this.instanceRoot = new File("../../" + instanceDir);
        } else {
            this.instanceRoot = new File(instanceDir);
        }

        this.properties = new InstanceProperties(this);
        this.properties.read(defaultChannel);
    }

    public @NotNull String getInstanceDir() {
        return instanceDir;
    }

    public @NotNull File getInstanceRoot() {
        return this.instanceRoot;
    }

    public boolean exists() {
        return this.instanceRoot.exists();
    }

    public final @Nullable String getVersion() {
        return this.properties.getVersion();
    }

    public final @Nullable String getChannel() {
        return this.properties.getChannel();
    }

    public final @NotNull File getWorkDir() {
        return new File(this.instanceRoot, WORK_DIR_NAME);
    }

    public final void synchronize(final @NotNull Channel channel, final @NotNull Version version, final @Nullable IProgressReporter progressReporter) throws IOException {
        if (progressReporter != null) {
            progressReporter.initialize();
        }

        try {
            // Download files
            int i = 0;
            for (final @NotNull Downloadable downloadable : version.getDownloadables()) {
                final @NotNull DownloadableInstaller installer = new DownloadableInstaller(this, version.getUrl(), downloadable);
                installer.synchronize();
                i++;
                if (progressReporter != null) {
                    progressReporter.setProgress((float) i / (float) version.getDownloadables().size());
                }
            }

            // Create properties file
            this.properties.setChannel(channel.getId());
            this.properties.setVersion(version.getId());
            this.properties.write();

            // Refresh instance list
            triggerLauncherRefresh();
        } finally {
            Files.walk(getWorkDir().toPath())
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);

            if (progressReporter != null) {
                progressReporter.setDone(true);
            }
        }
    }

    private void triggerLauncherRefresh() {
        if (this.launcherType.isMultiMcLike()) {
            final @NotNull File tmpDir = new File("../../temp");
            tmpDir.mkdir();
            tmpDir.delete();
        }
    }
}
