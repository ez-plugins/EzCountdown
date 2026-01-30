package com.skyblockexp.ezcountdown.storage;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.display.DisplayType;
import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.ZoneId;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PersistenceFeatureTest extends MockBukkitTestBase {

    @Test
    public void saveWritesCountdownsYaml() throws Exception {
        File data = plugin.getDataFolder();
        if (!data.exists()) data.mkdirs();

        Countdown c = new Countdown("persist-test", CountdownType.MANUAL,
                EnumSet.of(DisplayType.CHAT), 1, null, "{formatted}", "start", "end",
                java.util.List.of(), ZoneId.systemDefault());
        c.setRunning(true);
        c.setDurationSeconds(10);
        manager.createCountdown(c);

        // save via manager
        manager.save();

        File f = new File(data, "countdowns.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
        assertTrue(cfg.contains("countdowns.persist-test"), "Expected saved countdowns.yml to contain the saved countdown");
        // verify some saved properties
        assertTrue(cfg.getBoolean("countdowns.persist-test.running", false), "Expected saved countdown to be marked running");
        assertTrue(cfg.getString("countdowns.persist-test.messages.format", "").contains("{formatted}"), "Expected saved format message to match");
    }
}
