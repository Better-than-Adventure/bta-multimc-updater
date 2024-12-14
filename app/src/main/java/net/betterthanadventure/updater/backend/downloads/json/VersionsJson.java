package net.betterthanadventure.updater.backend.downloads.json;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VersionsJson {
    @SerializedName("versions")
    private @Nullable List<@NotNull String> versions = null;
    @SerializedName("default")
    private @Nullable String defaultVersion = null;

    private VersionsJson() { };

    public @Nullable List<@NotNull String> getVersions() {
        return this.versions;
    }

    public @Nullable String getDefaultVersion() {
        return this.defaultVersion;
    }
}
