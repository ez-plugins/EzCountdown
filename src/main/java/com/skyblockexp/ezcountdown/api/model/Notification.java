package com.skyblockexp.ezcountdown.api.model;

import com.skyblockexp.ezcountdown.display.DisplayType;

import java.time.Duration;
import java.util.EnumSet;
import java.util.Objects;

/**
 * A lightweight, immutable descriptor for an ephemeral countdown notification.
 * <p>
 * Unlike a full {@link Countdown}, a {@code Notification} is not persisted to
 * storage and is automatically removed from memory once it finishes. It is
 * designed for "fire-and-forget" use-cases where a developer simply wants to
 * show a timed display (action bar, boss bar, etc.) to all online players for
 * a given number of seconds.
 *
 * <h3>Quick start</h3>
 * <pre>{@code
 * // Show a 30-second action-bar countdown (plugin defaults for message format)
 * Optional<String> id = api.sendNotification(Notification.ofSeconds(30));
 *
 * // Fully customised 60-second boss-bar + action-bar notification
 * Optional<String> id = api.sendNotification(
 *     Notification.builder()
 *         .duration(60)
 *         .display(DisplayType.BOSS_BAR, DisplayType.ACTION_BAR)
 *         .message("&eEvent starts in &b{formatted}")
 *         .endMessage("&aEvent has started!")
 *         .build()
 * );
 *
 * // Cancel early if needed
 * id.ifPresent(api::stopCountdown);
 * }</pre>
 *
 * @see NotificationBuilder
 * @see EzCountdownApi#sendNotification(Notification)
 */
public final class Notification {

    /** Default display type used when none is specified. */
    public static final EnumSet<DisplayType> DEFAULT_DISPLAY_TYPES = EnumSet.of(DisplayType.ACTION_BAR);

    /** Default format message used when none is specified. */
    public static final String DEFAULT_FORMAT_MESSAGE = "{formatted}";

    private final long durationSeconds;
    private final EnumSet<DisplayType> displayTypes;
    private final String formatMessage;
    private final String startMessage;
    private final String endMessage;

    Notification(long durationSeconds,
                 EnumSet<DisplayType> displayTypes,
                 String formatMessage,
                 String startMessage,
                 String endMessage) {
        if (durationSeconds <= 0) throw new IllegalArgumentException("durationSeconds must be > 0, got " + durationSeconds);
        this.durationSeconds = durationSeconds;
        this.displayTypes = displayTypes == null || displayTypes.isEmpty()
                ? EnumSet.copyOf(DEFAULT_DISPLAY_TYPES)
                : EnumSet.copyOf(displayTypes);
        this.formatMessage = formatMessage == null || formatMessage.isBlank()
                ? DEFAULT_FORMAT_MESSAGE
                : formatMessage;
        this.startMessage = startMessage;
        this.endMessage = endMessage;
    }

    // -----------------------------------------------------------------------
    // Factory helpers
    // -----------------------------------------------------------------------

    /**
     * Create a notification that runs for {@code seconds} seconds using the
     * default action-bar display and default format message.
     *
     * @param seconds positive duration in seconds
     * @return ready-to-send notification
     */
    public static Notification ofSeconds(long seconds) {
        return new Notification(seconds, null, null, null, null);
    }

    /**
     * Create a notification from a {@link Duration} using the default
     * action-bar display and default format message.
     *
     * @param duration positive duration
     * @return ready-to-send notification
     */
    public static Notification of(Duration duration) {
        Objects.requireNonNull(duration, "duration");
        return new Notification(duration.getSeconds(), null, null, null, null);
    }

    /**
     * Start building a {@link Notification} with full customisation.
     *
     * @return a new {@link NotificationBuilder}
     */
    public static NotificationBuilder builder() {
        return new NotificationBuilder();
    }

    // -----------------------------------------------------------------------
    // Accessors
    // -----------------------------------------------------------------------

    public long getDurationSeconds() {
        return durationSeconds;
    }

    /** Returns a defensive copy of the display types. */
    public EnumSet<DisplayType> getDisplayTypes() {
        return EnumSet.copyOf(displayTypes);
    }

    public String getFormatMessage() {
        return formatMessage;
    }

    /** May be {@code null} — no start broadcast is sent when null or blank. */
    public String getStartMessage() {
        return startMessage;
    }

    /** May be {@code null} — no end broadcast is sent when null or blank. */
    public String getEndMessage() {
        return endMessage;
    }

    @Override
    public String toString() {
        return "Notification{durationSeconds=" + durationSeconds
                + ", displayTypes=" + displayTypes
                + ", formatMessage='" + formatMessage + "'"
                + ", startMessage=" + startMessage
                + ", endMessage=" + endMessage + '}';
    }
}
