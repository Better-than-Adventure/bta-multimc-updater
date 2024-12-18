package net.betterthanadventure.updater.backend.downloads;

import net.betterthanadventure.updater.backend.downloads.json.AutoManifestJson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Version {
    public static @Nullable Version readVersionFromUrl(final @NotNull String id, final @NotNull URL url) throws Exception {
        final @NotNull URL autoManifestJsonUrl = new URL(url, "auto-manifest.json");
        final @NotNull AutoManifestJson autoManifestJson;
        try (final @NotNull InputStream stream = autoManifestJsonUrl.openStream(); final @NotNull Reader reader = new InputStreamReader(stream)) {
            autoManifestJson = Project.GSON.fromJson(reader, AutoManifestJson.class);
        }

        if (autoManifestJson.getTimestamp() == null ||
            autoManifestJson.getChannel() == null ||
            autoManifestJson.getName() == null ||
            autoManifestJson.getDownloadables() == null
        ) {
            System.out.println("Version manifest was missing one or more fields!");
            return null;
        }

        final @NotNull List<@NotNull Downloadable> downloadables = new ArrayList<>();
        for (final @NotNull AutoManifestJson.DownloadableJson downloadableJson : autoManifestJson.getDownloadables()) {
            System.out.println("Fetching downloadable " + downloadableJson.getAssetPath());
            final @Nullable Downloadable downloadable = Downloadable.fromDownloadableJson(downloadableJson);
            if (downloadable == null) {
                return null;
            }
            downloadables.add(downloadable);
        }

        return new Version(url, id, downloadables, autoManifestJson.getTimestamp());
    }

    private final @NotNull URL url;
    private final @NotNull String id;
    private final @NotNull List<@NotNull Downloadable> downloadables;
    private final double timestamp;

    private Version(final @NotNull URL url,
                    final @NotNull String id,
                    final @NotNull List<@NotNull Downloadable> downloadables,
                    final double timestamp
    ) {
        this.url = url;
        this.id = id;
        this.downloadables = downloadables;
        this.timestamp = timestamp;
    }

    public @NotNull URL getUrl() {
        return this.url;
    }

    public @NotNull String getId() {
        return this.id;
    }

    public @NotNull @UnmodifiableView List<@NotNull Downloadable> getDownloadables() {
        return Collections.unmodifiableList(this.downloadables);
    }

    public double getTimestamp() {
        return timestamp;
    }
}
