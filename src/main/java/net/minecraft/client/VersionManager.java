package net.minecraft.client;

import org.kohsuke.github.GHAsset;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class VersionManager
{
    private GitHub github = null;
    private GHRepository repo = null;

    public HashMap<Integer, GHRelease> releases = new HashMap<>();
    public List<Integer> releaseIds = new ArrayList<>();

    public void connect(boolean includePrereleases)
        throws IOException
    {
        System.out.println("Reading version list from GitHub...");

        github = GitHub.connectAnonymously();

        if (github.getRateLimit().getRemaining() == 0)
        {
            throw new IOException("GitHub API rate limit exceeded! Please try again later. (Max: " + github.getRateLimit().getLimit() + ")");
        }

        repo = github.getRepositoryById(394789419);

        // Get each release
        for (GHRelease release : repo.listReleases())
        {
            // Check if this is a prerelease
            if (!includePrereleases && release.isPrerelease()) continue;

            // Find version ID
            String body = release.getBody();
            int id;
            try
            {
                id = Integer.parseInt(body);
            }
            catch (NumberFormatException ignored)
            {
                System.out.println("Warning: Could not parse ID for version " + release.getName());
                continue;
            }

            // Check if ID is < 0, and if so, ignore it
            if (id < 0) continue;

            // Check if this release contains a valid bta.jar
            boolean foundBtaJar = false;
            for (GHAsset asset : release.listAssets())
            {
                if (asset.getName().equals("bta.jar"))
                {
                    foundBtaJar = true;
                    break;
                }
            }
            if (!foundBtaJar)
            {
                System.out.println("Warning: Could not find bta.jar for version " + release.getName());
                continue;
            }

            // Release checks out - add it
            releases.put(id, release);
            releaseIds.add(id);
        }

        // Sort release IDs from lowest to highest
        Collections.sort(releaseIds);

        System.out.println(releases.size() + " versions available. Most recent is " + releases.get(getMostRecentVersionId()).getName() + ".");
    }

    public GHRelease getReleaseFromVersionId(int versionId)
    {
        return releases.getOrDefault(versionId, null);
    }

    public int getMostRecentVersionId()
    {
        return releaseIds.get(releaseIds.size() - 1);
    }

}
