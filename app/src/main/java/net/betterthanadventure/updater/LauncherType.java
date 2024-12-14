package net.betterthanadventure.updater;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public enum LauncherType {
    UNKNOWN(null, false),
    MULTIMC_LIKE(null, true),
    MULTIMC("MultiMC", true),
    PRISM_LAUNCHER("Prism Launcher", true);

    public static @NotNull LauncherType getLauncherType() {
        if (new File("../../../prismlauncher.cfg").exists()) {
            return PRISM_LAUNCHER;
        } else if (new File("../../../multimc.cfg").exists()) {
            return MULTIMC;
        } else if (new File("../instance.cfg").exists()) {
            return MULTIMC_LIKE;
        } else {
            return UNKNOWN;
        }
    }

    private final @Nullable String name;
    private final boolean isMultiMcLike;

    LauncherType(final @Nullable String name, final boolean isMultiMcLike) {
        this.name = name;
        this.isMultiMcLike = isMultiMcLike;
    }

    public @Nullable String getName() {
        return this.name;
    }

    public boolean isMultiMcLike() {
        return this.isMultiMcLike;
    }
}
