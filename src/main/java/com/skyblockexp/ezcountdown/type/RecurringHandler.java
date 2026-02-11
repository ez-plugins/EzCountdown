package com.skyblockexp.ezcountdown.type;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.display.DisplayType;
import com.skyblockexp.ezcountdown.manager.CountdownDefaults;
import org.bukkit.configuration.ConfigurationSection;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class RecurringHandler implements CountdownTypeHandler {
    @Override
    public CountdownType getType() { return CountdownType.RECURRING; }

    @Override
    public Countdown parse(String name, ConfigurationSection section, CountdownDefaults defaults) throws IllegalArgumentException {
        // Common fields
        List<String> displayEntries = section.isSet("display.types") ? section.getStringList("display.types") : null;
        EnumSet<DisplayType> displayTypes = EnumSet.noneOf(DisplayType.class);
        if (displayEntries == null) {
            displayTypes.addAll(defaults.displayTypes());
        } else if (displayEntries.isEmpty()) {
            // explicit empty
        } else {
            for (String e : displayEntries) {
                try { displayTypes.add(DisplayType.valueOf(e.toUpperCase(Locale.ROOT))); } catch (IllegalArgumentException ex) { /* ignore */ }
            }
            if (displayTypes.isEmpty()) displayTypes.addAll(defaults.displayTypes());
        }
        int updateInterval = section.getInt("display.update-interval", defaults.updateIntervalSeconds());
        String visibility = section.getString("display.visibility", defaults.visibilityPermission());
        if ("all".equalsIgnoreCase(visibility)) visibility = null;
        String format = section.getString("messages.format", defaults.formatMessage());
        String start = section.getString("messages.start", defaults.startMessage());
        String end = section.getString("messages.end", defaults.endMessage());
        List<String> endCommands = section.getStringList("commands.end").stream().filter(c -> c != null && !c.isBlank()).collect(Collectors.toList());
        String zoneKey = section.isSet("timezone") ? section.getString("timezone") : section.getString("zone", defaults.zoneId().getId());
        ZoneId zone = ZoneId.of(zoneKey);
        boolean autoRestart = section.getBoolean("auto_restart", false);
        String startCountdown = section.getString("start_countdown", null);
        int restartDelay = section.getInt("restart_delay_seconds", 0);
        boolean alignToClock = section.getBoolean("align_to_clock", false);
        String alignInterval = section.getString("align_interval", null);
        String missedRunRaw = section.getString("missed_run_policy", "SKIP");
        com.skyblockexp.ezcountdown.api.model.MissedRunPolicy missedPolicy;
        try {
            missedPolicy = com.skyblockexp.ezcountdown.api.model.MissedRunPolicy.valueOf(missedRunRaw.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            missedPolicy = com.skyblockexp.ezcountdown.api.model.MissedRunPolicy.SKIP;
        }

        Countdown countdown = new Countdown(name, getType(), displayTypes, updateInterval, visibility, format, start, end, endCommands, zone, autoRestart, startCountdown, restartDelay, alignToClock, alignInterval, missedPolicy);
        countdown.setRunning(section.getBoolean("running", true));
        countdown.setRecurringMonth(section.getInt("recurring.month", 1));
        countdown.setRecurringDay(section.getInt("recurring.day", 1));
        String timeValue = section.getString("recurring.time", "00:00");
        countdown.setRecurringTime(LocalTime.parse(timeValue));
        countdown.setTargetInstant(countdown.resolveNextRecurringTarget(Instant.now()));
        return countdown;
    }

    @Override
    public void serialize(Countdown countdown, ConfigurationSection section) {
        section.set("recurring.month", countdown.getRecurringMonth());
        section.set("recurring.day", countdown.getRecurringDay());
        if (countdown.getRecurringTime() != null) section.set("recurring.time", countdown.getRecurringTime().toString());
    }

    @Override
    public void configureFromCreateArgs(Countdown countdown, String[] args, CountdownDefaults defaults) throws IllegalArgumentException {
        if (args.length < 3) throw new IllegalArgumentException("Missing recurring args");
        int month = Integer.parseInt(args[0]);
        int day = Integer.parseInt(args[1]);
        String time = args[2];
        countdown.setRecurringMonth(month);
        countdown.setRecurringDay(day);
        countdown.setRecurringTime(LocalTime.parse(time));
        countdown.setTargetInstant(countdown.resolveNextRecurringTarget(Instant.now()));
        countdown.setRunning(true);
    }

    @Override
    public void onStart(Countdown countdown, Instant now) {
        countdown.setTargetInstant(countdown.resolveNextRecurringTarget(now));
    }

    @Override
    public void onStop(Countdown countdown) { /* keep recurrence config intact */ }

    @Override
    public void ensureTarget(Countdown countdown, Instant now) {
        if (countdown.getTargetInstant() == null) countdown.setTargetInstant(countdown.resolveNextRecurringTarget(now));
    }

    @Override
    public boolean tryApplyEditorInput(String input, Countdown countdown, Instant now) throws IllegalArgumentException {
        // Not implemented for free-form input; require structured editing
        throw new IllegalArgumentException("Use month day HH:mm format for recurring configuration");
    }
}
