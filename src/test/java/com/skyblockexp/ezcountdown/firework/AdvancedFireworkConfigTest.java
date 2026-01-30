package com.skyblockexp.ezcountdown.firework;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class AdvancedFireworkConfigTest {

    @Test
    public void parseAdvancedEffectsList() throws Exception {
        String yaml = "countdowns:\n" +
                "  festival:\n" +
                "    firework:\n" +
                "      start:\n" +
                "        location: spawn\n" +
                "        effects:\n" +
                "          - type: STAR\n" +
                "            colors: [RED, '#00FF00']\n" +
                "            fade: ['#FFFFFF']\n" +
                "            flicker: true\n" +
                "            trail: false\n" +
                "            power: 2\n" +
                "            count: 5\n" +
                "          - type: BURST\n" +
                "            colors: BLUE\n";

        File tmp = Files.createTempFile("countdowns-", ".yml").toFile();
        try (FileWriter w = new FileWriter(tmp)) { w.write(yaml); }

        FileConfiguration cfg = YamlConfiguration.loadConfiguration(tmp);
        String base = "countdowns.festival.firework.start";
        AdvancedFireworkConfig adv = FireworkShowManager.parseAdvancedConfig(cfg, base);
        assertNotNull(adv);
        assertEquals("spawn", adv.location);
        assertEquals(2, adv.effects.size());
        EffectDescriptor e0 = adv.effects.get(0);
        assertEquals("STAR", e0.type);
        assertEquals(2, e0.colors.size());
        assertEquals(1, e0.fades.size());
        assertTrue(e0.flicker);
        assertFalse(e0.trail);
        assertEquals(2, e0.power);
        assertEquals(5, e0.count);
    }

    @Test
    public void parseLegacySingleEffect() throws Exception {
        String yaml = "countdowns:\n" +
            "  quick:\n" +
            "    firework:\n" +
            "      end:\n" +
            "        location: arena\n" +
            "        color: ORANGE\n" +
            "        power: 3\n";

        File tmp = Files.createTempFile("countdowns-", ".yml").toFile();
        try (FileWriter w = new FileWriter(tmp)) { w.write(yaml); }

        FileConfiguration cfg = YamlConfiguration.loadConfiguration(tmp);
        String base = "countdowns.quick.firework.end";
        AdvancedFireworkConfig adv = FireworkShowManager.parseAdvancedConfig(cfg, base);
        assertNotNull(adv);
        assertEquals("arena", adv.location);
        assertEquals(1, adv.effects.size());
        EffectDescriptor e = adv.effects.get(0);
        assertEquals(3, e.power);
        assertEquals(1, e.colors.size());
    }
}
