package net.minecraft.client;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class Minecraft
{
    InstanceManager im;

    public Minecraft()
    {
        System.out.println("************************************************");
        System.out.println("* Better than Adventure! MultiMC Update Utility*");
        System.out.println("************************************************");

        im = new InstanceManager();
        ManagedInstance instance;
        try
        {
            instance = im.findOrCreateManagedInstance();
            Files.copy(Path.of("C:/Users/josep/Downloads/bta.jar"), instance.fJarmodsBtaJar.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args)
    {
        Minecraft mc = new Minecraft();
    }
}
