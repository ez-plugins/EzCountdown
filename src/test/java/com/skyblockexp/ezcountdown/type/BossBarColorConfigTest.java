package com.skyblockexp.ezcountdown.type;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.display.DisplayType;
import com.skyblockexp.ezcountdown.manager.CountdownDefaults;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.time.ZoneId;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Regression tests for https://github.com/... — bossbar color configured in
 * countdowns.yml was always ignored; the bar stayed blue regardless of the
 * value set under display.bossbar.color.
 *
 * Each test mirrors the user-reported config:
 *   display:
 *     bossbar:
 *       color: WHITE
 *       style: NOTCHED_6
 *
 * Before the fix every handler returned BarColor.BLUE / BarStyle.SOLID because
 * the shorter Countdown constructor was used which hard-codes those defaults.
 */
public class BossBarColorConfigTest {

    private static final CountdownDefaults DEFAULTS = new CountdownDefaults(
            EnumSet.noneOf(DisplayType.class), 1, null, "", "", "", false, ZoneId.of("UTC"));

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static org.bukkit.configuration.ConfigurationSection sectionFrom(String yaml) {
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(new StringReader(yaml));
        // The YAML contains a single top-level key "countdown" that holds the section.
        return cfg.getConfigurationSection("countdown");
    }

    // -------------------------------------------------------------------------
    // DURATION handler
    // -------------------------------------------------------------------------

    @Test
    public void durationHandler_parse_respectsBossBarColor() {
        String yaml =
                "countdown:\n" +
                "  type: DURATION\n" +
                "  duration: 3600s\n" +
                "  running: false\n" +
                "  display:\n" +
                "    types: [BOSS_BAR]\n" +
                "    bossbar:\n" +
                "      color: WHITE\n" +
                "      style: SEGMENTED_6\n";

        Countdown cd = new DurationHandler().parse("SoulBinder", sectionFrom(yaml), DEFAULTS);

        assertEquals(BarColor.WHITE, cd.getBossBarColor(),
                "DurationHandler.parse() must read display.bossbar.color from config");
        assertEquals(BarStyle.SEGMENTED_6, cd.getBossBarStyle(),
                "DurationHandler.parse() must read display.bossbar.style from config");
    }

    @Test
    public void durationHandler_parse_defaultsToBlueWhenKeyAbsent() {
        String yaml =
                "countdown:\n" +
                "  type: DURATION\n" +
                "  duration: 60s\n" +
                "  running: false\n" +
                "  display:\n" +
                "    types: [BOSS_BAR]\n";

        Countdown cd = new DurationHandler().parse("noBossBarKey", sectionFrom(yaml), DEFAULTS);

        assertEquals(BarColor.BLUE, cd.getBossBarColor(),
                "DurationHandler.parse() must fall back to BLUE when color key is absent");
        assertEquals(BarStyle.SOLID, cd.getBossBarStyle(),
                "DurationHandler.parse() must fall back to SOLID when style key is absent");
    }

    // -------------------------------------------------------------------------
    // FIXED_DATE handler
    // -------------------------------------------------------------------------

    @Test
    public void fixedDateHandler_parse_respectsBossBarColor() {
        String yaml =
                "countdown:\n" +
                "  type: FIXED_DATE\n" +
                "  target: \"2030-01-01 00:00\"\n" +
                "  display:\n" +
                "    types: [BOSS_BAR]\n" +
                "    bossbar:\n" +
                "      color: RED\n" +
                "      style: SEGMENTED_12\n";

        Countdown cd = new FixedDateHandler().parse("event", sectionFrom(yaml), DEFAULTS);

        assertEquals(BarColor.RED, cd.getBossBarColor(),
                "FixedDateHandler.parse() must read display.bossbar.color from config");
        assertEquals(BarStyle.SEGMENTED_12, cd.getBossBarStyle(),
                "FixedDateHandler.parse() must read display.bossbar.style from config");
    }

    // -------------------------------------------------------------------------
    // RECURRING handler
    // -------------------------------------------------------------------------

    @Test
    public void recurringHandler_parse_respectsBossBarColor() {
        String yaml =
                "countdown:\n" +
                "  type: RECURRING\n" +
                "  recurring:\n" +
                "    month: 6\n" +
                "    day: 15\n" +
                "    time: \"12:00\"\n" +
                "  display:\n" +
                "    types: [BOSS_BAR]\n" +
                "    bossbar:\n" +
                "      color: GREEN\n" +
                "      style: SOLID\n";

        Countdown cd = new RecurringHandler().parse("weekly", sectionFrom(yaml), DEFAULTS);

        assertEquals(BarColor.GREEN, cd.getBossBarColor(),
                "RecurringHandler.parse() must read display.bossbar.color from config");
    }

    // -------------------------------------------------------------------------
    // MANUAL handler
    // -------------------------------------------------------------------------

    @Test
    public void manualHandler_parse_respectsBossBarColor() {
        String yaml =
                "countdown:\n" +
                "  type: MANUAL\n" +
                "  duration: 120s\n" +
                "  display:\n" +
                "    types: [BOSS_BAR]\n" +
                "    bossbar:\n" +
                "      color: PURPLE\n" +
                "      style: SOLID\n";

        Countdown cd = new ManualHandler().parse("manual", sectionFrom(yaml), DEFAULTS);

        assertEquals(BarColor.PURPLE, cd.getBossBarColor(),
                "ManualHandler.parse() must read display.bossbar.color from config");
    }
}
