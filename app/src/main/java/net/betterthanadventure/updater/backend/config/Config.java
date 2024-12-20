package net.betterthanadventure.updater.backend.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.betterthanadventure.updater.backend.config.json.ChannelConfigJson;
import net.betterthanadventure.updater.backend.config.json.ConfigJson;
import net.betterthanadventure.updater.backend.downloads.Channel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Config {
    public static final class ChannelConfig {
        private @NotNull String directory;
        private @NotNull String name;

        public ChannelConfig(final @NotNull String directory, final @NotNull String name) {
            this.directory = directory;
            this.name = name;
        }

        public @NotNull String getDirectory() {
            return this.directory;
        }

        public void setDirectory() {
            this.directory = directory;
        }

        public @NotNull String getName() {
            return this.name;
        }

        public void setName(final @NotNull String name) {
            this.name = name;
        }
    }

    private static final @NotNull String CONFIG_FILE_NAME = "config.json";
    private static final @NotNull String DEFAULT_INSTANCE_DIR = "BTA_MANAGED_INSTANCE";

    private final @NotNull File configDir;

    private final @NotNull List<@NotNull ChannelConfig> channelConfigs = new ArrayList<>();
    private boolean isAdvanced = false;

    public Config(final @NotNull File configDir) {
        this.configDir = configDir;
    }

    public @NotNull @UnmodifiableView List<@NotNull ChannelConfig> getChannelConfigs() {
        return Collections.unmodifiableList(this.channelConfigs);
    }

    public boolean isAdvanced() {
        return this.isAdvanced;
    }

    public void setAdvanced(final boolean isAdvanced) {
        this.isAdvanced = isAdvanced;
    }

    public boolean read() {
        if (!this.configDir.exists()) {
            return false;
        }

        final @NotNull File configFile = new File(this.configDir, CONFIG_FILE_NAME);
        if (!configFile.exists()) {
            return false;
        }

        final @NotNull Gson gson = new Gson();

        // Parse config file
        final @Nullable ConfigJson configJson;
        try (final @NotNull InputStream in = Files.newInputStream(configFile.toPath(), StandardOpenOption.READ); final @NotNull Reader reader = new InputStreamReader(in)) {
            configJson = gson.fromJson(reader, ConfigJson.class);
        } catch (final @NotNull Exception ignored) {
            return false;
        }
        if (configJson == null) {
            return false;
        }
        if (configJson.getChannelConfigs() == null) {
            return false;
        }

        // Validate config JSON
        if (configJson.isAdvanced() == null) {
            return false;
        }
        for (final @NotNull ChannelConfigJson channelConfigJson : configJson.getChannelConfigs()) {
            if (channelConfigJson.getDirectory() == null || channelConfigJson.getName() == null) {
                return false;
            }
        }

        // Clear current configs
        this.channelConfigs.clear();

        // Read JSON into configs
        this.setAdvanced(Objects.requireNonNull(configJson.isAdvanced()));
        for (final @NotNull ChannelConfigJson channelConfigJson : configJson.getChannelConfigs()) {
            final @NotNull String directory = Objects.requireNonNull(channelConfigJson.getDirectory());
            final @NotNull String name = Objects.requireNonNull(channelConfigJson.getName());

            final @NotNull ChannelConfig channelConfig = new ChannelConfig(directory, name);
            this.channelConfigs.add(channelConfig);
        }

        return true;
    }

    public boolean write() {
        if (!this.configDir.exists()) {
            return false;
        }

        final @NotNull File configFile = new File(this.configDir, CONFIG_FILE_NAME);

        final @NotNull ConfigJson configJson = new ConfigJson();
        configJson.setAdvanced(isAdvanced());

        final @NotNull List<@NotNull ChannelConfigJson> channelConfigsJson = new ArrayList<>();
        for (final @NotNull ChannelConfig channelConfig : this.channelConfigs) {
            final @NotNull ChannelConfigJson channelConfigJson = new ChannelConfigJson();
            channelConfigJson.setDirectory(channelConfig.getDirectory());
            channelConfigJson.setName(channelConfig.getName());

            channelConfigsJson.add(channelConfigJson);
        }

        configJson.setChannelConfigs(channelConfigsJson);

        final @NotNull Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (final @NotNull OutputStream out = Files.newOutputStream(configFile.toPath(), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING); final @NotNull Writer writer = new OutputStreamWriter(out)) {
            gson.toJson(configJson, writer);
        } catch (final @NotNull Exception e) {
            return false;
        }

        return true;
    }

    public void initFromChannels(final @NotNull List<@NotNull Channel> channels, final @NotNull Channel defaultChannel) {
        final @NotNull List<@NotNull ChannelConfig> channelConfigs = new ArrayList<>();
        for (final @NotNull Channel channel : channels) {
            final @NotNull String instanceDir, instanceName;
            if (channel == defaultChannel) {
                instanceDir = DEFAULT_INSTANCE_DIR;
                instanceName = "Better than Adventure! (Managed)";
            } else {
                instanceDir = DEFAULT_INSTANCE_DIR + "_" + channel.getId().toUpperCase();
                instanceName = "Better than Adventure! " + channel.getId().substring(0, 1).toUpperCase() + channel.getId().substring(1) + " (Managed)";
            }

            final @NotNull ChannelConfig channelConfig = new ChannelConfig(instanceDir, instanceName);
            channelConfigs.add(channelConfig);
        }

        this.channelConfigs.addAll(channelConfigs);
    }
}
