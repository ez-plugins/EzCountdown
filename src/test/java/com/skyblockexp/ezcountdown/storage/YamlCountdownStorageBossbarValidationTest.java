package com.skyblockexp.ezcountdown.storage;

import com.skyblockexp.ezcountdown.manager.CountdownDefaults;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

public class YamlCountdownStorageBossbarValidationTest {

    @Test
    public void invalidBossbarValuesEmitWarningsAndFallbackToDefaults() throws Exception {
        File tmp = File.createTempFile("countdowns-bossbar-test", ".yml");
        tmp.delete();
        tmp.getParentFile().mkdirs();

        String yaml = "countdowns:\n" +
                "  bad_bossbar:\n" +
                "    type: FIXED_DATE\n" +
                "    target: \"2026-01-01 00:00\"\n" +
                "    display:\n" +
                "      types: [BOSS_BAR]\n" +
                "      bossbar:\n" +
                "        color: INVALID_COLOR\n" +
                "        style: INVALID_STYLE\n";

        try (FileWriter w = new FileWriter(tmp)) {
            w.write(yaml);
        }

        CountdownDefaults defaults = new CountdownDefaults(EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "{formatted}", "start", "end", true, ZoneId.of("UTC"));

        Logger logger = Logger.getLogger("test-bossbar");
        List<LogRecord> records = new ArrayList<>();
        Handler handler = new Handler() {
            @Override
            public void publish(LogRecord record) {
                records.add(record);
            }

            @Override
            public void flush() {}

            @Override
            public void close() throws SecurityException {}
        };
        logger.addHandler(handler);

        YamlCountdownStorage storage = new YamlCountdownStorage(defaults, tmp, logger);
        var loaded = storage.loadCountdowns();

        // ensure a countdown was loaded
        assertEquals(1, loaded.size());

        // Ensure warnings were logged for invalid color/style
        boolean hasColorWarning = records.stream().anyMatch(r -> r.getMessage().contains("Invalid boss bar color"));
        boolean hasStyleWarning = records.stream().anyMatch(r -> r.getMessage().contains("Invalid boss bar style"));
        assertTrue(hasColorWarning, "Expected warning about invalid boss bar color");
        assertTrue(hasStyleWarning, "Expected warning about invalid boss bar style");

        // Verify the loaded countdown fell back to defaults
        var cd = loaded.iterator().next();
        assertEquals(BarColor.BLUE, cd.getBossBarColor());
        assertEquals(BarStyle.SOLID, cd.getBossBarStyle());

        tmp.delete();
        logger.removeHandler(handler);
    }
}
