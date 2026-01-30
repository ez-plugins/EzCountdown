package com.skyblockexp.ezcountdown.firework;

import org.bukkit.Color;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FireworkUtilsTest {

    @Test
    public void parseNamedColor() {
        Color c = FireworkUtils.parseColor("RED");
        assertNotNull(c);
        assertEquals(255, c.getRed());
    }

    @Test
    public void parseHexColor() {
        Color c = FireworkUtils.parseColor("#00FF7F");
        assertNotNull(c);
        assertEquals(0, c.getRed());
        assertEquals(255, c.getGreen());
        assertEquals(127, c.getBlue());
    }

    @Test
    public void parseInvalidColorReturnsNull() {
        Color c = FireworkUtils.parseColor("NOT_A_COLOR");
        assertNull(c);
    }

    @Test
    public void parseColorListSkipsInvalid() {
        List<String> inputs = Arrays.asList("RED", "#0000FF", "BAD");
        List<Color> out = FireworkUtils.parseColorList(inputs);
        assertNotNull(out);
        assertEquals(2, out.size());
    }
}
