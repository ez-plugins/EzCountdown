package com.skyblockexp.ezcountdown.bootstrap;

import com.skyblockexp.ezcountdown.EzCountdownPlugin;
import com.skyblockexp.ezcountdown.command.CountdownCommand;
import com.skyblockexp.ezcountdown.command.CountdownPermissions;
import com.skyblockexp.ezcountdown.command.LocationPermissions;
import com.skyblockexp.ezcountdown.manager.DisplayManager;
import com.skyblockexp.ezcountdown.config.LocationsConfig;
import com.skyblockexp.ezcountdown.manager.LocationManager;
import com.skyblockexp.ezcountdown.manager.CountdownDefaults;
import com.skyblockexp.ezcountdown.manager.CountdownManager;
import com.skyblockexp.ezcountdown.manager.MessageManager;
import com.skyblockexp.ezcountdown.storage.CountdownStorage;
import com.skyblockexp.ezcountdown.storage.YamlCountdownStorage;
import com.skyblockexp.ezcountdown.listener.AnvilClickListener;
import com.skyblockexp.ezcountdown.gui.MainGui;
import com.skyblockexp.ezcountdown.gui.EditorMenu;
import com.skyblockexp.ezcountdown.gui.DisplayEditor;
import com.skyblockexp.ezcountdown.gui.CommandsEditor;
import com.skyblockexp.ezcountdown.listener.GuiClickListener;
import com.skyblockexp.ezcountdown.type.FixedDateHandler;
import com.skyblockexp.ezcountdown.type.DurationHandler;
import com.skyblockexp.ezcountdown.type.ManualHandler;
import com.skyblockexp.ezcountdown.type.RecurringHandler;
import com.skyblockexp.ezcountdown.config.ConfigService;
import com.skyblockexp.ezcountdown.config.DiscordWebhookConfig;
import com.skyblockexp.ezcountdown.gui.GuiManager;
import com.skyblockexp.ezcountdown.integration.PlaceholderIntegration;
import com.skyblockexp.ezcountdown.integration.SpigotIntegration;
import com.skyblockexp.ezcountdown.display.DisplayType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SingleLineChart;

import java.io.File;
import java.time.ZoneId;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

public final class PluginBootstrap {
    private PluginBootstrap() {}

    public static Registry start(EzCountdownPlugin plugin) {
        plugin.saveDefaultConfig();
        plugin.getDataFolder().mkdirs();
        ensureResource(plugin, "messages.yml");
        ensureResource(plugin, "discord.yml");

        // Use ConfigService to centralize config loading
        ConfigService configService = new ConfigService(plugin);
        MessageManager messageManager = configService.messages();
        CountdownDefaults defaults = configService.loadDefaults();
        CountdownPermissions permissions = configService.loadPermissions();
        DisplayManager displayManager = new DisplayManager(configService);

        ensureResource(plugin, "countdowns.yml");
        File storageFile = new File(plugin.getDataFolder(), "countdowns.yml");
        CountdownStorage storage = new YamlCountdownStorage(defaults, storageFile, plugin.getLogger());

        ensureResource(plugin, "locations.yml");
        LocationsConfig locationsConfig = new LocationsConfig(plugin.getDataFolder());
        LocationManager locationManager = new LocationManager(locationsConfig);
        LocationPermissions locationPermissions = configService.loadLocationPermissions();

        // Load Discord config
        DiscordWebhookConfig discordWebhookConfig = configService.loadDiscordConfig();

        // Create a registry placeholder so CountdownManager can reference plugin and other services
        Registry registry = new Registry(plugin, messageManager, defaults, permissions, displayManager, storage, locationManager, locationPermissions, null, null);

        // Register default countdown type handlers
        registry.registerHandler(new FixedDateHandler());
        registry.registerHandler(new DurationHandler());
        registry.registerHandler(new ManualHandler());
        registry.registerHandler(new RecurringHandler());

        // Provide handler registry to storage if using YamlCountdownStorage
        if (storage instanceof YamlCountdownStorage yamlStorage) {
            yamlStorage.setHandlerRegistry(registry.handlersMap());
        }

        CountdownManager countdownManager = new CountdownManager(registry, discordWebhookConfig, storage, displayManager, messageManager, locationManager);
        // register into registry
        registry.setCountdownManager(countdownManager);
        countdownManager.load();

        // GUI and input handlers
        AnvilClickListener anvil = new AnvilClickListener();
        // register anvil listener
        Bukkit.getPluginManager().registerEvents(anvil, plugin);

        // Create GuiManager with shared anvil handler
        GuiManager guiManager = new GuiManager(countdownManager, messageManager, permissions, anvil);

        GuiClickListener guiListener = new GuiClickListener(guiManager.mainGui(), guiManager.editorMenu(), guiManager.displayEditor(), guiManager.commandsEditor(), anvil, countdownManager, messageManager, permissions);
        Bukkit.getPluginManager().registerEvents(guiListener, plugin);

        // register gui manager into registry
        registry.setGuiManager(guiManager);

        // Command registration - provide registry so command uses shared components
        var command = plugin.getCommand("countdown");
        if (command != null) {
            CountdownCommand executor = new CountdownCommand(registry, plugin::reloadConfig);
            command.setExecutor(executor);
            command.setTabCompleter(executor);
        }

        // Discord config already loaded and provided to CountdownManager

        // Register PlaceholderAPI expansion if available and store on plugin
        var expansion = PlaceholderIntegration.registerIfPresent(registry);
        if (expansion != null) registry.setPlaceholderExpansion(expansion);

        // Initialize metrics
        try {
            Metrics metrics = new Metrics(plugin, 28545);
            metrics.addCustomChart(new SingleLineChart("countdown_total_count",
                    () -> registry == null || registry.countdowns() == null ? 0 : registry.countdowns().getCountdownCount()));
            metrics.addCustomChart(new SingleLineChart("countdown_executed",
                    () -> registry == null || registry.countdowns() == null ? 0 : registry.countdowns().getExecutedCount()));
        } catch (Exception ex) {
            plugin.getLogger().warning("Failed to start bStats metrics: " + ex.getMessage());
        }

        // Run update checker
        try {
            new SpigotIntegration(registry, 131146).checkForUpdates((currentVersion, newVersion, link) -> {
                if (isNewerVersion(currentVersion, newVersion)) {
                    String template = registry == null ? null : registry.messages().raw("update-message");
                    if (template == null || template.isEmpty()) {
                        template = "&6Update available! %link% (Current: %current_version%, New: %new_version%)";
                    }
                    String message = template.replace("%link%", link)
                            .replace("%current_version%", currentVersion)
                            .replace("%new_version%", newVersion);
                    plugin.getServer().getConsoleSender().sendMessage(message);
                }
            });
        } catch (Exception ex) {
            plugin.getLogger().warning("Failed to start update checker: " + ex.getMessage());
        }

        plugin.reloadConfig();

        return registry;
    }

    private static boolean isNewerVersion(String current, String latest) {
        String[] c = current.split("\\.");
        String[] l = latest.split("\\.");
        int len = Math.max(c.length, l.length);
        for (int i = 0; i < len; i++) {
            int cv = i < c.length ? parseIntSafe(c[i]) : 0;
            int lv = i < l.length ? parseIntSafe(l[i]) : 0;
            if (lv > cv) return true;
            if (lv < cv) return false;
        }
        return false;
    }

    private static int parseIntSafe(String s) {
        try { return Integer.parseInt(s.replaceAll("[^0-9]", "")); } catch (Exception e) { return 0; }
    }

    private static void ensureResource(EzCountdownPlugin plugin, String name) {
        File f = new File(plugin.getDataFolder(), name);
        if (!f.exists()) plugin.saveResource(name, false);
    }

    private static CountdownDefaults loadDefaults(EzCountdownPlugin plugin, MessageManager messageManager, FileConfiguration config) {
        List<String> displayEntries = config.getStringList("defaults.display-types");
        EnumSet<DisplayType> displayTypes = EnumSet.noneOf(DisplayType.class);
        for (String entry : displayEntries) {
            try { displayTypes.add(DisplayType.valueOf(entry.toUpperCase(Locale.ROOT))); } catch (IllegalArgumentException ex) { plugin.getLogger().warning("Unknown display type: " + entry); }
        }
        if (displayTypes.isEmpty()) displayTypes.add(DisplayType.ACTION_BAR);
        int updateInterval = config.getInt("defaults.update-interval", 1);
        String visibility = config.getString("defaults.visibility", "all"); if ("all".equalsIgnoreCase(visibility)) visibility = null;
        String formatMessage = messageManager.raw("defaults.format");
        String startMessage = messageManager.raw("defaults.start");
        String endMessage = messageManager.raw("defaults.end");
        boolean startOnCreate = config.getBoolean("defaults.start-on-create", true);
        ZoneId zone = ZoneId.of(config.getString("defaults.zone", ZoneId.systemDefault().getId()));
        return new CountdownDefaults(displayTypes, updateInterval, visibility, formatMessage, startMessage, endMessage, startOnCreate, zone);
    }

    private static CountdownPermissions loadPermissions(FileConfiguration config) {
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

    private static LocationPermissions loadLocationPermissions(FileConfiguration config) {
        String base = config.getString("permissions.location.base", "ezcountdown.location");
        String add = config.getString("permissions.location.add", "ezcountdown.location.add");
        String delete = config.getString("permissions.location.delete", "ezcountdown.location.delete");
        return new LocationPermissions(base, add, delete);
    }
}
