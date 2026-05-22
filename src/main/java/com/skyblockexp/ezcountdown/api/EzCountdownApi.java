package com.skyblockexp.ezcountdown.api;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.api.model.Notification;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Optional;

/**
 * Public API for other plugins to control countdowns.
 */
public interface EzCountdownApi {
    boolean startCountdown(String name);
    boolean stopCountdown(String name);
    Optional<Countdown> getCountdown(String name);
    Collection<Countdown> listCountdowns();
    boolean createCountdown(Countdown countdown);
    /**
     * Convenience helper to create a simple countdown using plugin defaults.
     * The method generates a unique name for the countdown, uses the configured
     * defaults for display types and messaging, sets the provided duration
     * (seconds) for duration/manual types and starts the countdown immediately.
     *
     * Note: the created countdown is registered globally; displays will follow
     * the plugin's configured visibility rules. The provided `players` list is
     * notified when the countdown is created but is not used to restrict display
     * visibility.
     *
     * @param type countdown type
     * @param amountSeconds duration or amount in seconds (used for DURATION/MANUAL)
     * @param players players to notify of creation (optional, may be null)
     * @return true if the countdown was created; false if a generated name collision occurred
     */
    boolean createCountdown(CountdownType type, long amountSeconds, Collection<Player> players);
    boolean deleteCountdown(String name);

    /**
     * Send an ephemeral countdown notification to all online players.
     * <p>
     * The notification runs for the configured duration and then disappears
     * automatically — it is never saved to {@code countdowns.yml} and is
     * removed from memory once it ends.
     *
     * <h3>Quick start</h3>
     * <pre>{@code
     * // 30-second action-bar countdown (plugin defaults)
     * api.sendNotification(Notification.ofSeconds(30));
     *
     * // Customised boss-bar + action-bar notification
     * api.sendNotification(
     *     Notification.builder()
     *         .duration(60)
     *         .display(DisplayType.BOSS_BAR, DisplayType.ACTION_BAR)
     *         .message("&eEvent starts in &b{formatted}")
     *         .endMessage("&aEvent has started!")
     *         .build()
     * );
     * }</pre>
     *
     * @param notification the notification descriptor
     * @return the generated countdown name if successfully created (can be
     *         passed to {@link #stopCountdown(String)} to cancel early), or
     *         {@link Optional#empty()} on a (very unlikely) name collision
     */
    Optional<String> sendNotification(Notification notification);

    /**
     * Send an ephemeral countdown notification to a specific subset of players.
     * <p>
     * Behaves identically to {@link #sendNotification(Notification)} but
     * restricts display output to the given players for the lifetime of the
     * notification.  When {@code players} is {@code null} or empty the
     * notification falls back to targeting all online players.
     *
     * <h3>Example</h3>
     * <pre>{@code
     * // Show a 10-second action-bar only to a single player
     * api.sendNotification(Notification.ofSeconds(10), List.of(player));
     *
     * // Builder approach — same result
     * api.sendNotification(
     *     Notification.builder()
     *         .duration(10)
     *         .players(List.of(player))
     *         .build()
     * );
     * }</pre>
     *
     * @param notification the notification descriptor
     * @param players      players to receive the notification;
     *                     {@code null} or empty sends to all online players
     * @return the generated countdown name wrapped in Optional, or empty on collision
     */
    default Optional<String> sendNotification(Notification notification, Collection<Player> players) {
        throw new UnsupportedOperationException(
                "Per-player sendNotification is not supported by this EzCountdownApi implementation.");
    }
}
