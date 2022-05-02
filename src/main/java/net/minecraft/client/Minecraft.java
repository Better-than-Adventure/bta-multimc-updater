package net.minecraft.client;

import org.kohsuke.github.GHRelease;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class Minecraft
{
    InstanceManager im;
    VersionManager vm;
    DownloadManager dm;

    public Minecraft()
    {
        im = new InstanceManager();
        vm = new VersionManager();
        dm = new DownloadManager();
    }

    public int doUpdate(boolean includePrereleases)
    {
        System.out.println("************************************************");
        System.out.println("* Better than Adventure! MultiMC Update Utility*");
        System.out.println("************************************************");

        // Set up instance manager and version manager
        ManagedInstance instance;
        try
        {
            instance = im.findOrCreateManagedInstance();
            vm.connect(false);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return -1;
        }

        // Get current version
        GHRelease currentRelease = vm.getReleaseFromVersionId(instance.currentVersionId);
        String currentReleaseName = "UNKNOWN";
        if (currentRelease != null)
        {
            currentReleaseName = currentRelease.getName();
        }
        System.out.println("Current version of \"" + instance.instanceName + "\" is " + currentReleaseName + ".");

        // Check if current version is less than latest version
        GHRelease latestRelease = vm.getReleaseFromVersionId(vm.getMostRecentVersionId());
        if (instance.currentVersionId < vm.getMostRecentVersionId())
        {
            System.out.println("Instance can be updated to " + latestRelease.getName() + "!");

            // Start download
            try
            {
                dm.downloadRelease(instance, latestRelease);
                FileWriter fw = new FileWriter(instance.fVersion);
                fw.write(Integer.toString(vm.getMostRecentVersionId()));
                fw.close();
                FileWriter fw2 = new FileWriter(instance.fInstanceCfg);
                fw2.write("JvmArgs=-XX:+UseG1GC -Dsun.rmi.dgc.server.gcInterval=2147483646 -XX:+UnlockExperimentalVMOptions -XX:G1NewSizePercent=20 -XX:G1ReservePercent=20 -XX:MaxGCPauseMillis=50 -XX:G1HeapRegionSize=32M\n");
                fw2.close();

            }
            catch (IOException e)
            {
                e.printStackTrace();
                return -1;
            }
        }
        else
        {
            System.out.println("Instance is already up to date.");
        }

        System.out.println("Have a nice day :)");

        return 0;
    }


    public static void main(String[] args)
    {
        Minecraft mc = new Minecraft();
        System.exit(mc.doUpdate(false));
    }
}
