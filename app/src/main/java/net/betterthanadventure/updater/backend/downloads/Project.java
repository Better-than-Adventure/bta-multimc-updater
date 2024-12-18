package net.betterthanadventure.updater.backend.downloads;

import com.google.gson.Gson;
import net.betterthanadventure.updater.backend.downloads.json.ChannelsJson;
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

public class Project {
    public static final @NotNull Gson GSON = new Gson();

    public static @Nullable Project readProjectFromUrl(final @NotNull String id, final @NotNull URL url) throws Exception {
        final @NotNull URL channelsJsonUrl = new URL(url, "channels.json");
        final @NotNull ChannelsJson channelsJson;
        try (final @NotNull InputStream stream = channelsJsonUrl.openStream(); final @NotNull Reader reader = new InputStreamReader(stream)) {
            channelsJson = GSON.fromJson(reader, ChannelsJson.class);
        }

        if (channelsJson.getChannels() == null || channelsJson.getDefaultChannel() == null) {
            return null;
        }

        final @NotNull List<@NotNull Channel> channels = new ArrayList<>();
        @Nullable Channel defaultChannel = null;
        for (final @NotNull String channelName : channelsJson.getChannels()) {
            System.out.println("Fetching channel " + channelName);
            final @Nullable Channel channel = Channel.readChannelFromUrl(channelName, new URL(url, channelName + "/"));
            if (channel == null) {
                return null;
            }
            channels.add(channel);
            if (channelName.equals(channelsJson.getDefaultChannel())) {
                defaultChannel = channel;
            }
        }
        if (defaultChannel == null) {
            return null;
        }

        return new Project(url, id, channels, defaultChannel);
    }

    private final @NotNull URL url;
    private final @NotNull String id;
    private final @NotNull List<@NotNull Channel> channels;
    private final @NotNull Channel defaultChannel;

    public Project(final @NotNull URL url,
                   final @NotNull String id,
                   final @NotNull List<@NotNull Channel> channels,
                   final @NotNull Channel defaultChannel
    ) {
        this.url = url;
        this.id = id;
        this.channels = channels;
        this.defaultChannel = defaultChannel;
    }

    public @NotNull URL getUrl() {
        return this.url;
    }

    public @NotNull String getId() {
        return this.id;
    }

    public final @NotNull Channel getDefaultChannel() {
        return this.defaultChannel;
    }

    public final @NotNull @UnmodifiableView List<@NotNull Channel> getChannels() {
        return Collections.unmodifiableList(this.channels);
    }
}
