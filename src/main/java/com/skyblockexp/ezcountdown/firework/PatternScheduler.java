package com.skyblockexp.ezcountdown.firework;

import com.skyblockexp.ezcountdown.compat.scheduler.SchedulerAdapter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;

import java.util.List;

public final class PatternScheduler {
    private static final int MAX_PER_EFFECT = 64;

    private PatternScheduler() {}

    public static void scheduleEffect(SchedulerAdapter scheduler, World world, Location center, EffectDescriptor desc) {
        if (scheduler == null || world == null || center == null || desc == null) return;
        int totalCount = Math.max(0, Math.min(desc.count, MAX_PER_EFFECT));
        if (totalCount == 0) return;
        int[] spawned = {0};
        com.skyblockexp.ezcountdown.compat.scheduler.TaskHandle[] handle = {null};
        handle[0] = scheduler.runTaskTimer(() -> {
            if (spawned[0] >= totalCount) {
                if (handle[0] != null) handle[0].cancel();
                return;
            }
            Location at = center.clone().add(desc.offsetX, desc.offsetY, desc.offsetZ);
            try {
                Firework fw = world.spawn(at, Firework.class);
                FireworkMeta meta = fw.getFireworkMeta();
                meta.addEffect(FireworkEffectBuilder.buildEffect(desc));
                meta.setPower(Math.max(0, desc.power));
                fw.setFireworkMeta(meta);
            } catch (Exception ignored) {}
            spawned[0]++;
        }, 0L, Math.max(1, desc.interval));
    }
}
