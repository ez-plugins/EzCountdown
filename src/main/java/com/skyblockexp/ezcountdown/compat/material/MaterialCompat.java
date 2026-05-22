package com.skyblockexp.ezcountdown.compat.material;

import org.bukkit.Material;

/**
 * Compatibility helper for resolving {@link Material} names across Minecraft
 * versions.
 *
 * <p>Material names changed between 1.12 (pre-flattening) and 1.13
 * (post-flattening). This utility tries each provided name in order and
 * returns the first non-null match, falling back to {@code PAPER} if none
 * match.
 */
public final class MaterialCompat {

    private MaterialCompat() {}

    /**
     * Resolves the first matching {@link Material} from the given candidate
     * names and falls back to {@code PAPER} if none match.
     *
     * <p>Both {@link Material#matchMaterial(String)} and
     * {@link Material#getMaterial(String)} are tried for each name so that
     * flattened and legacy names are both handled.
     *
     * @param names candidate material names in preference order
     * @return a non-null {@link Material}
     */
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
