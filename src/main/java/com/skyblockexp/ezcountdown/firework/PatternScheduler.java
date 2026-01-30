package com.skyblockexp.ezcountdown.firework;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public final class PatternScheduler {
    private static final int MAX_PER_EFFECT = 64;

    private PatternScheduler() {}

    public static void scheduleEffect(Plugin plugin, World world, Location center, EffectDescriptor desc) {
        if (plugin == null || world == null || center == null || desc == null) return;
        int count = Math.max(0, Math.min(desc.count, MAX_PER_EFFECT));
        int rows = Math.max(1, 1); // per-effect we use count and interval; rows handled by phase

        // spawn all in a single task for simplicity but spaced by interval ticks
        new BukkitRunnable() {
            int spawned = 0;

            @Override
            public void run() {
                if (spawned >= count) {
                    cancel();
                    return;
                }
                // use center + offset
                Location at = center.clone().add(desc.offsetX, desc.offsetY, desc.offsetZ);
                try {
                    Firework fw = world.spawn(at, Firework.class);
                    FireworkMeta meta = fw.getFireworkMeta();
                    meta.addEffect(FireworkEffectBuilder.buildEffect(desc));
                    meta.setPower(Math.max(0, desc.power));
                    fw.setFireworkMeta(meta);
                } catch (Exception ignored) {}
                spawned++;
            }
        }.runTaskTimer(plugin, 0L, Math.max(1, desc.interval));
    }
}
