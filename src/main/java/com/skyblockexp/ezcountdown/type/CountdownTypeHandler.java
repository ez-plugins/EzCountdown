package com.skyblockexp.ezcountdown.type;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.manager.CountdownDefaults;
import org.bukkit.configuration.ConfigurationSection;

import java.time.Instant;
import java.util.Map;

public interface CountdownTypeHandler {
    CountdownType getType();

    // Parse a countdown from the provided configuration section (type-specific fields)
    Countdown parse(String name, ConfigurationSection section, CountdownDefaults defaults) throws IllegalArgumentException;

    // Serialize type-specific fields into the given section
    void serialize(Countdown countdown, ConfigurationSection section);

    // Configure a newly-built Countdown instance from CLI/API args
    void configureFromCreateArgs(Countdown countdown, String[] args, CountdownDefaults defaults) throws IllegalArgumentException;

    // Lifecycle hooks
    void onStart(Countdown countdown, Instant now);
    void onStop(Countdown countdown);

    // Ensure a targetInstant exists when ticking
    void ensureTarget(Countdown countdown, Instant now);

    // Try to apply editor input (GUI) for this type; return true if input was applied
    boolean tryApplyEditorInput(String input, Countdown countdown, Instant now) throws IllegalArgumentException;

    // Optional validation hints for UI/CLI
    default Map<String, String> validationHints() { return Map.of(); }
}
