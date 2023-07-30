package net.minecraft.client;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public abstract class Util
{
    public static String readStringFromURL(String requestURL)
            throws IOException
    {
        try (Scanner scanner = new Scanner(new URL(requestURL).openStream(),
                StandardCharsets.UTF_8.toString()))
        {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }

    public static void downloadFile(String url, File path)
            throws IOException
    {
        try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream()))
        {
            try (FileOutputStream fileOutputStream = new FileOutputStream(path))
            {
                byte[] dataBuffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1)
                {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
            }
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
    }

    public static boolean matchesChecksum(File file, String md5)
    {
        try
        {
            byte[] oldFileData = Files.readAllBytes(file.toPath());
            String checksum;
            try
            {
                byte[] hash = MessageDigest.getInstance("MD5").digest(oldFileData);
                checksum = new BigInteger(1, hash).toString(16);
            }
            catch (NoSuchAlgorithmException e)
            {
                e.printStackTrace();
                return false;
            }

            return checksum.equals(md5);
        }
        catch (Exception e)
        {
            return false;
        }
    }
}
