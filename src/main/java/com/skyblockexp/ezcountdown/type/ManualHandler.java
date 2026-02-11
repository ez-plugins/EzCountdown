package com.skyblockexp.ezcountdown.type;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.display.DisplayType;
import com.skyblockexp.ezcountdown.manager.CountdownDefaults;
import com.skyblockexp.ezcountdown.util.DurationParser;
import org.bukkit.configuration.ConfigurationSection;

import java.time.Instant;
import java.time.ZoneId;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ManualHandler implements CountdownTypeHandler {
    @Override
    public CountdownType getType() { return CountdownType.MANUAL; }

    @Override
    public Countdown parse(String name, ConfigurationSection section, CountdownDefaults defaults) throws IllegalArgumentException {
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
        try { missedPolicy = com.skyblockexp.ezcountdown.api.model.MissedRunPolicy.valueOf(missedRunRaw.toUpperCase(Locale.ROOT)); } catch (IllegalArgumentException ex) { missedPolicy = com.skyblockexp.ezcountdown.api.model.MissedRunPolicy.SKIP; }

        Countdown countdown = new Countdown(name, getType(), displayTypes, updateInterval, visibility, format, start, end, endCommands, zone, autoRestart, startCountdown, restartDelay, alignToClock, alignInterval, missedPolicy);
        countdown.setRunning(section.getBoolean("running", false));
        String durationValue = section.getString("duration", "0s");
        long seconds = parseDurationLegacy(durationValue);
        countdown.setDurationSeconds(seconds);
        if (countdown.isRunning() && countdown.getTargetInstant() == null) {
            countdown.setTargetInstant(Instant.now().plusSeconds(countdown.getDurationSeconds()));
        }
        return countdown;
    }

    private long parseDurationLegacy(String value) {
        try {
            return DurationParser.parseToSeconds(value);
        } catch (IllegalArgumentException ex) {
            try {
                String v = value.trim();
                if (v.endsWith("s") || v.endsWith("S")) v = v.substring(0, v.length()-1);
                return Long.parseLong(v);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid duration: " + value);
            }
        }
    }

    @Override
    public void serialize(Countdown countdown, ConfigurationSection section) {
        section.set("duration", countdown.getDurationSeconds() + "s");
    }

    @Override
    public void configureFromCreateArgs(Countdown countdown, String[] args, CountdownDefaults defaults) throws IllegalArgumentException {
        if (args.length == 0) throw new IllegalArgumentException("Missing duration");
        long secs = parseDurationLegacy(args[0]);
        countdown.setDurationSeconds(secs);
        countdown.setRunning(false);
    }

    @Override
    public void onStart(Countdown countdown, Instant now) {
        countdown.setTargetInstant(now.plusSeconds(countdown.getDurationSeconds()));
    }

    @Override
    public void onStop(Countdown countdown) { countdown.setTargetInstant(null); }

    @Override
    public void ensureTarget(Countdown countdown, Instant now) {
        if (countdown.getTargetInstant() == null && countdown.isRunning()) {
            countdown.setTargetInstant(now.plusSeconds(countdown.getDurationSeconds()));
        }
    }

    @Override
    public boolean tryApplyEditorInput(String input, Countdown countdown, Instant now) throws IllegalArgumentException {
        long secs = parseDurationLegacy(input);
        countdown.setDurationSeconds(secs);
        return true;
    }
}
