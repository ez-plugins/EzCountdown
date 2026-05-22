package com.skyblockexp.ezcountdown.compat.title;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

import java.time.Duration;

/**
 * Cross-version helpers for sending and clearing player titles.
 *
 * <ul>
 *   <li>{@link #sendTitle} — prefers the Adventure {@code showTitle(Title)} API available on
 *       Paper 1.18+ ({@link Player} implements {@code Audience}); falls back to the legacy
 *       {@code Player.sendTitle(String, String, int, int, int)} for Spigot.
 *   <li>{@link #clearTitle} — prefers Adventure {@code clearTitle()}; falls back to legacy
 *       {@code resetTitle()}.
 * </ul>
 *
 * <p>Both methods catch {@link NoSuchMethodError} and {@link NoClassDefFoundError} so the plugin
 * degrades gracefully on server software that does not bundle Adventure.
 */
public final class TitleCompat {

    /** {@code true} when the Adventure {@code Title} class and {@code Player#showTitle} are present. */
    private static final boolean HAS_SHOW_TITLE = detectShowTitle();
    /** {@code true} when the Adventure {@code Player#clearTitle} method is present. */
    private static final boolean HAS_CLEAR_TITLE = detectClearTitle();

    private TitleCompat() {}

    private static boolean detectShowTitle() {
        try {
            // showTitle(TitleLike) is present on Paper 1.18+ (Adventure Audience)
            Player.class.getMethod("showTitle", net.kyori.adventure.title.TitlePart.class, Object.class);
            return false; // wrong signature; use the simpler check below
        } catch (NoSuchMethodException ignored) {
        } catch (NoClassDefFoundError ignored) {
            return false;
        } catch (Throwable ignored) {
            return false;
        }
        try {
            Player.class.getMethod("showTitle", Title.class);
            return true;
        } catch (NoSuchMethodException ignored) {
            return false;
        } catch (NoClassDefFoundError ignored) {
            return false;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static boolean detectClearTitle() {
        try {
            Player.class.getMethod("clearTitle");
            return true;
        } catch (NoSuchMethodException ignored) {
            return false;
        } catch (NoClassDefFoundError ignored) {
            return false;
        } catch (Throwable ignored) {
            return false;
        }
    }

    /**
     * Sends a title to {@code player} using the best available API.
     *
     * <p>On Paper 1.18+ the Adventure {@code showTitle(Title)} API is used; this avoids the
     * deprecation warning from the legacy string-based {@code Player.sendTitle}. On Spigot or
     * older Paper the string overload is used as a fallback.
     *
     * @param player   the player to send the title to
     * @param title    main title text (legacy-formatted string)
     * @param fadeIn   fade-in ticks
     * @param stay     stay ticks
     * @param fadeOut  fade-out ticks
     */
    @SuppressWarnings("deprecation")
    public static void sendTitle(Player player, String title, int fadeIn, int stay, int fadeOut) {
        if (HAS_SHOW_TITLE) {
            try {
                Title.Times times = Title.Times.times(
                        Duration.ofMillis(fadeIn * 50L),
                        Duration.ofMillis(stay * 50L),
                        Duration.ofMillis(fadeOut * 50L));
                player.showTitle(Title.title(Component.text(title), Component.empty(), times));
                return;
            } catch (NoSuchMethodError | NoClassDefFoundError ignored) {
                // fall through to legacy
            }
        }
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
     * <p>Prefers the Adventure {@code clearTitle()} API on Paper 1.18+; falls back to
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
