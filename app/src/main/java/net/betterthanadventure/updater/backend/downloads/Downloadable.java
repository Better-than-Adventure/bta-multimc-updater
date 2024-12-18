package net.betterthanadventure.updater.backend.downloads;

import net.betterthanadventure.updater.backend.downloads.json.AutoManifestJson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Downloadable {
    public static @Nullable Downloadable fromDownloadableJson(final @NotNull AutoManifestJson.DownloadableJson downloadableJson) {
        if (downloadableJson.getAssetPath() == null || downloadableJson.getInstallPath() == null || downloadableJson.getSize() == null || downloadableJson.getMd5() == null) {
            System.out.println("Downloadable manifest was missing one or more fields!");
            return null;
        }

        return new Downloadable(
            downloadableJson.getAssetPath(),
            downloadableJson.getInstallPath(),
            downloadableJson.getSize(),
            downloadableJson.getMd5()
        );
    }

    private final @NotNull String assetPath;
    private final @NotNull String installPath;
    private final long size;
    private final @NotNull String md5;

    private Downloadable(final @NotNull String assetPath,
                         final @NotNull String installPath,
                         final long size,
                         final @NotNull String md5
    ) {
        this.assetPath = assetPath;
        this.installPath = installPath;
        this.size = size;
        this.md5 = md5;
    }

    public @NotNull String getAssetPath() {
        return this.assetPath;
    }

    public @NotNull String getInstallPath() {
        return this.installPath;
    }

    public long getSize() {
        return size;
    }

    public @NotNull String getMd5() {
        return this.md5;
    }
}
