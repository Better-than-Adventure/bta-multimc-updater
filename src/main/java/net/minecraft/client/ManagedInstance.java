package net.minecraft.client;

import java.io.File;
import java.nio.file.Path;

public class ManagedInstance
{
    public String instanceName;
    public Path folderPath;
    public int currentVersionId = -1;
    public File fRootDir;
    public File fMinecraft;
    public File fJarmods;
    public File fPatches;
    public File fInstanceCfg;
    public File fMmcPackJson;
    public File fVersion;
    public File fPatchesJson;
    public File fJarmodsBtaJar;

}
