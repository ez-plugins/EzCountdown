package com.skyblockexp.ezcountdown.type;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.manager.CountdownDefaults;
import org.bukkit.configuration.ConfigurationSection;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class FixedDateHandler implements CountdownTypeHandler {
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public CountdownType getType() { return CountdownType.FIXED_DATE; }

    @Override
    public Countdown parse(String name, ConfigurationSection section, CountdownDefaults defaults) throws IllegalArgumentException {
        Countdown countdown = new Countdown(name, getType(), defaults.displayTypes(), defaults.updateIntervalSeconds(), defaults.visibilityPermission(), defaults.formatMessage(), defaults.startMessage(), defaults.endMessage(), java.util.List.<String>of(), defaults.zoneId(), false, null, 0);
        countdown.setRunning(section.getBoolean("running", defaults.startOnCreate()));
        String target = section.getString("target");
        if (target == null) throw new IllegalArgumentException("Missing target date for fixed date countdown.");
        ZoneId zone = countdown.getZoneId();
        LocalDateTime dt = LocalDateTime.parse(target, DATE_TIME_FORMAT);
        countdown.setTargetInstant(dt.atZone(zone).toInstant());
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
