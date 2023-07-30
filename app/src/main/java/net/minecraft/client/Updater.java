package net.minecraft.client;

import net.minecraft.client.instance.Instance;
import net.minecraft.client.version.Version;

import java.io.File;
import java.io.IOException;

public class Updater
{
    public Updater() { }

    public void update(boolean prereleaseInstance)
            throws IOException
    {
        System.out.println("*************************************************");
        System.out.println("* Better than Adventure! MultiMC Update Utility *");
        System.out.println("*************************************************");

        Instance instance;
        if (prereleaseInstance)
        {
            instance = new Instance("BTA_PRERELEASE_INSTANCE");
        }
        else
        {
            instance = new Instance("BTA_MANAGED_INSTANCE");
        }

        VersionManager vm = new VersionManager();
        vm.connect();

        Version latest = vm.getLatestVersion(prereleaseInstance);
        if (latest == null)
        {
            System.out.println("No releases found.");
            return;
        }

        System.out.println("Validating downloaded assets against version " + latest.getName() + "...");
        boolean wasUpdated = latest.applyToInstance(instance);

        if (wasUpdated)
        {
            System.out.println("Assets updated successfully.");

            File tempDir = new File("../../temp");
            tempDir.mkdir();
            tempDir.delete();

        }
        else
        {
            System.out.println("Assets already up to date.");
        }

        System.out.println("Have a nice day :)");
    }
}
