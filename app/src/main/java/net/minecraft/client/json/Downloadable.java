package net.minecraft.client.json;

import net.minecraft.client.Util;
import net.minecraft.client.ops.Merge;
import net.minecraft.client.version.Version;
import org.kohsuke.github.GHAsset;
import org.kohsuke.github.GHRelease;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class Downloadable
{
    public String asset;
    public String path;
    public String md5;
    public String updateMode;

    public Downloadable() { }

    public boolean download(Version version, File rootDir)
            throws IOException
    {
        File file = new File(rootDir, this.path);
        if (file.isDirectory())
        {
            file.mkdirs();
            file = new File(file, asset);
        }
        else
        {
            file.getParentFile().mkdirs();
        }

        System.out.println("Validating asset " + this.path + "...");

        GHAsset asset = null;
        if (this.asset != null)
        {
            List<GHAsset> assets = version.getAssets();
            for (GHAsset a : assets)
            {
                if (a.getName().equals(this.asset))
                {
                    asset = a;
                    break;
                }
            }
        }

        if (asset == null)
        {
            System.out.println("Asset " + this.asset + " does not exist on GitHub repo. Please report this!");
            return false;
        }

        if (file.exists())
        {
            if (Util.matchesChecksum(file, this.md5))
            {
                System.out.println("Asset is up to date.");
                return false;
            }
            else
            {
                System.out.println("Asset is out of date. Downloading new asset...");
            }
        }
        else
        {
            System.out.println("Asset does not exist. Downloading new asset...");
        }

        if (this.updateMode.equals("merge"))
        {
            System.out.println("Merging new asset with existing file...");
            File newFile = new File(file.getParentFile(), file.getName() + "_tmp");
            Util.downloadFile(asset.getBrowserDownloadUrl(), newFile);
            Merge.mergeFiles(file, newFile);
            newFile.delete();
        }
        else if (this.updateMode.equals("overwrite"))
        {
            if (file.delete())
            {
                System.out.println("Overwriting existing asset...");
            }
            Util.downloadFile(asset.getBrowserDownloadUrl(), file);

            System.out.println("Validating new asset...");
            if (!Util.matchesChecksum(file, this.md5))
            {
                System.out.println("New asset is invalid! Please report this!");
                return false;
            }
        }
        else
        {
            System.out.println("Invalid update mode! Please report this!");
            return false;
        }

        System.out.println("Asset is up to date.");
        return true;
    }
}
