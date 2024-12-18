package net.betterthanadventure.updater.backend.config.json;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChannelConfigJson {
    @SerializedName("directory")
    private @Nullable String directory = null;
    @SerializedName("name")
    private @Nullable String name = null;

    public ChannelConfigJson() { }

    public @Nullable String getDirectory() {
        return this.directory;
    }

    public void setDirectory(final @NotNull String directory) {
        this.directory = directory;
    }

    public @Nullable String getName() {
        return this.name;
    }

    public void setName(final @NotNull String name) {
        this.name = name;
    }
}
