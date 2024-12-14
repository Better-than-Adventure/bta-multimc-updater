package net.betterthanadventure.updater.backend.downloads.json;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AutoManifestJson {
    public static class DownloadableJson {
        @SerializedName("assetPath")
        private @Nullable String assetPath = null;
        @SerializedName("installPath")
        private @Nullable String installPath = null;
        @SerializedName("md5")
        private @Nullable String md5 = null;

        private DownloadableJson() { }

        public @Nullable String getAssetPath() {
            return this.assetPath;
        }

        public @Nullable String getInstallPath() {
            return this.installPath;
        }

        public @Nullable String getMd5() {
            return this.md5;
        }
    }

    @SerializedName("timestamp")
    private @Nullable Double timestamp = null;
    @SerializedName("channel")
    private @Nullable String channel = null;
    @SerializedName("name")
    private @Nullable String name = null;
    @SerializedName("downloadables")
    private @Nullable List<@NotNull DownloadableJson> downloadables = null;

    private AutoManifestJson() { }

    public @Nullable Double getTimestamp() {
        return this.timestamp;
    }

    public @Nullable String getChannel() {
        return this.channel;
    }

    public @Nullable String getName() {
        return this.name;
    }

    public @Nullable List<@NotNull DownloadableJson> getDownloadables() {
        return this.downloadables;
    }
}
