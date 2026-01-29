package com.skyblockexp.ezcountdown.storage;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.display.DisplayType;
import com.skyblockexp.ezcountdown.manager.CountdownDefaults;
import com.skyblockexp.ezcountdown.type.CountdownTypeHandler;
import com.skyblockexp.ezcountdown.util.DurationParser;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class YamlCountdownStorage implements CountdownStorage {

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final CountdownDefaults defaults;
    private final File storageFile;
    private final java.util.logging.Logger logger;
    private java.util.Map<CountdownType, CountdownTypeHandler> handlers = java.util.Map.of();

    public YamlCountdownStorage(CountdownDefaults defaults, File storageFile, java.util.logging.Logger logger) {
        this.defaults = Objects.requireNonNull(defaults, "defaults");
        this.storageFile = Objects.requireNonNull(storageFile, "storageFile");
        this.logger = Objects.requireNonNull(logger, "logger");
    }

     public void setHandlerRegistry(java.util.Map<CountdownType, CountdownTypeHandler> handlers) {
        if (handlers == null) return;
        this.handlers = handlers;
    }

    @Override
    public Collection<Countdown> loadCountdowns() {
        if (!storageFile.exists()) {
            // Resource copy should be handled by bootstrap; log if missing
            logger.warning("Storage file missing: " + storageFile.getAbsolutePath());
            return new ArrayList<>();
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(storageFile);
        ConfigurationSection root = config.getConfigurationSection("countdowns");
        Collection<Countdown> countdowns = new ArrayList<>();
        if (root == null) {
            return countdowns;
        }
        for (String key : root.getKeys(false)) {
            ConfigurationSection section = root.getConfigurationSection(key);
            if (section == null) continue;
            try {
                Countdown countdown = parseCountdown(key, section);
                if (countdown != null) countdowns.add(countdown);
            } catch (IllegalArgumentException ex) {
                logger.log(Level.WARNING, "Failed to load countdown " + key + ": " + ex.getMessage(), ex);
            }
        }
        return countdowns;
    }

    @Override
    public void saveCountdowns(Collection<Countdown> countdowns) {
        FileConfiguration config = new YamlConfiguration();
        ConfigurationSection root = config.createSection("countdowns");
        for (Countdown countdown : countdowns) {
            ConfigurationSection section = root.createSection(countdown.getName());
            section.set("type", countdown.getType().name());
            section.set("running", countdown.isRunning());
            section.set("display.types", countdown.getDisplayTypes().stream().map(Enum::name).toList());
            section.set("display.update-interval", countdown.getUpdateIntervalSeconds());
            if (countdown.getVisibilityPermission() == null || countdown.getVisibilityPermission().isBlank()) {
                section.set("display.visibility", "all");
            } else {
                section.set("display.visibility", countdown.getVisibilityPermission());
            }
            section.set("messages.format", countdown.getFormatMessage());
            section.set("messages.start", countdown.getStartMessage());
            section.set("messages.end", countdown.getEndMessage());
            section.set("commands.end", countdown.getEndCommands());
            section.set("zone", countdown.getZoneId().getId());
            CountdownTypeHandler handler = handlers.get(countdown.getType());
            if (handler != null) {
                handler.serialize(countdown, section);
            } else {
                switch (countdown.getType()) {
                    case FIXED_DATE -> section.set("target", DATE_TIME_FORMAT.format(countdown.getTargetInstant().atZone(countdown.getZoneId())));
                    case DURATION, MANUAL -> section.set("duration", countdown.getDurationSeconds() + "s");
                    case RECURRING -> {
                        section.set("recurring.month", countdown.getRecurringMonth());
                        section.set("recurring.day", countdown.getRecurringDay());
                        section.set("recurring.time", countdown.getRecurringTime().toString());
                    }
                }
            }
        }
        try {
            config.save(storageFile);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Failed to save countdowns.yml", ex);
        }
    }

    private Countdown parseCountdown(String name, ConfigurationSection section) {
        CountdownType type = CountdownType.valueOf(section.getString("type", "FIXED_DATE").toUpperCase(Locale.ROOT));
        CountdownTypeHandler handler = handlers.get(type);
        if (handler != null) {
            return handler.parse(name, section, defaults);
        }
        // Fallback to legacy parsing
        EnumSet<DisplayType> displayTypes = parseDisplayTypes(section.getStringList("display.types"));
        int updateInterval = section.getInt("display.update-interval", defaults.updateIntervalSeconds());
        String visibility = section.getString("display.visibility", defaults.visibilityPermission());
        if ("all".equalsIgnoreCase(visibility)) visibility = null;
        String format = section.getString("messages.format", defaults.formatMessage());
        String start = section.getString("messages.start", defaults.startMessage());
        String end = section.getString("messages.end", defaults.endMessage());
        List<String> endCommands = section.getStringList("commands.end").stream().filter(command -> command != null && !command.isBlank()).toList();
        ZoneId zoneId = ZoneId.of(section.getString("zone", defaults.zoneId().getId()));

        Countdown countdown = new Countdown(name, type, displayTypes, updateInterval, visibility, format, start, end, endCommands, zoneId);
        countdown.setRunning(section.getBoolean("running", type != CountdownType.MANUAL));

        switch (type) {
            case FIXED_DATE -> {
                String target = section.getString("target");
                if (target == null) throw new IllegalArgumentException("Missing target date for fixed date countdown.");
                countdown.setTargetInstant(ZonedDateTime.of(LocalDateTime.parse(target, DATE_TIME_FORMAT), zoneId).toInstant());
            }
            case DURATION, MANUAL -> {
                String durationValue = section.getString("duration", "0s");
                countdown.setDurationSeconds(DurationParser.parseToSeconds(durationValue));
                if (countdown.isRunning() && countdown.getTargetInstant() == null) countdown.setTargetInstant(Instant.now().plusSeconds(countdown.getDurationSeconds()));
            }
            case RECURRING -> {
                countdown.setRecurringMonth(section.getInt("recurring.month", 1));
                countdown.setRecurringDay(section.getInt("recurring.day", 1));
                String timeValue = section.getString("recurring.time", "00:00");
                countdown.setRecurringTime(LocalTime.parse(timeValue));
                countdown.setTargetInstant(countdown.resolveNextRecurringTarget(Instant.now()));
            }
        }
        if (type == CountdownType.FIXED_DATE && countdown.isRunning() && countdown.getTargetInstant() != null) {
            if (countdown.getTargetInstant().isBefore(Instant.now())) countdown.setRunning(false);
        }
        return countdown;
    }

    private EnumSet<DisplayType> parseDisplayTypes(List<String> entries) {
        EnumSet<DisplayType> types = EnumSet.noneOf(DisplayType.class);
        if (entries == null || entries.isEmpty()) {
            types.addAll(defaults.displayTypes());
            return types;
        }
        for (String entry : entries) {
            try {
                types.add(DisplayType.valueOf(entry.toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException ex) {
                logger.warning("Unknown display type: " + entry);
            }
        }
        if (types.isEmpty()) {
            types.addAll(defaults.displayTypes());
        }
        return types;
    }
}
