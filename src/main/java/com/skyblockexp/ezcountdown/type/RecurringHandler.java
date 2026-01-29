package com.skyblockexp.ezcountdown.type;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.manager.CountdownDefaults;
import org.bukkit.configuration.ConfigurationSection;

import java.time.Instant;
import java.time.LocalTime;

public class RecurringHandler implements CountdownTypeHandler {
    @Override
    public CountdownType getType() { return CountdownType.RECURRING; }

    @Override
    public Countdown parse(String name, ConfigurationSection section, CountdownDefaults defaults) throws IllegalArgumentException {
        Countdown countdown = new Countdown(name, getType(), defaults.displayTypes(), defaults.updateIntervalSeconds(), defaults.visibilityPermission(), defaults.formatMessage(), defaults.startMessage(), defaults.endMessage(), java.util.List.of(), defaults.zoneId());
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
