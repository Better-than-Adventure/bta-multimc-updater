package net.minecraft.client.version;

import net.minecraft.client.instance.Instance;
import org.kohsuke.github.GHAsset;
import org.kohsuke.github.GHRelease;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OldStyleVersion
        implements Version
{
    public final GHRelease release;

    private final long timestamp;
    private final boolean prerelease;
    private final String name;
    private List<GHAsset> assets = null;

    public OldStyleVersion(GHRelease release)
    {
        this.release = release;

        this.timestamp = Long.parseLong(release.getBody());
        this.prerelease = release.isPrerelease();
        this.name = release.getName();
    }

    @Override
    public long getTimestamp()
    {
        return this.timestamp;
    }

    @Override
    public boolean isPrerelease()
    {
        return this.prerelease;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public List<GHAsset> getAssets()
    {
        if (this.assets == null)
        {
            try
            {
                this.assets = this.release.listAssets().toList();
            }
            catch (IOException e)
            {
                this.assets = new ArrayList<>();
            }
        }

        return this.assets;
    }

    @Override
    public boolean applyToInstance(Instance instance)
            throws IOException
    {
        System.out.println("Old-style versions are not supported.");
        return false;
    }
}
