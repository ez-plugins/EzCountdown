package com.skyblockexp.ezcountdown.compat.version;

import org.bukkit.Bukkit;

/**
 * Lightweight utility for querying the running Minecraft minor version at
 * runtime. Used to enable or suppress features that are not available on all
 * supported server versions (1.18+).
 *
 * <p>The version is parsed once on class-load and cached for the lifetime of
 * the server process.
 */
public final class ServerVersionUtil {

    /** Parsed Minecraft minor version (e.g. 18 for 1.18.2, 21 for 1.21.4). */
    private static final int MINOR = parseMinor();

    private ServerVersionUtil() {}

    /**
     * Returns the Minecraft minor version number.
     *
     * @return minor version (e.g. 18, 19, 20, 21)
     */
    public static int minorVersion() {
        return MINOR;
    }

    /**
     * Returns {@code true} if the server is running Minecraft 1.{@code minor}
     * or newer.
     *
     * @param minor the minimum minor version to require
     * @return {@code true} if the server version is sufficient
     */
    public static boolean atLeast(int minor) {
        return MINOR >= minor;
    }

    /**
     * Parses the Minecraft minor version from {@link Bukkit#getBukkitVersion()}.
     * The string has the format {@code "1.21.1-R0.1-SNAPSHOT"}.
     * Falls back to {@code 21} if parsing fails.
     */
    private static int parseMinor() {
        try {
            String version = Bukkit.getBukkitVersion();
            String[] parts = version.split("\\.");
            if (parts.length >= 2) {
                String raw = parts[1].replaceAll("[^0-9].*", "");
                return Integer.parseInt(raw);
            }
        } catch (Throwable ignored) {
            // Unexpected format; use a safe default
        }
        return 21;
    }
}
