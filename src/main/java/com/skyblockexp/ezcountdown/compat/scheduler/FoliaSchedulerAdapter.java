package com.skyblockexp.ezcountdown.compat.scheduler;

import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.plugin.Plugin;

/**
 * {@link SchedulerAdapter} backed by Folia's {@link GlobalRegionScheduler}.
 *
 * <p>This class directly imports Folia API types, so it must <b>never be
 * instantiated on servers that do not provide those types</b> (i.e. Paper
 * 1.18-1.20.4 and Spigot). The {@link SchedulerAdapterFactory} guards the
 * instantiation so that the JVM only class-loads this type when Folia is
 * detected at runtime; if the Folia API is absent the resulting
 * {@link NoClassDefFoundError} is caught and a {@link BukkitSchedulerAdapter}
 * is used as a fallback.
 *
 * <p>Async work is dispatched via a daemon {@link Thread} to avoid the
 * complexity of Folia's {@code AsyncScheduler} for simple fire-and-forget
 * tasks (update checks, webhook calls).
 *
 * @see BukkitSchedulerAdapter
 * @see SchedulerAdapterFactory
 */
public final class FoliaSchedulerAdapter implements SchedulerAdapter {

    private final Plugin plugin;
    private final GlobalRegionScheduler global;

    public FoliaSchedulerAdapter(Plugin plugin) {
        this.plugin = plugin;
        this.global = plugin.getServer().getGlobalRegionScheduler();
    }

    @Override
    public TaskHandle runTask(Runnable task) {
        ScheduledTask t = global.run(plugin, st -> task.run());
        return wrap(t);
    }

    @Override
    public TaskHandle runTaskTimer(Runnable task, long delayTicks, long periodTicks) {
        // Folia's runAtFixedRate requires initialDelayTicks >= 1
        ScheduledTask t = global.runAtFixedRate(plugin, st -> task.run(),
                Math.max(1L, delayTicks), periodTicks);
        return wrap(t);
    }

    @Override
    public TaskHandle runTaskLater(Runnable task, long delayTicks) {
        ScheduledTask t = delayTicks <= 0
                ? global.run(plugin, st -> task.run())
                : global.runDelayed(plugin, st -> task.run(), delayTicks);
        return wrap(t);
    }

    @Override
    public void runTaskAsync(Runnable task) {
        Thread thread = new Thread(task, "EzCountdown-Async");
        thread.setDaemon(true);
        thread.start();
    }

    private static TaskHandle wrap(ScheduledTask task) {
        return new TaskHandle() {
            @Override public void cancel()         { task.cancel(); }
            @Override public boolean isCancelled() { return task.isCancelled(); }
        };
    }
}
