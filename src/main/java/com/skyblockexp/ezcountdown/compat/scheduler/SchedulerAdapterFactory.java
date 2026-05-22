package com.skyblockexp.ezcountdown.compat.scheduler;

import com.skyblockexp.ezcountdown.compat.platform.PlatformDetector;
import org.bukkit.plugin.Plugin;

/**
 * Creates the most appropriate {@link SchedulerAdapter} for the current
 * server runtime.
 *
 * <ul>
 *   <li>On <b>Folia</b>: returns a {@link FoliaSchedulerAdapter} that uses
 *       Folia's {@code GlobalRegionScheduler}.</li>
 *   <li>On <b>Paper / Spigot</b>: returns a {@link BukkitSchedulerAdapter}
 *       that wraps the legacy Bukkit scheduler.</li>
 * </ul>
 *
 * <p>If Folia is detected but its API classes are unexpectedly absent at
 * runtime, a warning is logged and the factory falls back to
 * {@link BukkitSchedulerAdapter}.
 */
public final class SchedulerAdapterFactory {

    private SchedulerAdapterFactory() {}

    /**
     * Returns the best {@link SchedulerAdapter} for the running platform.
     *
     * @param plugin the owning plugin; used to bind scheduled tasks
     * @return a non-null {@link SchedulerAdapter}
     */
    public static SchedulerAdapter create(Plugin plugin) {
        if (PlatformDetector.isFolia()) {
            try {
                return new FoliaSchedulerAdapter(plugin);
            } catch (NoClassDefFoundError e) {
                plugin.getLogger().warning("[EzCountdown] Folia classes not found, falling back to Bukkit scheduler. Reason: " + e.getMessage());
            } catch (Throwable e) {
                plugin.getLogger().warning(
                        "[EzCountdown] Folia detected but Folia scheduler API is unavailable — "
                        + "falling back to Bukkit scheduler. Some features may not work correctly.");
            }
        }
        return new BukkitSchedulerAdapter(plugin);
    }
}
