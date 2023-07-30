package net.minecraft.client;

import net.minecraft.client.instance.Instance;
import net.minecraft.client.version.JsonVersion;
import net.minecraft.client.version.Version;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Minecraft
{
    public static void main(String[] args)
            throws IOException
    {
        Updater up = new Updater();
        up.update(new File("../prerelease").exists());
    }
}
