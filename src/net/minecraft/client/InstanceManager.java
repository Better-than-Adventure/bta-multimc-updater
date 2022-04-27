package net.minecraft.client;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Stream;

public class InstanceManager
{
    public final String instanceName = "Better than Adventure! (Managed)";
    public final String folderName = "BTA_MANAGED_INSTANCE";
    public boolean instanceExists;

    public ManagedInstance findOrCreateManagedInstance() throws IOException
    {
        ManagedInstance instance = new ManagedInstance();

        System.out.println("Locating managed instance...");

        if (!managedInstanceExists())
        {
            System.out.println("Could not locate managed instance. Creating...");

            instance.fRootDir = new File(System.getProperty("java.io.tmpdir") + "/" + folderName);
            instance.fMinecraft = new File(instance.fRootDir, ".minecraft");
            instance.fJarmods = new File(instance.fRootDir, "jarmods");
            instance.fPatches = new File(instance.fRootDir, "patches");
            instance.fInstanceCfg = new File(instance.fRootDir, "instance.cfg");
            instance.fMmcPackJson = new File(instance.fRootDir, "mmc-pack.json");
            instance.fVersion = new File(instance.fRootDir, "version");
            instance.fPatchesJson = new File(instance.fPatches, "org.multimc.jarmod.bta.json");
            instance.fJarmodsBtaJar = new File(instance.fJarmods, "bta.jar");

            deleteFolder(instance.fRootDir.toPath());

            Files.createDirectory(instance.fRootDir.toPath());
            Files.createDirectory(instance.fMinecraft.toPath());
            Files.createDirectory(instance.fPatches.toPath());
            Files.createDirectory(instance.fJarmods.toPath());
            Files.createFile(instance.fInstanceCfg.toPath());
            Files.createFile(instance.fMmcPackJson.toPath());
            Files.createFile(instance.fVersion.toPath());
            Files.createFile(instance.fPatchesJson.toPath());

            populateInstanceCfg(instance.fInstanceCfg);
            populateMmcPackJson(instance.fMmcPackJson);
            populatePatchesJson(instance.fPatchesJson);

            copyFolder(instance.fRootDir.toPath(), Path.of("../../" + folderName));

            File tempDir = new File("../../temp");
            tempDir.mkdir();
            tempDir.delete();

            System.out.println("Managed instance created.");
        }
        else
        {
            System.out.println("Instance " + folderName + " located.");
        }

        instance.fRootDir = new File( "../../" + folderName);
        instance.fMinecraft = new File(instance.fRootDir, ".minecraft");
        instance.fJarmods = new File(instance.fRootDir, "jarmods");
        instance.fPatches = new File(instance.fRootDir, "patches");
        instance.fInstanceCfg = new File(instance.fRootDir, "instance.cfg");
        instance.fMmcPackJson = new File(instance.fRootDir, "mmc-pack.json");
        instance.fVersion = new File(instance.fRootDir, "version");
        instance.fPatchesJson = new File(instance.fPatches, "org.multimc.jarmod.bta.json");
        instance.fJarmodsBtaJar = new File(instance.fJarmods, "bta.jar");

        if (Files.size(instance.fVersion.toPath()) > 0)
        {
            instance.currentVersion = Files.readString(instance.fVersion.toPath());
        }

        instance.instanceName = instanceName;
        instance.folderPath = instance.fRootDir.toPath();

        return instance;
    }

    public boolean managedInstanceExists() throws IOException
    {
        Stream<Path> paths = Files.list(Paths.get("../../"));
        return paths.anyMatch(x -> x.endsWith(folderName));
    }

    private void populateInstanceCfg(File fInstanceCfg) throws IOException
    {
        FileWriter fw = new FileWriter(fInstanceCfg);

        fw.write("InstanceType=OneSix\n");
        fw.write("MCLaunchMethod=LauncherPart\n");
        fw.write("name=" + instanceName + "\n");

        fw.close();
    }

    private void populateMmcPackJson(File fMmcPackJson) throws IOException
    {
        FileWriter fw = new FileWriter(fMmcPackJson);
        String toWrite = """
{
    "components": [
        {
            "cachedName": "LWJGL 2",
            "cachedVersion": "2.9.4-nightly-20150209",
            "cachedVolatile": true,
            "dependencyOnly": true,
            "uid": "org.lwjgl",
            "version": "2.9.4-nightly-20150209"
        },
        {
            "cachedName": "Minecraft",
            "cachedRequires": [
                {
                    "suggests": "2.9.4-nightly-20150209",
                    "uid": "org.lwjgl"
                }
            ],
            "cachedVersion": "b1.7.3",
            "important": true,
            "uid": "net.minecraft",
            "version": "b1.7.3"
        },
        {
            "cachedName": "bta (jar mod)",
            "uid": "org.multimc.jarmod.bta"
        }
    ],
    "formatVersion": 1
}
    """;

        fw.write(toWrite);
        fw.close();
    }

    private void populatePatchesJson(File fPatchesJson) throws IOException
    {
        FileWriter fw = new FileWriter(fPatchesJson);
        String toWrite = """
{
    "formatVersion": 1,
    "jarMods": [
        {
            "MMC-displayname": "bta",
            "MMC-filename": "bta.jar",
            "MMC-hint": "local",
            "name": "org.multimc.jarmods:bta:1"
        }
    ],
    "name": "bta (jar mod)",
    "uid": "org.multimc.jarmod.bta"
}
        """;

        fw.write(toWrite);
        fw.close();
    }

    private void copyFolder(Path source, Path target, CopyOption... options)
            throws IOException
    {
        Files.walkFileTree(source, new SimpleFileVisitor<Path>()
        {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException
            {
                Files.createDirectories(target.resolve(source.relativize(dir)));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException
            {
                Files.copy(file, target.resolve(source.relativize(file)), options);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void deleteFolder(Path directory)
            throws IOException
    {
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>()
        {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
            {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
            {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

}
