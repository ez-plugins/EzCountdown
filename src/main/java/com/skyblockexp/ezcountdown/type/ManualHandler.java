package com.skyblockexp.ezcountdown.type;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.manager.CountdownDefaults;
import com.skyblockexp.ezcountdown.util.DurationParser;
import org.bukkit.configuration.ConfigurationSection;

import java.time.Instant;

public class ManualHandler implements CountdownTypeHandler {
    @Override
    public CountdownType getType() { return CountdownType.MANUAL; }

    @Override
    public Countdown parse(String name, ConfigurationSection section, CountdownDefaults defaults) throws IllegalArgumentException {
        Countdown countdown = new Countdown(name, getType(), defaults.displayTypes(), defaults.updateIntervalSeconds(), defaults.visibilityPermission(), defaults.formatMessage(), defaults.startMessage(), defaults.endMessage(), java.util.List.of(), defaults.zoneId());
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
