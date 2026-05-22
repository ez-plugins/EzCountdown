package com.skyblockexp.ezcountdown.compat.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * {@link SchedulerAdapter} backed by the Bukkit scheduler.
 *
 * <p>Suitable for Paper and Spigot 1.18+. <b>Do not use on Folia</b> — the
 * Bukkit scheduler is not supported on Folia and will throw when tasks are
 * submitted.
 *
 * @see FoliaSchedulerAdapter
 */
public final class BukkitSchedulerAdapter implements SchedulerAdapter {

    private final Plugin plugin;

    public BukkitSchedulerAdapter(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public TaskHandle runTask(Runnable task) {
        BukkitTask t = Bukkit.getScheduler().runTask(plugin, task);
        return wrap(t);
    }

    @Override
    public TaskHandle runTaskTimer(Runnable task, long delayTicks, long periodTicks) {
        BukkitTask t = Bukkit.getScheduler().runTaskTimer(plugin, task, delayTicks, periodTicks);
        return wrap(t);
    }

    @Override
    public TaskHandle runTaskLater(Runnable task, long delayTicks) {
        BukkitTask t = delayTicks <= 0
                ? Bukkit.getScheduler().runTask(plugin, task)
                : Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
        return wrap(t);
    }

    @Override
    public void runTaskAsync(Runnable task) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
    }

    private static TaskHandle wrap(BukkitTask task) {
        return new TaskHandle() {
            @Override public void cancel()         { task.cancel(); }
            @Override public boolean isCancelled() { return task.isCancelled(); }
        };
    }
}
