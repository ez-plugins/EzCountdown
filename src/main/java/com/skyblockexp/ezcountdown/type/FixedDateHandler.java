package com.skyblockexp.ezcountdown.type;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.display.DisplayType;
import com.skyblockexp.ezcountdown.manager.CountdownDefaults;
import org.bukkit.configuration.ConfigurationSection;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class FixedDateHandler implements CountdownTypeHandler {
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public CountdownType getType() { return CountdownType.FIXED_DATE; }

    @Override
    public Countdown parse(String name, ConfigurationSection section, CountdownDefaults defaults) throws IllegalArgumentException {
        // Parse common configuration that may override defaults
        List<String> displayEntries = section.isSet("display.types") ? section.getStringList("display.types") : null;
        EnumSet<DisplayType> displayTypes = EnumSet.noneOf(DisplayType.class);
        if (displayEntries == null) {
            displayTypes.addAll(defaults.displayTypes());
        } else if (displayEntries.isEmpty()) {
            // explicit empty -> no displays
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
        try { missedPolicy = com.skyblockexp.ezcountdown.api.model.MissedRunPolicy.valueOf(missedRunRaw.toUpperCase(Locale.ROOT)); } catch (IllegalArgumentException ex) { missedPolicy = com.skyblockexp.ezcountdown.api.model.MissedRunPolicy.SKIP; }

        Countdown countdown = new Countdown(name, getType(), displayTypes, updateInterval, visibility, format, start, end, endCommands, zone, autoRestart, startCountdown, restartDelay, alignToClock, alignInterval, missedPolicy);
        countdown.setRunning(section.getBoolean("running", defaults.startOnCreate()));
        String target = section.getString("target");
        if (target == null) throw new IllegalArgumentException("Missing target date for fixed date countdown.");
        ZoneId zoneId = countdown.getZoneId();
        LocalDateTime dt = LocalDateTime.parse(target, DATE_TIME_FORMAT);
        countdown.setTargetInstant(dt.atZone(zoneId).toInstant());
        return countdown;
    }

    @Override
    public void serialize(Countdown countdown, ConfigurationSection section) {
        if (countdown.getTargetInstant() != null) {
            section.set("target", DATE_TIME_FORMAT.format(countdown.getTargetInstant().atZone(countdown.getZoneId())));
        }
    }

    @Override
    public void configureFromCreateArgs(Countdown countdown, String[] args, CountdownDefaults defaults) throws IllegalArgumentException {
        // args can be [date] or [date,time]
        if (args.length == 0) throw new IllegalArgumentException("Missing date");
        String date = args[0];
        String time = args.length > 1 ? args[1] : "00:00";
        LocalDateTime parsed = LocalDateTime.parse(date + " " + time, DATE_TIME_FORMAT);
        countdown.setTargetInstant(parsed.atZone(countdown.getZoneId()).toInstant());
        countdown.setRunning(defaults.startOnCreate());
    }

    @Override
    public void onStart(Countdown countdown, Instant now) { /* nothing special */ }

    @Override
    public void onStop(Countdown countdown) { /* nothing special */ }

    @Override
    public void ensureTarget(Countdown countdown, Instant now) { if (countdown.getTargetInstant() == null) throw new IllegalArgumentException("Missing target for fixed-date countdown"); }

    @Override
    public boolean tryApplyEditorInput(String input, Countdown countdown, Instant now) throws IllegalArgumentException {
        try {
            // Try parse as DATE_TIME_FORMAT
            LocalDateTime dt = LocalDateTime.parse(input, DATE_TIME_FORMAT);
            countdown.setTargetInstant(dt.atZone(countdown.getZoneId()).toInstant());
            return true;
        } catch (Exception ex) {
            try {
                Instant inst = Instant.parse(input);
                countdown.setTargetInstant(inst);
                return true;
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid date format");
            }
        }
    }
}
