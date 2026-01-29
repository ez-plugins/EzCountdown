package com.skyblockexp.ezcountdown.manager;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.manager.DisplayManager;
import com.skyblockexp.ezcountdown.api.event.CountdownEndEvent;
import com.skyblockexp.ezcountdown.api.event.CountdownStartEvent;
import com.skyblockexp.ezcountdown.api.event.CountdownTickEvent;
import com.skyblockexp.ezcountdown.storage.CountdownStorage;
import com.skyblockexp.ezcountdown.util.TimeFormat;
import com.skyblockexp.ezcountdown.util.TimeFormat.TimeParts;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.logging.Level;
import com.skyblockexp.ezcountdown.config.DiscordWebhookConfig;
import com.skyblockexp.ezcountdown.integration.discord.DiscordWebhookSender;
import org.bukkit.Bukkit;
import com.skyblockexp.ezcountdown.bootstrap.Registry;
import com.skyblockexp.ezcountdown.type.CountdownTypeHandler;
import org.bukkit.scheduler.BukkitTask;
import com.skyblockexp.ezcountdown.manager.LocationManager;
import org.bukkit.entity.Player;
import com.skyblockexp.ezcountdown.firework.FireworkShowManager;
import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public final class CountdownManager {

    private final Registry registry;
    private final DiscordWebhookConfig discordWebhookConfig;
    private final CountdownStorage storage;
    private final DisplayManager displayManager;
    private final MessageManager messageManager;
    private final Map<String, Countdown> countdowns = new ConcurrentHashMap<>();
    private final Map<String, Instant> lastUpdate = new HashMap<>();
    private final LongAdder executedCount = new LongAdder();
    private final LocationManager locationManager;
    private final FireworkShowManager fireworkShowManager = new FireworkShowManager();

    private BukkitTask task;

    public CountdownManager(Registry registry,
                            DiscordWebhookConfig discordWebhookConfig,
                            CountdownStorage storage,
                            DisplayManager displayManager,
                            MessageManager messageManager,
                            LocationManager locationManager) {
        this.registry = Objects.requireNonNull(registry, "registry");
        this.discordWebhookConfig = discordWebhookConfig;
        this.storage = Objects.requireNonNull(storage, "storage");
        this.displayManager = Objects.requireNonNull(displayManager, "displayManager");
        this.messageManager = Objects.requireNonNull(messageManager, "messageManager");
        this.locationManager = Objects.requireNonNull(locationManager, "locationManager");
        // discordWebhookConfig provided by bootstrap
    }

    public void load() {
        countdowns.clear();
        lastUpdate.clear();
        for (Countdown countdown : storage.loadCountdowns()) {
            countdowns.put(normalizeName(countdown.getName()), countdown);
        }
        startTask();
    }

    public void shutdown() {
        stopTask();
        displayManager.clearAll();
    }

    public Collection<Countdown> getCountdowns() {
        return Collections.unmodifiableCollection(countdowns.values());
    }

    public int getCountdownCount() {
        return countdowns.size();
    }

    public int getExecutedCount() {
        long value = executedCount.sum();
        return value > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) value;
    }

    public Optional<Countdown> getCountdown(String name) {
        return Optional.ofNullable(countdowns.get(normalizeName(name)));
    }

    public CountdownTypeHandler getHandler(CountdownType type) {
        return registry.getHandler(type);
    }

    public boolean createCountdown(Countdown countdown) {
        String key = normalizeName(countdown.getName());
        if (countdowns.containsKey(key)) {
            return false;
        }
        countdowns.put(key, countdown);
        if (countdown.isRunning()) {
            CountdownTypeHandler handler = registry.getHandler(countdown.getType());
            if (handler != null) {
                handler.onStart(countdown, Instant.now());
            } else {
                if (countdown.getType() == CountdownType.DURATION || countdown.getType() == CountdownType.MANUAL) {
                    countdown.setTargetInstant(Instant.now().plusSeconds(countdown.getDurationSeconds()));
                }
            }
            fireStart(countdown);
        }
        return true;
    }

    public boolean deleteCountdown(String name) {
        Countdown removed = countdowns.remove(normalizeName(name));
        if (removed != null) {
            displayManager.clearCountdown(removed);
            return true;
        }
        return false;
    }

    /**
     * Replace an existing countdown with an updated instance. Does not fire start/stop events.
     * Returns true if the countdown existed and was replaced.
     */
    public boolean updateCountdown(String name, Countdown updated) {
        String key = normalizeName(name);
        if (!countdowns.containsKey(key)) return false;
        countdowns.put(key, updated);
        return true;
    }

    public boolean startCountdown(String name) {
        Countdown countdown = countdowns.get(normalizeName(name));
        if (countdown == null) {
            return false;
        }
        if (countdown.isRunning()) {
            return true;
        }
        countdown.setRunning(true);
        CountdownTypeHandler handler = registry.getHandler(countdown.getType());
        if (handler != null) {
            handler.onStart(countdown, Instant.now());
        } else {
            if (countdown.getType() == CountdownType.DURATION || countdown.getType() == CountdownType.MANUAL) {
                countdown.setTargetInstant(Instant.now().plusSeconds(countdown.getDurationSeconds()));
            } else if (countdown.getType() == CountdownType.RECURRING) {
                countdown.setTargetInstant(countdown.resolveNextRecurringTarget(Instant.now()));
            }
        }
        fireStart(countdown);
        return true;
    }

    public boolean stopCountdown(String name) {
        Countdown countdown = countdowns.get(normalizeName(name));
        if (countdown == null) {
            return false;
        }
        countdown.setRunning(false);
        CountdownTypeHandler handler = registry.getHandler(countdown.getType());
        if (handler != null) {
            handler.onStop(countdown);
        } else {
            if (countdown.getType() == CountdownType.DURATION || countdown.getType() == CountdownType.MANUAL) {
                countdown.setTargetInstant(null);
            }
        }
        displayManager.clearCountdown(countdown);
        return true;
    }

    // Only call this after explicit config changes (create/delete/edit), not on runtime events
    public void save() {
        storage.saveCountdowns(countdowns.values());
    }

    private void startTask() {
        stopTask();
        task = Bukkit.getScheduler().runTaskTimer(registry.plugin(), this::tick, 20L, 20L);
    }

    private void stopTask() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    private void tick() {
        Instant now = Instant.now();
        for (Countdown countdown : countdowns.values()) {
            if (!countdown.isRunning()) {
                continue;
            }
            Instant target = countdown.getTargetInstant();
            if (target == null) {
                CountdownTypeHandler handler = registry.getHandler(countdown.getType());
                if (handler != null) {
                    try {
                        handler.ensureTarget(countdown, now);
                    } catch (IllegalArgumentException ignore) {
                        // handler could not produce a target
                    }
                } else {
                    if (countdown.getType() == CountdownType.DURATION || countdown.getType() == CountdownType.MANUAL) {
                        countdown.setTargetInstant(now.plusSeconds(countdown.getDurationSeconds()));
                    } else if (countdown.getType() == CountdownType.RECURRING) {
                        countdown.setTargetInstant(countdown.resolveNextRecurringTarget(now));
                    } else {
                        continue;
                    }
                }
                target = countdown.getTargetInstant();
            }

            long remaining = Math.max(0L, target.getEpochSecond() - now.getEpochSecond());
            Instant last = lastUpdate.getOrDefault(countdown.getName(), Instant.EPOCH);
            if (now.isAfter(last.plusSeconds(countdown.getUpdateIntervalSeconds()))) {
                lastUpdate.put(countdown.getName(), now);
                String message = buildMessage(countdown, remaining);
                displayManager.display(countdown, message, remaining);
                fireTick(countdown, remaining);
            }

            if (remaining <= 0L) {
                fireEnd(countdown);
                CountdownTypeHandler endHandler = registry.getHandler(countdown.getType());
                if (endHandler != null) {
                    try {
                        endHandler.ensureTarget(countdown, now);
                    } catch (Exception ignore) {}
                }
                if (countdown.getTargetInstant() != null && countdown.isRunning()) {
                    // handler scheduled a new target (recurring behavior)
                    continue;
                }
                countdown.setRunning(false);
                displayManager.clearCountdown(countdown);
            }
        }
    }

    private String buildMessage(Countdown countdown, long remaining) {
        TimeParts parts = TimeFormat.toParts(remaining);
        String formatted = TimeFormat.format(parts);
        String message = countdown.getFormatMessage();
        message = message.replace("{name}", countdown.getName())
                .replace("{days}", String.valueOf(parts.days()))
                .replace("{hours}", String.valueOf(parts.hours()))
                .replace("{minutes}", String.valueOf(parts.minutes()))
                .replace("{seconds}", String.valueOf(parts.seconds()))
                .replace("{formatted}", formatted);
        return messageManager.formatWithPrefix(message, Map.of());
    }

    private void fireStart(Countdown countdown) {
        String message = countdown.getStartMessage();
        if (message != null && !message.isBlank()) {
            displayManager.broadcastMessage(messageManager.formatWithPrefix(message,
                    Map.of("name", countdown.getName())));
        }
        // Teleport players if configured
        String teleportLocation = getTeleportLocation(countdown, "start");
        if (teleportLocation != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                locationManager.teleportPlayer(player, teleportLocation);
            }
        }
        // Firework show if configured
        fireworkShowManager.launchConfiguredShow(registry.plugin(), countdown, "start");
        // Discord webhook integration: send on countdown_start
        sendDiscordWebhooks("countdown_start", countdown, null);
        try {
            Bukkit.getPluginManager().callEvent(new CountdownStartEvent(countdown));
        } catch (Exception ex) {
                registry.plugin().getLogger().log(Level.WARNING, "Error while firing CountdownStartEvent", ex);
        }
    }

    private void fireTick(Countdown countdown, long remaining) {
        try {
            Bukkit.getPluginManager().callEvent(new CountdownTickEvent(countdown, remaining));
        } catch (Exception ex) {
            registry.plugin().getLogger().log(Level.WARNING, "Error while firing CountdownTickEvent", ex);
        }
    }

    private void fireEnd(Countdown countdown) {
        String message = countdown.getEndMessage();
        if (message != null && !message.isBlank()) {
            displayManager.broadcastMessage(messageManager.formatWithPrefix(message,
                    Map.of("name", countdown.getName())));
        }
        // Teleport players if configured
        String teleportLocation = getTeleportLocation(countdown, "end");
        if (teleportLocation != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                locationManager.teleportPlayer(player, teleportLocation);
            }
        }
        // Firework show if configured
        fireworkShowManager.launchConfiguredShow(registry.plugin(), countdown, "end");
        executeEndCommands(countdown);
        executedCount.increment();
        // Discord webhook integration: send on countdown_end
        sendDiscordWebhooks("countdown_end", countdown, null);
        try {
            Bukkit.getPluginManager().callEvent(new CountdownEndEvent(countdown));
        } catch (Exception ex) {
            registry.plugin().getLogger().log(Level.WARNING, "Error while firing CountdownEndEvent", ex);
        }
    }

    

    // Helper to get teleport location from countdown config (per countdown)
    private String getTeleportLocation(Countdown countdown, String phase) {
        // This assumes the Countdown class is extended to store teleport config, or you can fetch from config if needed
        // For now, try to fetch from config section if available
        // (You may want to refactor to store this in Countdown object for efficiency)
        // Example: countdown.getTeleportLocation("start") or similar
        // Here, fallback to config lookup if not present in object
        // TODO: Refactor as needed for your actual config storage
        try {
            org.bukkit.configuration.file.FileConfiguration config = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(
                new java.io.File(registry.plugin().getDataFolder(), "countdowns.yml"));
            String path = "countdowns." + countdown.getName() + ".teleport." + phase;
            if (config.contains(path)) {
                return config.getString(path);
            }
        } catch (Exception ignored) {}
        return null;
    }

    private void sendDiscordWebhooks(String trigger, Countdown countdown, Map<String, String> extraPlaceholders) {
        if (discordWebhookConfig == null) return;
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("{event}", countdown.getName());
        placeholders.put("{trigger}", trigger);
        if (extraPlaceholders != null) {
            placeholders.putAll(extraPlaceholders);
        }
        for (DiscordWebhookConfig.Webhook webhook : discordWebhookConfig.getWebhooks()) {
            if (!webhook.enabled || webhook.url == null || webhook.url.isBlank()) continue;
            if (webhook.triggers == null || webhook.triggers.isEmpty() || !webhook.triggers.contains(trigger)) continue;
            DiscordWebhookConfig.Embed embed = webhook.embed;
            if (embed == null) continue;
            // Build embed JSON with placeholder replacement
            java.util.Map<String, Object> embedJson = new java.util.HashMap<>();
            embedJson.put("title", replacePlaceholders(embed.title, countdown, placeholders));
            embedJson.put("description", replacePlaceholders(embed.description, countdown, placeholders));
            if (embed.color != null && !embed.color.isBlank()) {
                try {
                    embedJson.put("color", Integer.decode(embed.color));
                } catch (Exception ignore) {}
            }
            if (embed.footer != null) {
                java.util.Map<String, Object> footer = new java.util.HashMap<>();
                footer.put("text", replacePlaceholders(embed.footer.text, countdown, placeholders));
                if (embed.footer.iconUrl != null) footer.put("icon_url", embed.footer.iconUrl);
                embedJson.put("footer", footer);
            }
            if (embed.thumbnailUrl != null && !embed.thumbnailUrl.isBlank()) {
                embedJson.put("thumbnail", java.util.Map.of("url", embed.thumbnailUrl));
            }
            if (embed.imageUrl != null && !embed.imageUrl.isBlank()) {
                embedJson.put("image", java.util.Map.of("url", embed.imageUrl));
            }
            if (embed.author != null) {
                java.util.Map<String, Object> author = new java.util.HashMap<>();
                author.put("name", replacePlaceholders(embed.author.name, countdown, placeholders));
                if (embed.author.iconUrl != null) author.put("icon_url", embed.author.iconUrl);
                embedJson.put("author", author);
            }
            DiscordWebhookSender.sendWebhook(webhook.url, embedJson);
        }
    }

    private String replacePlaceholders(String input, Countdown countdown, Map<String, String> extra) {
        if (input == null) return null;
        String out = input.replace("{countdown}", countdown.getName());
        long remaining = 0;
        if (countdown.getTargetInstant() != null) {
            remaining = Math.max(0L, countdown.getTargetInstant().getEpochSecond() - java.time.Instant.now().getEpochSecond());
        }
        TimeParts parts = TimeFormat.toParts(remaining);
        out = out.replace("{time_left}", TimeFormat.format(parts));
        out = out.replace("{days}", String.valueOf(parts.days()));
        out = out.replace("{hours}", String.valueOf(parts.hours()));
        out = out.replace("{minutes}", String.valueOf(parts.minutes()));
        out = out.replace("{seconds}", String.valueOf(parts.seconds()));
        if (extra != null) {
            for (Map.Entry<String, String> e : extra.entrySet()) {
                out = out.replace(e.getKey(), e.getValue());
            }
        }
        return out;
    }

    private void executeEndCommands(Countdown countdown) {
        if (countdown.getEndCommands().isEmpty()) {
            return;
        }
        for (String command : countdown.getEndCommands()) {
            if (command == null || command.isBlank()) {
                continue;
            }
            String resolved = command.replace("{name}", countdown.getName());
            if (resolved.startsWith("/")) {
                resolved = resolved.substring(1);
            }
            try {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), resolved);
                } catch (Exception ex) {
                    registry.plugin().getLogger().log(Level.WARNING,
                            "Error executing end command for countdown " + countdown.getName() + ": " + command, ex);
                }
        }
    }

    private String normalizeName(String name) {
        return name == null ? "" : name.toLowerCase(Locale.ROOT);
    }
}
