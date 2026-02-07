package com.skyblockexp.ezcountdown.api.model;

import com.skyblockexp.ezcountdown.display.DisplayType;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

/**
 * Builder for {@link Countdown} to simplify construction from consumer code.
 */
public final class CountdownBuilder {
    private final String name;
    private CountdownType type = CountdownType.DURATION;
    private EnumSet<DisplayType> displayTypes = EnumSet.noneOf(DisplayType.class);
    private int updateIntervalSeconds = 1;
    private String visibilityPermission = null;
    private String formatMessage = null;
    private String startMessage = null;
    private String endMessage = null;
    private List<String> endCommands = List.of();
    private ZoneId zoneId = ZoneId.systemDefault();
    private boolean autoRestart = false;
    private String startCountdown = null;
    private int restartDelaySeconds = 0;

    /* optional runtime values that the builder can configure */
    private long durationSeconds = -1L;
    private int recurringMonth = 0;
    private int recurringDay = 0;
    private LocalTime recurringTime = null;

    private CountdownBuilder(String name) {
        this.name = Objects.requireNonNull(name, "name");
    }

    public static CountdownBuilder builder(String name) {
        return new CountdownBuilder(name);
    }

    public CountdownBuilder type(CountdownType type) {
        this.type = Objects.requireNonNull(type);
        return this;
    }

    public CountdownBuilder displayTypes(EnumSet<DisplayType> types) {
        this.displayTypes = types == null ? EnumSet.noneOf(DisplayType.class) : EnumSet.copyOf(types);
        return this;
    }

    public CountdownBuilder addDisplayType(DisplayType type) {
        if (this.displayTypes == null) this.displayTypes = EnumSet.noneOf(DisplayType.class);
        this.displayTypes.add(Objects.requireNonNull(type));
        return this;
    }

    public CountdownBuilder updateIntervalSeconds(int secs) {
        this.updateIntervalSeconds = secs;
        return this;
    }

    public CountdownBuilder visibilityPermission(String perm) {
        this.visibilityPermission = perm;
        return this;
    }

    public CountdownBuilder formatMessage(String formatMessage) {
        this.formatMessage = formatMessage;
        return this;
    }

    public CountdownBuilder startMessage(String msg) {
        this.startMessage = msg;
        return this;
    }

    public CountdownBuilder endMessage(String msg) {
        this.endMessage = msg;
        return this;
    }

    public CountdownBuilder endCommands(List<String> commands) {
        this.endCommands = commands == null ? List.of() : List.copyOf(commands);
        return this;
    }

    public CountdownBuilder addEndCommand(String cmd) {
        if (this.endCommands == null || this.endCommands.isEmpty()) this.endCommands = new ArrayList<>();
        this.endCommands.add(Objects.requireNonNull(cmd));
        return this;
    }

    public CountdownBuilder zoneId(ZoneId zoneId) {
        this.zoneId = Objects.requireNonNull(zoneId);
        return this;
    }

    public CountdownBuilder autoRestart(boolean autoRestart) {
        this.autoRestart = autoRestart;
        return this;
    }

    public CountdownBuilder startCountdown(String startCountdown) {
        this.startCountdown = startCountdown;
        return this;
    }

    public CountdownBuilder restartDelaySeconds(int seconds) {
        this.restartDelaySeconds = Math.max(0, seconds);
        return this;
    }

    public CountdownBuilder duration(Duration duration) {
        if (duration != null) this.durationSeconds = duration.getSeconds();
        return this;
    }

    public CountdownBuilder durationSeconds(long seconds) {
        this.durationSeconds = seconds;
        return this;
    }

    public CountdownBuilder recurringDate(int month, int day, LocalTime time) {
        this.recurringMonth = month;
        this.recurringDay = day;
        this.recurringTime = time;
        return this;
    }

    public Countdown build() {
        Countdown countdown = new Countdown(
            name,
            type,
            displayTypes,
            updateIntervalSeconds,
            visibilityPermission,
            formatMessage,
            startMessage,
            endMessage,
            endCommands,
            zoneId,
            autoRestart,
            startCountdown,
            restartDelaySeconds
        );

        if (durationSeconds >= 0L) countdown.setDurationSeconds(durationSeconds);
        if (recurringMonth > 0) countdown.setRecurringMonth(recurringMonth);
        if (recurringDay > 0) countdown.setRecurringDay(recurringDay);
        if (recurringTime != null) countdown.setRecurringTime(recurringTime);

        return countdown;
    }
}
