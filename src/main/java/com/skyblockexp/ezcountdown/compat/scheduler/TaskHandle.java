package com.skyblockexp.ezcountdown.compat.scheduler;

/**
 * A handle to a scheduled task that can be cancelled.
 *
 * <p>Abstracts over {@link org.bukkit.scheduler.BukkitTask} (Paper/Spigot) and
 * {@code io.papermc.paper.threadedregions.scheduler.ScheduledTask} (Folia)
 * so callers never need to import platform-specific types.
 */
public interface TaskHandle {

    /** Cancels this task. No-op if already cancelled or already run. */
    void cancel();

    /** Returns {@code true} if this task has been cancelled. */
    boolean isCancelled();

    /**
     * Returns a no-op handle that is immediately in the cancelled state.
     * Useful as a null-safe sentinel value.
     *
     * @return an already-cancelled handle
     */
    static TaskHandle empty() {
        return new TaskHandle() {
            private volatile boolean cancelled;
            @Override public void cancel()           { cancelled = true; }
            @Override public boolean isCancelled()   { return cancelled; }
        };
    }
}
