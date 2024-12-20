package net.betterthanadventure.updater.backend.config.json;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ConfigJson {
    @SerializedName("channelConfigs")
    private @Nullable List<@NotNull ChannelConfigJson> channelConfigs = null;
    @SerializedName("isAdvanced")
    private @Nullable Boolean isAdvanced = null;

    public ConfigJson() { }

    public @Nullable List<@NotNull ChannelConfigJson> getChannelConfigs() {
        return this.channelConfigs;
    }

    public void setChannelConfigs(final @Nullable List<@NotNull ChannelConfigJson> channelConfigs) {
        this.channelConfigs = channelConfigs;
    }

    public @Nullable Boolean isAdvanced() {
        return this.isAdvanced;
    }

    public void setAdvanced(final boolean advanced) {
        this.isAdvanced = advanced;
    }
}
