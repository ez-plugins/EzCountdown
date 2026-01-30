package com.skyblockexp.ezcountdown.storage;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.display.DisplayType;
import com.skyblockexp.ezcountdown.manager.CountdownDefaults;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.Instant;
import java.time.ZoneId;
import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class YamlCountdownStorageRoundtripTest {

    @Test
    public void saveAndLoadRoundtrip() throws Exception {
        File tmp = File.createTempFile("countdowns-test", ".yml");
        tmp.delete();
        tmp.getParentFile().mkdirs();

        CountdownDefaults defaults = new CountdownDefaults(EnumSet.noneOf(DisplayType.class), 1, null, "{formatted}", "start", "end", true, ZoneId.of("UTC"));
        YamlCountdownStorage storage = new YamlCountdownStorage(defaults, tmp, java.util.logging.Logger.getLogger("test"));

        Countdown d1 = new Countdown("duration1", CountdownType.DURATION, EnumSet.noneOf(DisplayType.class), 1, null, "{formatted}", "s", "e", List.of(), ZoneId.of("UTC"));
        d1.setDurationSeconds(10);
        d1.setRunning(false);

        Countdown f1 = new Countdown("fixed1", CountdownType.FIXED_DATE, EnumSet.noneOf(DisplayType.class), 1, null, "{formatted}", "s", "e", List.of(), ZoneId.of("UTC"));
        f1.setRunning(true);
        f1.setTargetInstant(Instant.now().plusSeconds(3600));

        storage.saveCountdowns(List.of(d1, f1));

        // reload from file
        YamlCountdownStorage reloader = new YamlCountdownStorage(defaults, tmp, java.util.logging.Logger.getLogger("test2"));
        var loaded = reloader.loadCountdowns();
        assertEquals(2, loaded.size());
        boolean foundDuration = loaded.stream().anyMatch(c -> c.getName().equals("duration1") && c.getType() == CountdownType.DURATION);
        boolean foundFixed = loaded.stream().anyMatch(c -> c.getName().equals("fixed1") && c.getType() == CountdownType.FIXED_DATE);
        assertTrue(foundDuration);
        assertTrue(foundFixed);
        tmp.delete();
    }
}
