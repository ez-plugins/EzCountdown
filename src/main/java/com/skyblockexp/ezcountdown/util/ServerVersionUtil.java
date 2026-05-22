package com.skyblockexp.ezcountdown.util;

/**
 * @deprecated Use
 *     {@link com.skyblockexp.ezcountdown.compat.version.ServerVersionUtil}
 *     instead.
 */
@Deprecated
public final class ServerVersionUtil {

    private ServerVersionUtil() {}

    /** @deprecated Use the {@code compat.version} equivalent. */
    @Deprecated
    public static int minorVersion() {
        return com.skyblockexp.ezcountdown.compat.version.ServerVersionUtil.minorVersion();
    }

    /** @deprecated Use the {@code compat.version} equivalent. */
    @Deprecated
    public static boolean atLeast(int minor) {
        return com.skyblockexp.ezcountdown.compat.version.ServerVersionUtil.atLeast(minor);
    }

    /** @deprecated Use the {@code compat.version} equivalent. */
    @Deprecated
    public static String versionDisplay() {
        return com.skyblockexp.ezcountdown.compat.version.ServerVersionUtil.versionDisplay();
    }
}
