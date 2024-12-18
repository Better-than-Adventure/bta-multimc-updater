package net.betterthanadventure.updater.backend.instance;

import net.betterthanadventure.updater.backend.downloads.Downloadable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.math.BigInteger;
import java.net.URL;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DownloadableInstaller {
    private static final int BUFFER_SIZE = 1024;

    private final @NotNull InstanceManager instance;
    private final @NotNull URL rootUrl;
    private final @NotNull Downloadable downloadable;

    private final @NotNull File workingPath;
    private final @NotNull File installPath;

    private float progress = 0.0f;
    private final @NotNull Lock progressLock = new ReentrantLock();

    public DownloadableInstaller(final @NotNull InstanceManager instance, final @NotNull URL rootUrl, final @NotNull Downloadable downloadable) {
        this.instance = instance;
        this.rootUrl = rootUrl;
        this.downloadable = downloadable;

        this.workingPath = new File(instance.getWorkDir(), downloadable.getAssetPath());
        this.installPath = new File(instance.getInstanceRoot(), downloadable.getInstallPath());
    }

    public void synchronize() throws IOException {
        // Create directory
        final @Nullable File workDir = this.workingPath.getParentFile();
        if (workDir != null && !workDir.exists()) {
            if (!workDir.mkdirs()) {
                throw new IOException("Could not create working directory!");
            }
        }

        // Create download URL
        final @NotNull URL downloadableUrl = new URL(this.rootUrl, "auto/" + this.downloadable.getAssetPath());

        // Get download size
        final long downloadSize = this.downloadable.getSize();

        // Download file
        try (final @NotNull InputStream in = downloadableUrl.openStream(); final @NotNull FileOutputStream out = new FileOutputStream(this.workingPath)) {
            long bytesRead = 0;
            final byte[] buffer = new byte[BUFFER_SIZE];
            int count;
            while ((count = in.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, count);
                bytesRead += count;
                setProgress((float) bytesRead / (float) downloadSize);
            }
        }

        // Check downloaded MD5
        try {
            final byte @NotNull[] fileData = Files.readAllBytes(this.workingPath.toPath());
            final byte @NotNull[] digest = MessageDigest.getInstance("MD5").digest(fileData);
            final @NotNull String checksum = new BigInteger(1, digest).toString(16);
            if (!(checksum.equalsIgnoreCase(this.downloadable.getMd5()))) {
                throw new IOException("MD5 signature for " + this.downloadable.getAssetPath() + " was invalid!");
            }
        } catch (final @NotNull NoSuchAlgorithmException e) {
            System.out.println("ERROR: could not check MD5 signature. Skipping check.");
        }

        // Copy to real directory
        Files.move(this.workingPath.toPath(), this.installPath.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    public float getProgress() {
        this.progressLock.lock();
        final float progress = this.progress;
        this.progressLock.unlock();

        return progress;
    }

    private void setProgress(final float progress) {
        this.progressLock.lock();
        this.progress = progress;
        this.progressLock.unlock();
    }
}
