package net.betterthanadventure.updater.backend.downloads;

import net.betterthanadventure.updater.backend.downloads.json.ChannelsJson;
import net.betterthanadventure.updater.backend.downloads.json.VersionsJson;
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

public class Channel {
    public static @Nullable Channel readChannelFromUrl(final @NotNull String id, final @NotNull URL url) throws Exception {
        final @NotNull URL versionsJsonUrl = new URL(url, "versions.json");
        final @NotNull VersionsJson versionsJson;
        try (final @NotNull InputStream stream = versionsJsonUrl.openStream(); final @NotNull Reader reader = new InputStreamReader(stream)) {
            versionsJson = Project.GSON.fromJson(reader, VersionsJson.class);
        }

        if (versionsJson.getVersions() == null || versionsJson.getDefaultVersion() == null) {
            return null;
        }

        final @NotNull List<@NotNull Version> versions = new ArrayList<>();
        @Nullable Version defaultVersion = null;
        for (final @NotNull String versionName : versionsJson.getVersions()) {
            final @Nullable Version version;
            System.out.println("Fetching version " + versionName);
            try {
                version = Version.readVersionFromUrl(versionName, new URL(url, versionName + "/"));
            } catch (final @NotNull Exception e) {
                continue;
            }
            if (version == null) {
                continue;
            }
            versions.add(version);
            if (versionName.equals(versionsJson.getDefaultVersion())) {
                defaultVersion = version;
            }
        }
        if (defaultVersion == null) {
            return null;
        }

        return new Channel(url, id, versions, defaultVersion);
    }

    private final @NotNull URL url;
    private final @NotNull String id;
    private final @NotNull List<@NotNull Version> versions;
    private final @NotNull Version defaultVersion;

    private Channel(final @NotNull URL url,
                    final @NotNull String id,
                    final @NotNull List<@NotNull Version> versions,
                    final @NotNull Version defaultVersion
    ) {
        this.url = url;
        this.id = id;
        this.versions = versions;
        this.defaultVersion = defaultVersion;
    }

    public URL getUrl() {
        return this.url;
    }

    public @NotNull String getId() {
        return this.id;
    }

    public @NotNull Version getDefaultVersion() {
        return this.defaultVersion;
    }

    public @NotNull @UnmodifiableView List<@NotNull Version> getVersions() {
        return Collections.unmodifiableList(this.versions);
    }
}
