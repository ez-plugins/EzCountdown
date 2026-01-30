package com.skyblockexp.ezcountdown.firework;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.display.DisplayType;
import com.skyblockexp.ezcountdown.manager.CountdownManager;
import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.Instant;
import java.time.ZoneId;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FireworkShowFeatureTest extends MockBukkitTestBase {

    @Test
    public void endPhaseAdvancedFireworkConfigSpawnsFireworks() throws Exception {
        // prepare plugin data folder and config files
        File data = plugin.getDataFolder();
        if (!data.exists()) data.mkdirs();

        // ensure a mock world exists and write a locations.yml with a named location in it
        server.addSimpleWorld("world");
        File locFile = new File(data, "locations.yml");
        FileConfiguration locCfg = new YamlConfiguration();
        String locBase = "testloc";
        locCfg.set(locBase + ".world", "world");
        locCfg.set(locBase + ".x", 0.0);
        locCfg.set(locBase + ".y", 65.0);
        locCfg.set(locBase + ".z", 0.0);
        locCfg.set(locBase + ".yaw", 0.0);
        locCfg.set(locBase + ".pitch", 0.0);
        locCfg.save(locFile);

        // write a countdowns.yml with advanced firework config for end phase
        File cw = new File(data, "countdowns.yml");
        FileConfiguration cwCfg = new YamlConfiguration();
        String base = "countdowns.firework-test.firework.end";
        cwCfg.set(base + ".location", "testloc");
        cwCfg.set(base + ".effects[0].type", "ball");
        cwCfg.set(base + ".effects[0].colors[0]", "WHITE");
        cwCfg.set(base + ".effects[0].power", 1);
        cwCfg.set(base + ".effects[0].count", 2);
        cwCfg.set(base + ".effects[0].interval", 1);
        cwCfg.set("countdowns.firework-test.messages.format", "{formatted}");
        cwCfg.save(cw);

        // create and end a countdown to trigger end-phase fireworks
        Countdown c = new Countdown("firework-test", CountdownType.MANUAL,
                EnumSet.of(DisplayType.CHAT), 1, null, "{formatted}", "start", "end",
                java.util.List.of(), ZoneId.systemDefault());
        c.setRunning(true);
        c.setTargetInstant(Instant.now().minusSeconds(1));
        manager.createCountdown(c);

        // invoke tick which will call launchConfiguredShow
        java.lang.reflect.Method tick = CountdownManager.class.getDeclaredMethod("tick");
        tick.setAccessible(true);
        tick.invoke(manager);

        // run scheduler ticks so scheduled spawn tasks execute
        server.getScheduler().performTicks(5);

        // verify that the mock world contains at least one firework entity
        World w = server.getWorld("world");
        var fireworks = w.getEntities().stream().filter(e -> e instanceof org.bukkit.entity.Firework).map(e -> (org.bukkit.entity.Firework) e).toList();
        assertTrue(!fireworks.isEmpty(), "Expected at least one Firework entity to have been spawned");
        // and at least one of them should have configured effects
        boolean hasEffect = fireworks.stream().anyMatch(fw -> {
            try {
                return fw.getFireworkMeta() != null && !fw.getFireworkMeta().getEffects().isEmpty();
            } catch (Exception ex) { return false; }
        });
        assertTrue(hasEffect, "Expected at least one spawned Firework to have effects configured");
    }
}
