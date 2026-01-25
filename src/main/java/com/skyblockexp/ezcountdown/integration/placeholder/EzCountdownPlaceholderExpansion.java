package com.skyblockexp.ezcountdown.integration.placeholder;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.manager.CountdownManager;
import com.skyblockexp.ezcountdown.util.TimeFormat;
import com.skyblockexp.ezcountdown.bootstrap.Registry;
import com.skyblockexp.ezcountdown.util.TimeFormat.TimeParts;
import java.time.Instant;
import java.util.Locale;
import java.util.Optional;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class EzCountdownPlaceholderExpansion extends PlaceholderExpansion {

    private final Registry registry;
    private final CountdownManager manager;

    public EzCountdownPlaceholderExpansion(Registry registry) {
        this.registry = registry;
        this.manager = registry == null ? null : registry.countdowns();
    }

    @Override
    public String getIdentifier() {
        return "ezcountdown";
    }

    @Override
    public String getAuthor() {
        return registry == null ? "" : String.join(", ", registry.plugin().getDescription().getAuthors());
    }

    @Override
    public String getVersion() {
        return registry == null ? "" : registry.plugin().getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (identifier == null || identifier.isBlank()) {
            return "";
        }
        int lastUnderscore = identifier.lastIndexOf('_');
        if (lastUnderscore < 0 || lastUnderscore == identifier.length() - 1) {
            return "";
        }
        String name = identifier.substring(0, lastUnderscore);
        String field = identifier.substring(lastUnderscore + 1).toLowerCase(Locale.ROOT);
        Optional<Countdown> countdownOptional = manager.getCountdown(name);
        if (countdownOptional.isEmpty()) {
            return "";
        }
        Countdown countdown = countdownOptional.get();
        if (!countdown.isRunning() || countdown.getTargetInstant() == null) {
            return "0";
        }
        long remaining = Math.max(0L, countdown.getTargetInstant().getEpochSecond() - Instant.now().getEpochSecond());
        TimeParts parts = TimeFormat.toParts(remaining);
        return switch (field) {
            case "days" -> String.valueOf(parts.days());
            case "hours" -> String.valueOf(parts.hours());
            case "minutes" -> String.valueOf(parts.minutes());
            case "seconds" -> String.valueOf(parts.seconds());
            case "formatted" -> TimeFormat.format(parts);
            default -> "";
        };
    }
}
