package com.skyblockexp.ezcountdown.compat.version;

import org.bukkit.Bukkit;

/**
 * Lightweight utility for querying the running Minecraft minor version at
 * runtime.
 *
 * <p>Two Bukkit version string formats are supported:
 * <ul>
 *   <li><b>Legacy</b>: {@code 1.21.1-R0.1-SNAPSHOT} — through MC 1.x.
 *       {@link #minorVersion()} returns {@code 21}.
 *   <li><b>New-format</b>: {@code 26.1-R0.1-SNAPSHOT} — when the leading
 *       {@code 1.} prefix is dropped. {@link #minorVersion()} returns
 *       {@code 26} so all {@link #atLeast} comparisons remain valid.
 * </ul>
 */
public final class ServerVersionUtil {

    private static final int MINOR;
    private static final String DISPLAY;

    static {
        String[] parsed = parseFrom(safeBukkitVersion());
        MINOR   = Integer.parseInt(parsed[0]);
        DISPLAY = parsed[1];
    }

    private ServerVersionUtil() {}

    /** Returns the effective Minecraft minor version (e.g. 21 or 26). */
    public static int minorVersion() {
        return MINOR;
    }

    /**
     * Returns a human-readable MC version string for log messages,
     * e.g. {@code "1.21"} or {@code "26.1"}.
     */
    public static String versionDisplay() {
        return DISPLAY;
    }

    /**
     * Returns {@code true} if the server is running Minecraft 1.{@code minor}
     * or newer.
     */
    public static boolean atLeast(int minor) {
        return MINOR >= minor;
    }

    // -------------------------------------------------------------------------
    // Package-private for unit testing
    // -------------------------------------------------------------------------

    /**
     * Parses a Bukkit version string and returns [effectiveMinor, displayString].
     * Examples:
     *   "1.21.1-R0.1-SNAPSHOT"  -> ["21", "1.21"]
     *   "26.1-R0.1-SNAPSHOT"    -> ["26", "26.1"]
     *   "27-R0.1-SNAPSHOT"      -> ["27", "27"]
     */
    static String[] parseFrom(String bukkit) {
        try {
            String clean = bukkit.split("-")[0]; // strip -R0.1-SNAPSHOT
            String[] parts = clean.split("\\.");
            if (parts.length >= 2 && "1".equals(parts[0])) {
                // Legacy: 1.21.1 -> minor=21, display="1.21"
                String minorStr = digits(parts[1]);
                Integer.parseInt(minorStr); // validate; throws on empty
                return new String[]{minorStr, "1." + minorStr};
            } else if (parts.length >= 2) {
                // New-format: 26.1 -> effective-minor=26, display="26.1"
                String majorStr = digits(parts[0]);
                Integer.parseInt(majorStr); // validate
                String subStr   = digits(parts[1]);
                return new String[]{majorStr, majorStr + "." + subStr};
            } else if (parts.length == 1) {
                // Bare major: 27 -> effective-minor=27, display="27"
                String majorStr = digits(parts[0]);
                Integer.parseInt(majorStr); // validate
                return new String[]{majorStr, majorStr};
            }
        } catch (Throwable ignored) {
            // Unexpected format; fall through to default
        }
        return new String[]{"21", "1.21"};
    }

    /** Strips everything from the first non-digit character onward. */
    private static String digits(String s) {
        return s.replaceAll("[^0-9].*", "");
    }

    private static String safeBukkitVersion() {
        try {
            return Bukkit.getBukkitVersion();
        } catch (Throwable t) {
            return "1.21-R0.1-SNAPSHOT";
        }
    }
}
