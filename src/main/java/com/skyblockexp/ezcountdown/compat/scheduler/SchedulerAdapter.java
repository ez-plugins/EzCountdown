package com.skyblockexp.ezcountdown.compat.scheduler;

/**
 * Platform-agnostic scheduler abstraction.
 *
 * <p>Implementations route task submissions to either the Bukkit scheduler
 * (Paper/Spigot via {@link BukkitSchedulerAdapter}) or Folia's
 * {@code GlobalRegionScheduler} / {@code AsyncScheduler} (via
 * {@link FoliaSchedulerAdapter}), depending on the detected runtime.
 *
 * <p>Use {@link SchedulerAdapterFactory#create(org.bukkit.plugin.Plugin)} to
 * obtain the correct implementation at start-up.
 *
 * @see BukkitSchedulerAdapter
 * @see FoliaSchedulerAdapter
 * @see SchedulerAdapterFactory
 */
public interface SchedulerAdapter {

    /**
     * Schedules {@code task} to run on the next available server tick
     * on the global (main-thread) scheduler.
     *
     * @param task the task to run
     * @return a handle that can cancel the pending execution
     */
    TaskHandle runTask(Runnable task);

    /**
     * Schedules {@code task} to run at a fixed rate on the global scheduler.
     *
     * @param task        the task to repeat
     * @param delayTicks  ticks to wait before the first run (minimum 1 on Folia)
     * @param periodTicks ticks between subsequent runs
     * @return a handle that cancels the repeating task
     */
    TaskHandle runTaskTimer(Runnable task, long delayTicks, long periodTicks);

    /**
     * Schedules {@code task} to run once after a delay on the global scheduler.
     *
     * @param task       the task to run
     * @param delayTicks ticks to wait; {@code 0} runs on the next tick
     * @return a handle that cancels the pending task
     */
    TaskHandle runTaskLater(Runnable task, long delayTicks);

    /**
     * Submits {@code task} for execution on an off-thread worker.
     *
     * <p>The task must not call Bukkit API directly from its thread.
     *
     * @param task the work to execute asynchronously
     */
    void runTaskAsync(Runnable task);
}
