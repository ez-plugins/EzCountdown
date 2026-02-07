package com.skyblockexp.ezcountdown.config;

import com.skyblockexp.ezcountdown.EzCountdownPlugin;
import com.skyblockexp.ezcountdown.manager.CountdownDefaults;
import com.skyblockexp.ezcountdown.manager.MessageManager;
import com.skyblockexp.ezcountdown.command.CountdownPermissions;
import com.skyblockexp.ezcountdown.command.LocationPermissions;
import com.skyblockexp.ezcountdown.config.DiscordWebhookConfig;
import com.skyblockexp.ezcountdown.display.DisplayType;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.time.ZoneId;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

public final class ConfigService {
    private final EzCountdownPlugin plugin;
    private final MessageManager messageManager;

    public ConfigService(EzCountdownPlugin plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        ensureResource("messages.yml");
        ensureResource("discord.yml");
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        this.messageManager = new MessageManager(messagesFile);
    }

    public MessageManager messages() { return messageManager; }

    public java.util.Map<com.skyblockexp.ezcountdown.display.DisplayType, Boolean> loadDisplayOverrides() {
        FileConfiguration config = plugin.getConfig();
        java.util.Map<com.skyblockexp.ezcountdown.display.DisplayType, Boolean> overrides = new java.util.EnumMap<>(com.skyblockexp.ezcountdown.display.DisplayType.class);
        for (com.skyblockexp.ezcountdown.display.DisplayType dt : com.skyblockexp.ezcountdown.display.DisplayType.values()) {
            String key = "display-overrides.force-enable." + dt.name().toLowerCase(java.util.Locale.ROOT).replace('_', '_');
            boolean val = config.getBoolean(key, false);
            overrides.put(dt, val);
        }
        return overrides;
    }

    public CountdownDefaults loadDefaults() {
        FileConfiguration config = plugin.getConfig();
        List<String> displayEntries = config.getStringList("defaults.display-types");
        EnumSet<DisplayType> displayTypes = EnumSet.noneOf(DisplayType.class);
        // Distinguish between key absent and explicitly set empty list.
        // If the key is present, honor an explicit empty list (meaning no default displays).
        if (config.contains("defaults.display-types")) {
            for (String entry : displayEntries) {
                try { displayTypes.add(DisplayType.valueOf(entry.toUpperCase(Locale.ROOT))); } catch (IllegalArgumentException ex) { plugin.getLogger().warning("Unknown display type: " + entry); }
            }
        } else {
            // Key missing -> keep legacy behavior of defaulting to ACTION_BAR
            displayTypes.add(DisplayType.ACTION_BAR);
        }
        int updateInterval = config.getInt("defaults.update-interval", 1);
        String visibility = config.getString("defaults.visibility", "all"); if ("all".equalsIgnoreCase(visibility)) visibility = null;
        String formatMessage = messageManager.raw("defaults.format");
        String startMessage = messageManager.raw("defaults.start");
        String endMessage = messageManager.raw("defaults.end");
        boolean startOnCreate = config.getBoolean("defaults.start-on-create", true);
        ZoneId zone = ZoneId.of(config.getString("defaults.zone", ZoneId.systemDefault().getId()));
        return new CountdownDefaults(displayTypes, updateInterval, visibility, formatMessage, startMessage, endMessage, startOnCreate, zone);
    }

    public CountdownPermissions loadPermissions() {
        FileConfiguration config = plugin.getConfig();
        String base = config.getString("permissions.base", "ezcountdown.use");
        String create = config.getString("permissions.create", "ezcountdown.admin");
        String start = config.getString("permissions.start", "ezcountdown.admin");
        String stop = config.getString("permissions.stop", "ezcountdown.admin");
        String delete = config.getString("permissions.delete", "ezcountdown.admin");
        String list = config.getString("permissions.list", "ezcountdown.use");
        String info = config.getString("permissions.info", "ezcountdown.use");
        String reload = config.getString("permissions.reload", "ezcountdown.admin");
        return new CountdownPermissions(base, create, start, stop, delete, list, info, reload);
    }

    public LocationPermissions loadLocationPermissions() {
        FileConfiguration config = plugin.getConfig();
        String base = config.getString("permissions.location.base", "ezcountdown.location");
        String add = config.getString("permissions.location.add", "ezcountdown.location.add");
        String delete = config.getString("permissions.location.delete", "ezcountdown.location.delete");
        return new LocationPermissions(base, add, delete);
    }

    public DiscordWebhookConfig loadDiscordConfig() {
        File discordFile = new File(plugin.getDataFolder(), "discord.yml");
        if (discordFile.exists()) {
            return DiscordWebhookConfig.load(discordFile);
        }
        return new DiscordWebhookConfig();
    }

    private void ensureResource(String name) {
        File file = new File(plugin.getDataFolder(), name);
        if (!file.exists()) plugin.saveResource(name, false);
    }
}
