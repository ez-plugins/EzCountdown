package com.skyblockexp.ezcountdown.firework;

import org.bukkit.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class FireworkUtils {
    private FireworkUtils() {}

    public static Color parseColor(String input) {
        if (input == null) return null;
        String s = input.trim();
        // hex form #RRGGBB or RRGGBB
        if (s.startsWith("#")) s = s.substring(1);
        try {
            if (s.length() == 6) {
                int rgb = Integer.parseInt(s, 16);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                return Color.fromRGB(r, g, b);
            }
        } catch (NumberFormatException ignored) {
        }

        // try named constant from org.bukkit.Color
        try {
            return (Color) Color.class.getField(s.toUpperCase(Locale.ROOT)).get(null);
        } catch (Exception ignored) {
        }
        return null;
    }

    public static List<Color> parseColorList(List<String> inputs) {
        List<Color> out = new ArrayList<>();
        if (inputs == null) return out;
        for (String s : inputs) {
            Color c = parseColor(s);
            if (c != null) out.add(c);
        }
        return out;
    }
}
