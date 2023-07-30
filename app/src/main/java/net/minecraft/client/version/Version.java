package net.minecraft.client.version;

import net.minecraft.client.instance.Instance;
import org.kohsuke.github.GHAsset;

import java.io.IOException;
import java.util.List;

public interface Version
{
    long getTimestamp();
    boolean isPrerelease();
    String getName();
    List<GHAsset> getAssets();
    boolean applyToInstance(Instance instance) throws IOException;
}
