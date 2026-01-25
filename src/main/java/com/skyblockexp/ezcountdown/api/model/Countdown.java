package com.skyblockexp.ezcountdown.api.model;

import com.skyblockexp.ezcountdown.display.DisplayType;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.EnumSet;
import java.util.Objects;

/**
 * Represents a configured countdown with metadata and runtime state.
 * <p>
 * Instances are immutable for their configuration properties (name, type,
 * display types, messages, and zone) but maintain mutable runtime state such
 * as `running`, `durationSeconds`, and the computed `targetInstant`.
 */
public final class Countdown {

    /** The unique name of the countdown. */
    private final String name;

    /** The configured countdown type (fixed date, duration, manual, recurring). */
    private final CountdownType type;

    /** The set of display types enabled for this countdown. */
    private final EnumSet<DisplayType> displayTypes;

    /** How often (in seconds) the countdown updates its displays. */
    private final int updateIntervalSeconds;

    /** Optional permission required to see this countdown; null/blank means public. */
    private final String visibilityPermission;

    /** Format string used to render the countdown's time (from messages.yml). */
    private final String formatMessage;

    /** Message template shown when the countdown starts. */
    private final String startMessage;

    /** Message template shown when the countdown ends. */
    private final String endMessage;

    /** Console commands to execute when the countdown completes. */
    private final java.util.List<String> endCommands;

    /** Time zone used for date/recurring calculations. */
    private final ZoneId zoneId;

    /* Mutable runtime state */
    private long durationSeconds;
    private Instant targetInstant;
    private int recurringMonth;
    private int recurringDay;
    private LocalTime recurringTime;
    private boolean running;

    /**
     * Create a new Countdown instance.
     *
     * @param name                 unique name of the countdown
     * @param type                 countdown mode
     * @param displayTypes         enabled display types
     * @param updateIntervalSeconds update frequency in seconds
     * @param visibilityPermission permission required to view the countdown
     * @param formatMessage        formatting template for the display
     * @param startMessage         message to show on start
     * @param endMessage           message to show on end
     * @param endCommands          console commands to run on end
     * @param zoneId               time zone for date calculations
     */
    public Countdown(String name,
                     CountdownType type,
                     EnumSet<DisplayType> displayTypes,
                     int updateIntervalSeconds,
                     String visibilityPermission,
                     String formatMessage,
                     String startMessage,
                     String endMessage,
                     java.util.List<String> endCommands,
                     ZoneId zoneId) {
        this.name = Objects.requireNonNull(name, "name");
        this.type = Objects.requireNonNull(type, "type");
        this.displayTypes = displayTypes == null ? EnumSet.noneOf(DisplayType.class) : EnumSet.copyOf(displayTypes);
        this.updateIntervalSeconds = updateIntervalSeconds;
        this.visibilityPermission = visibilityPermission;
        this.formatMessage = formatMessage;
        this.startMessage = startMessage;
        this.endMessage = endMessage;
        this.endCommands = endCommands == null ? java.util.List.of() : java.util.List.copyOf(endCommands);
        this.zoneId = zoneId;
    }

    /** @return countdown name */
    public String getName() { return name; }

    /** @return configured countdown type */
    public CountdownType getType() { return type; }

    /**
     * @return a copy of the display types enabled for this countdown
     */
    public EnumSet<DisplayType> getDisplayTypes() { return EnumSet.copyOf(displayTypes); }

    /** @return update interval in seconds */
    public int getUpdateIntervalSeconds() { return updateIntervalSeconds; }

    /** @return visibility permission or null if public */
    public String getVisibilityPermission() { return visibilityPermission; }

    /** @return raw format message (from messages.yml) */
    public String getFormatMessage() { return formatMessage; }

    /** @return start message template */
    public String getStartMessage() { return startMessage; }

    /** @return end message template */
    public String getEndMessage() { return endMessage; }

    /** @return immutable copy of end commands */
    public java.util.List<String> getEndCommands() { return java.util.List.copyOf(endCommands); }

    /** @return configured ZoneId used for date operations */
    public ZoneId getZoneId() { return zoneId; }

    /** @return whether the countdown is currently running */
    public boolean isRunning() { return running; }

    /** Set whether the countdown is running. */
    public void setRunning(boolean running) { this.running = running; }

    /** @return configured duration in seconds for duration/manual types */
    public long getDurationSeconds() { return durationSeconds; }

    /** Set the duration in seconds. */
    public void setDurationSeconds(long durationSeconds) { this.durationSeconds = durationSeconds; }

    /** @return the current target Instant, or null if not set */
    public Instant getTargetInstant() { return targetInstant; }

    /** Set the target instant for fixed-date or duration-based countdowns. */
    public void setTargetInstant(Instant targetInstant) { this.targetInstant = targetInstant; }

    /** @return recurring month (1-12) for recurring countdowns */
    public int getRecurringMonth() { return recurringMonth; }

    /** Set the recurring month (1-12). */
    public void setRecurringMonth(int recurringMonth) { this.recurringMonth = recurringMonth; }

    /** @return recurring day-of-month for recurring countdowns */
    public int getRecurringDay() { return recurringDay; }

    /** Set the recurring day-of-month. */
    public void setRecurringDay(int recurringDay) { this.recurringDay = recurringDay; }

    /** @return recurring time for recurring countdowns */
    public LocalTime getRecurringTime() { return recurringTime; }

    /** Set the recurring time. */
    public void setRecurringTime(LocalTime recurringTime) { this.recurringTime = recurringTime; }

    /**
     * Resolve the next target Instant for a recurring countdown given the provided
     * current time. This considers the configured `recurringMonth`, `recurringDay`
     * and `recurringTime` as well as the countdown's `zoneId`.
     *
     * @param now current instant used as reference
     * @return the next occurrence Instant for the recurring countdown
     */
    public Instant resolveNextRecurringTarget(Instant now) {
        LocalDate currentDate = LocalDate.ofInstant(now, zoneId);
        LocalDate targetDate = LocalDate.of(currentDate.getYear(), recurringMonth, recurringDay);
        if (!targetDate.isAfter(currentDate) || targetDate.isEqual(currentDate)) {
            if (!LocalTime.ofInstant(now, zoneId).isBefore(recurringTime)) {
                targetDate = targetDate.plusYears(1);
            }
        }
        return targetDate.atTime(recurringTime).atZone(zoneId).toInstant();
    }
}
