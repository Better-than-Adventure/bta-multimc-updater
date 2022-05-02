package net.minecraft.client;

import org.kohsuke.github.GHAsset;
import org.kohsuke.github.GHRelease;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class DownloadManager
{
    public void downloadRelease(ManagedInstance instance, GHRelease release)
            throws IOException
    {
        GHAsset btaJar = null;

        System.out.println("Starting download...");

        // Find bta.jar
        for (GHAsset asset : release.listAssets())
        {
            if (asset.getName().equals("bta.jar"))
            {
                btaJar = asset;
                break;
            }
        }

        if (btaJar == null)
        {
            throw new FileNotFoundException("Could not find bta.jar!");
        }

        String downloadUrl = btaJar.getBrowserDownloadUrl();
        long downloadSize = btaJar.getSize();

        // Start download
        try (BufferedInputStream in = new BufferedInputStream(new URL(downloadUrl).openStream()))
        {
            FileOutputStream fileOutputStream = new FileOutputStream(instance.fJarmodsBtaJar);
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            int totalBytesRead = 0;
            int lastPrinted = -10;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1)
            {
                totalBytesRead += bytesRead;
                float downloadPercentage = (float)totalBytesRead / downloadSize;
                if ((downloadPercentage * 100) > lastPrinted + 10)
                {
                    lastPrinted += 10;
                    System.out.println(lastPrinted + "%");
                }
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        }

        System.out.println("\nDownload complete.");
    }
}
