package net.minecraft.client.instance;

import net.minecraft.client.version.Version;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Instance
{
    public final String name;

    public final File rootDir;

    private final String pathPrefix = "../../";

    public Instance(String name)
        throws IOException
    {

        this.name = name;

        System.out.println("Locating managed instance directory " + name + "...");

        if (!managedInstanceExists())
        {
            System.out.println("Could not locate managed instance directory " + name + ". Creating...");

            Files.createDirectory(Paths.get(pathPrefix + name));

            System.out.println("Managed instance directory " + name + " created.");
        }
        else
        {
            System.out.println("Managed instance directory " + name + " located.");
        }

        this.rootDir = new File(pathPrefix + name);
    }

    public boolean managedInstanceExists()
            throws IOException
    {
        try (Stream<Path> paths = Files.list(Paths.get(pathPrefix)))
        {
            return paths.anyMatch(x -> x.endsWith(name));
        }
    }
}
