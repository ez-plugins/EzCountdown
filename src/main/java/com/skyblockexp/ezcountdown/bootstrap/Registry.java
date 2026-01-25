package com.skyblockexp.ezcountdown.bootstrap;

import com.skyblockexp.ezcountdown.EzCountdownPlugin;
import com.skyblockexp.ezcountdown.manager.DisplayManager;
import com.skyblockexp.ezcountdown.manager.CountdownManager;
import com.skyblockexp.ezcountdown.manager.CountdownDefaults;
import com.skyblockexp.ezcountdown.manager.MessageManager;
import com.skyblockexp.ezcountdown.storage.CountdownStorage;
import com.skyblockexp.ezcountdown.gui.GuiManager;
import com.skyblockexp.ezcountdown.command.CountdownPermissions;
import com.skyblockexp.ezcountdown.manager.LocationManager;
import com.skyblockexp.ezcountdown.command.LocationPermissions;
import org.bukkit.plugin.PluginManager;
import com.skyblockexp.ezcountdown.api.EzCountdownApi;

public final class Registry {
    private final EzCountdownPlugin plugin;
    private final MessageManager messageManager;
    private final CountdownDefaults defaults;
    private final CountdownPermissions permissions;
    private final DisplayManager displayManager;
    private final CountdownStorage storage;
    private final LocationManager locationManager;
    private final LocationPermissions locationPermissions;
    private CountdownManager countdownManager;
    private GuiManager guiManager;
    private com.skyblockexp.ezcountdown.integration.placeholder.EzCountdownPlaceholderExpansion placeholderExpansion;
    private EzCountdownApi api;

    public Registry(EzCountdownPlugin plugin, MessageManager messageManager, CountdownDefaults defaults, CountdownPermissions permissions, DisplayManager displayManager, CountdownStorage storage, LocationManager locationManager, LocationPermissions locationPermissions, CountdownManager countdownManager, GuiManager guiManager) {
        this.plugin = plugin;
        this.messageManager = messageManager;
        this.defaults = defaults;
        this.permissions = permissions;
        this.displayManager = displayManager;
        this.storage = storage;
        this.locationManager = locationManager;
        this.locationPermissions = locationPermissions;
        this.countdownManager = countdownManager;
        this.guiManager = guiManager;
    }

    public void setCountdownManager(CountdownManager countdownManager) {
        this.countdownManager = countdownManager;
    }

    public void setPlaceholderExpansion(com.skyblockexp.ezcountdown.integration.placeholder.EzCountdownPlaceholderExpansion expansion) {
        this.placeholderExpansion = expansion;
    }

    public void setApi(EzCountdownApi api) { this.api = api; }
    public EzCountdownApi api() { return api; }

    public void setGuiManager(GuiManager guiManager) {
        this.guiManager = guiManager;
    }

    public EzCountdownPlugin plugin() { return plugin; }
    public MessageManager messages() { return messageManager; }
    public CountdownDefaults defaults() { return defaults; }
    public CountdownPermissions permissions() { return permissions; }
    public DisplayManager display() { return displayManager; }
    public CountdownStorage storage() { return storage; }
    public LocationManager locations() { return locationManager; }
    public LocationPermissions locationPermissions() { return locationPermissions; }
    public CountdownManager countdowns() { return countdownManager; }

    public GuiManager gui() { return guiManager; }

    public PluginManager pluginManager() { return plugin.getServer().getPluginManager(); }

    public void shutdown() {
        if (countdownManager != null) countdownManager.shutdown();
        if (placeholderExpansion != null) {
            try { placeholderExpansion.unregister(); } catch (Exception ignored) {}
            placeholderExpansion = null;
        }
        if (api != null) {
            try { plugin.getServer().getServicesManager().unregister(EzCountdownApi.class, api); } catch (Exception ignored) {}
            api = null;
        }
        if (plugin != null) {
            try { plugin.getLogger().info("Registry shutdown complete."); } catch (Exception ignored) {}
        }
    }
}
