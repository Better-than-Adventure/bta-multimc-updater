package net.betterthanadventure.updater.backend;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IProgressReporter {
    enum ProgressMessage {
        CREATING_DIR,
        DOWNLOADING_FILE,
        VERIFYING_FILE,
        COPYING_FILE,
        CREATING_PROPERTIES_FILE
    }
    void initialize();
    void setProgress(final float progress);
    void setDone(final boolean done);
    void setProgressMessage(final @NotNull ProgressMessage message, final @Nullable String context);
    boolean getDone();

}
