package com.skyblockexp.ezcountdown.compat.title;

import org.bukkit.entity.Player;

/**
 * Cross-version helpers for sending and clearing player titles.
 *
 * <ul>
 *   <li>{@link #sendTitle} — uses the Bukkit {@code sendTitle(String, String, int, int, int)} API,
 *       which is present on Paper 1.8+ and all Spigot versions. The method is marked deprecated
 *       in newer Paper builds but is not removed and works correctly across all supported servers.
 *   <li>{@link #clearTitle} — prefers the Adventure {@code clearTitle()} (Paper 1.18+) which
 *       instantly removes the title; falls back to {@code resetTitle()} on Spigot.
 * </ul>
 *
 * <p>Note: the Adventure {@code Player.showTitle(Title)} API cannot be used here because
 * MockBukkit does not bridge it to the tracked title queue and real-server implementations
 * vary. {@code sendTitle} is universally reliable.</p>
 */
public final class TitleCompat {

    /** {@code true} when Adventure {@code Player#clearTitle()} is available (Paper 1.18+). */
    private static final boolean HAS_CLEAR_TITLE = detectClearTitle();

    private TitleCompat() {}

    private static boolean detectClearTitle() {
        try {
            Player.class.getMethod("clearTitle");
            return true;
        } catch (NoSuchMethodException ignored) {
            return false;
        } catch (NoClassDefFoundError ignored) {
            return false;
        }
    }

    /**
     * Sends a title to {@code player}.
     *
     * <p>Uses {@code Player.sendTitle(String, String, int, int, int)} which is available
     * on all supported server variants (Paper and Spigot 1.18+). Falls back to the
     * action-bar / chat if the method is unexpectedly absent.
     *
     * @param player   the player to send the title to
     * @param title    main title text
     * @param fadeIn   fade-in ticks
     * @param stay     stay ticks
     * @param fadeOut  fade-out ticks
     */
    @SuppressWarnings("deprecation")
    public static void sendTitle(Player player, String title, int fadeIn, int stay, int fadeOut) {
        try {
            player.sendTitle(title, "", fadeIn, stay, fadeOut);
        } catch (NoSuchMethodError | NoClassDefFoundError ex) {
            // last resort: action bar, then chat
            try {
                player.sendActionBar(title);
            } catch (NoSuchMethodError | NoClassDefFoundError ignored) {
                player.sendMessage(title);
            }
        }
    }

    /**
     * Clears any title currently shown to {@code player}.
     *
     * <p>Prefers the Adventure {@code clearTitle()} on Paper 1.18+; falls back to
     * {@code resetTitle()} for Spigot.
     */
    @SuppressWarnings("deprecation")
    public static void clearTitle(Player player) {
        if (HAS_CLEAR_TITLE) {
            try {
                player.clearTitle();
                return;
            } catch (NoSuchMethodError | NoClassDefFoundError ignored) {
                // fall through
            }
        }
        try {
            player.resetTitle();
        } catch (NoSuchMethodError | NoClassDefFoundError ignored) {
            // nothing to clear
        }
    }
}

