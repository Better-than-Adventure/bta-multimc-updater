package net.betterthanadventure.updater.backend.config.json;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ConfigJson {
    @SerializedName("channelConfigs")
    private @Nullable List<@NotNull ChannelConfigJson> channelConfigs = null;

    public ConfigJson() { }

    public @Nullable List<@NotNull ChannelConfigJson> getChannelConfigs() {
        return this.channelConfigs;
    }

    public void setChannelConfigs(final @Nullable List<@NotNull ChannelConfigJson> channelConfigs) {
        this.channelConfigs = channelConfigs;
    }
}
