package com.skyblockexp.ezcountdown.api.model;

import com.skyblockexp.ezcountdown.display.DisplayType;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.entity.Player;

/**
 * Fluent builder for {@link Notification}.
 *
 * <p>Obtain an instance via {@link Notification#builder()}.
 *
 * <h3>Defaults</h3>
 * <ul>
 *   <li>Display: {@code ACTION_BAR}</li>
 *   <li>Format message: {@code {formatted}}</li>
 *   <li>Start / end message: none (no broadcast)</li>
 * </ul>
 */
public final class NotificationBuilder {

    private long durationSeconds = -1L;
    private EnumSet<DisplayType> displayTypes = EnumSet.copyOf(Notification.DEFAULT_DISPLAY_TYPES);
    private String formatMessage = Notification.DEFAULT_FORMAT_MESSAGE;
    private String startMessage = null;
    private String endMessage = null;
    private Set<UUID> targetPlayers = null;

    // package-private — created via Notification.builder()
    NotificationBuilder() {}

    // -----------------------------------------------------------------------
    // Duration
    // -----------------------------------------------------------------------

    /**
     * Set the duration in seconds.
     *
     * @param seconds positive number of seconds
     */
    public NotificationBuilder duration(long seconds) {
        this.durationSeconds = seconds;
        return this;
    }

    /**
     * Set the duration from a {@link Duration} instance.
     *
     * @param duration positive duration
     */
    public NotificationBuilder duration(Duration duration) {
        Objects.requireNonNull(duration, "duration");
        this.durationSeconds = duration.getSeconds();
        return this;
    }

    // -----------------------------------------------------------------------
    // Display types
    // -----------------------------------------------------------------------

    /**
     * Replace the display type set with the given types.
     *
     * @param types one or more display types
     */
    public NotificationBuilder display(DisplayType... types) {
        if (types == null || types.length == 0) return this;
        this.displayTypes = EnumSet.copyOf(Arrays.asList(types));
        return this;
    }

    /**
     * Replace the display type set.
     *
     * @param types display types; {@code null} resets to the default
     */
    public NotificationBuilder displays(EnumSet<DisplayType> types) {
        this.displayTypes = (types == null || types.isEmpty())
                ? EnumSet.copyOf(Notification.DEFAULT_DISPLAY_TYPES)
                : EnumSet.copyOf(types);
        return this;
    }

    /**
     * Add a single display type to the current set.
     */
    public NotificationBuilder addDisplay(DisplayType type) {
        Objects.requireNonNull(type, "type");
        if (this.displayTypes == null) this.displayTypes = EnumSet.noneOf(DisplayType.class);
        this.displayTypes.add(type);
        return this;
    }

    // -----------------------------------------------------------------------
    // Messages
    // -----------------------------------------------------------------------

    /**
     * Set the format message shown during the countdown tick.
     * Supports the same placeholders as a regular countdown:
     * {@code {formatted}}, {@code {days}}, {@code {hours}},
     * {@code {minutes}}, {@code {seconds}}, {@code {name}}.
     *
     * @param message format string; {@code null} resets to default
     */
    public NotificationBuilder message(String message) {
        this.formatMessage = (message == null || message.isBlank())
                ? Notification.DEFAULT_FORMAT_MESSAGE
                : message;
        return this;
    }

    /**
     * Set the message broadcast to all players when the notification starts.
     * Pass {@code null} or blank to suppress the start broadcast.
     */
    public NotificationBuilder startMessage(String message) {
        this.startMessage = (message != null && message.isBlank()) ? null : message;
        return this;
    }

    /**
     * Set the message broadcast to all players when the notification ends.
     * Pass {@code null} or blank to suppress the end broadcast.
     */
    public NotificationBuilder endMessage(String message) {
        this.endMessage = (message != null && message.isBlank()) ? null : message;
        return this;
    }

    /**
     * Restrict this notification to the given players only.
     * Pass {@code null} or an empty collection to target all online players.
     *
     * @param players players to receive the notification
     * @return this builder
     */
    public NotificationBuilder players(Collection<? extends Player> players) {
        this.targetPlayers = (players == null || players.isEmpty()) ? null
                : players.stream().map(Player::getUniqueId)
                         .collect(Collectors.toCollection(HashSet::new));
        return this;
    }

    // -----------------------------------------------------------------------
    // Build
    // -----------------------------------------------------------------------

    /**
     * Build the {@link Notification}.
     *
     * @return the configured notification
     * @throws IllegalStateException if no duration was set
     */
    public Notification build() {
        if (durationSeconds <= 0) {
            throw new IllegalStateException(
                    "A positive duration must be set before calling build(). "
                    + "Use duration(long) or duration(Duration).");
        }
        return new Notification(durationSeconds, displayTypes, formatMessage, startMessage, endMessage, targetPlayers);
    }
}
