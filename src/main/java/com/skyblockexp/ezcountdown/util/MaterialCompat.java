package com.skyblockexp.ezcountdown.util;

import org.bukkit.Material;

public final class MaterialCompat {
    private MaterialCompat() {}

    public static Material resolve(String... names) {
        if (names != null) {
            for (String n : names) {
                if (n == null) continue;
                try {
                    Material m = Material.matchMaterial(n);
                    if (m != null) return m;
                } catch (Throwable ignored) {}
                try {
                    Material m2 = Material.getMaterial(n);
                    if (m2 != null) return m2;
                } catch (Throwable ignored) {}
            }
        }
        try {
            Material paper = Material.matchMaterial("PAPER");
            return paper == null ? Material.PAPER : paper;
        } catch (Throwable ignored) {
            return Material.PAPER;
        }
    }
}
