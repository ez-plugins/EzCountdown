package com.skyblockexp.ezcountdown.compat.platform;

/**
 * Detects the server platform at class-load time and caches the result.
 *
 * <p>Folia is identified by the presence of
 * {@code io.papermc.paper.threadedregions.RegionizedServer}, a class that
 * exists only in Folia and is absent from Paper and Spigot. The check is
 * performed once via {@link Class#forName(String)} and the result is stored
 * in a {@code static final} field so subsequent calls are free.
 */
public final class PlatformDetector {

    /** {@code true} when the server runtime is Folia. */
    private static final boolean FOLIA;

    static {
        boolean folia;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            folia = true;
        } catch (ClassNotFoundException ignored) {
            folia = false;
        }
        FOLIA = folia;
    }

    private PlatformDetector() {}

    /**
     * Returns {@code true} if the current server runtime is Folia.
     *
     * @return {@code true} on Folia, {@code false} on Paper/Spigot
     */
    public static boolean isFolia() {
        return FOLIA;
    }

    /**
     * Returns a human-readable platform name suitable for log messages.
     *
     * @return {@code "Folia"} or {@code "Paper/Spigot"}
     */
    public static String platformName() {
        return FOLIA ? "Folia" : "Paper/Spigot";
    }
}
