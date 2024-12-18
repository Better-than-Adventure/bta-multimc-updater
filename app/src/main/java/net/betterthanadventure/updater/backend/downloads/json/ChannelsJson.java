package net.betterthanadventure.updater.backend.downloads.json;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ChannelsJson {
    @SerializedName("channels")
    private @Nullable List<@NotNull String> channels = null;
    @SerializedName("default")
    private @Nullable String defaultChannel = null;

    private ChannelsJson() { }

    public final @Nullable List<@NotNull String> getChannels() {
        return this.channels;
    }

    public @Nullable String getDefaultChannel() {
        return this.defaultChannel;
    }
}
