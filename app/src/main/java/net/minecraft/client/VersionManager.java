package net.minecraft.client;

import net.minecraft.client.version.JsonVersion;
import net.minecraft.client.version.OldStyleVersion;
import net.minecraft.client.version.Version;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class VersionManager
{
    public final List<Version> versions = new ArrayList<>();

    public VersionManager() { }

    public void connect()
        throws IOException
    {
        System.out.println("Reading version list from GitHub...");

        GitHub github = GitHub.connectAnonymously();

        if (github.getRateLimit().getRemaining() == 0)
        {
            System.out.println("GitHub API rate limit exceeded! Please try again later. (Max: " + github.getRateLimit().getLimit() + ")");
            return;
        }

        GHRepository repo = github.getRepositoryById(394789419);

        // Get each release
        for (GHRelease release : repo.listReleases())
        {
            // Check for old-style releases first
            String body = release.getBody();
            int id = -1;
            try
            {
                id = Integer.parseInt(body);
            }
            catch (NumberFormatException ignored) { }

            if (id >= 0)
            {
                // Old-style release
                versions.add(new OldStyleVersion(release));
            }
            else
            {
                // Possible new-style release - check for manifest.json
                release.listAssets().forEach(asset -> {
                    if (asset.getName().equals("manifest.json"))
                    {
                        // New-style release
                        try
                        {
                            versions.add(new JsonVersion(release));
                        }
                        catch (Exception e)
                        {
                            System.out.println("Warning: Could not parse version " + release.getName() + ": " + e.getMessage());
                        }
                    }
                });
            }
        }

        versions.sort(Comparator.comparingLong(Version::getTimestamp));
    }

    public Version getLatestVersion(boolean allowPrereleases)
    {
        for (int i = versions.size() - 1; i >= 0; i--)
        {
            Version v = versions.get(i);
            if (allowPrereleases || !v.isPrerelease())
            {
                return v;
            }
        }
        return null;
    }
}
