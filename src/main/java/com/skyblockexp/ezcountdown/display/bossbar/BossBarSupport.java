package com.skyblockexp.ezcountdown.display.bossbar;

public final class BossBarSupport {

    private static final boolean SUPPORTED = isBossBarSupported();

    private BossBarSupport() {
    }

    private static boolean isBossBarSupported() {
        try {
            Class.forName("org.bukkit.boss.BossBar");
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    public static boolean isSupported() {
        return SUPPORTED;
    }
}
