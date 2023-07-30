package net.minecraft.client.version;

import com.google.gson.Gson;
import net.minecraft.client.Util;
import net.minecraft.client.instance.Instance;
import net.minecraft.client.json.Downloadable;
import net.minecraft.client.json.Manifest;
import org.kohsuke.github.GHAsset;
import org.kohsuke.github.GHRelease;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonVersion
        implements Version
{
    public final GHRelease release;
    public final Manifest manifest;

    private List<GHAsset> assets = null;

    public JsonVersion(GHRelease release)
            throws Exception
    {
        this.release = release;

        List<GHAsset> assets = getAssets();
        GHAsset manifestAsset = null;
        for (GHAsset asset : assets)
        {
            if (asset.getName().equals("manifest.json"))
            {
                manifestAsset = asset;
                break;
            }
        }

        if (manifestAsset == null)
        {
            throw new Exception("Release " + release.getName() + " does not contain a manifest.json file!");
        }

        String manifestJson = Util.readStringFromURL(manifestAsset.getBrowserDownloadUrl());

        Gson gson = new Gson();
        this.manifest = gson.fromJson(manifestJson, Manifest.class);
    }

    @Override
    public long getTimestamp()
    {
        return manifest.timestamp;
    }

    @Override
    public boolean isPrerelease()
    {
        return manifest.prerelease;
    }

    @Override
    public String getName()
    {
        return manifest.name;
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
        boolean wasUpdated = false;

        for (Downloadable downloadable : manifest.downloadables)
        {
            if (downloadable.download(this, instance.rootDir))
            {
                wasUpdated = true;
            }
        }

        return wasUpdated;
    }
}
