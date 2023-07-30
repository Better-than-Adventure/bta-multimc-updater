package net.minecraft.client.ops;

import com.google.common.io.Files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

public class Merge
{
    public static void mergeFiles(File oldFile, File newFile)
            throws IOException
    {
        if (!oldFile.exists())
        {
            Files.copy(newFile, oldFile);
            return;
        }

        Properties properties = new Properties();

        try (FileInputStream stream = new FileInputStream(oldFile))
        {
            properties.load(stream);
        }

        try (FileInputStream stream = new FileInputStream(newFile))
        {
            properties.load(stream);
        }

        oldFile.delete();
        try (FileOutputStream stream = new FileOutputStream(oldFile))
        {
            properties.store(stream, "Updated by merge " + new Date());
        }
    }
}
