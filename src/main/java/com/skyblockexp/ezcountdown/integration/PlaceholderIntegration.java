package com.skyblockexp.ezcountdown.integration;

import com.skyblockexp.ezcountdown.EzCountdownPlugin;
import com.skyblockexp.ezcountdown.manager.CountdownManager;
import com.skyblockexp.ezcountdown.integration.placeholder.EzCountdownPlaceholderExpansion;
import org.bukkit.plugin.Plugin;

public final class PlaceholderIntegration {
    private PlaceholderIntegration() {}

    public static com.skyblockexp.ezcountdown.integration.placeholder.EzCountdownPlaceholderExpansion registerIfPresent(com.skyblockexp.ezcountdown.bootstrap.Registry registry) {
        if (registry == null) return null;
        Plugin placeholder = registry.plugin().getServer().getPluginManager().getPlugin("PlaceholderAPI");
        if (placeholder == null) return null;
        com.skyblockexp.ezcountdown.integration.placeholder.EzCountdownPlaceholderExpansion expansion = new com.skyblockexp.ezcountdown.integration.placeholder.EzCountdownPlaceholderExpansion(registry);
        try {
            registry.setPlaceholderExpansion(expansion);
        } catch (Exception ignored) {}
        try {
            expansion.register();
        } catch (Throwable t) {
            // In test environments PlaceholderAPI static initialization may fail; keep expansion available in the registry
        }
        return expansion;
    }

    public static void unregister(EzCountdownPlugin plugin, EzCountdownPlaceholderExpansion expansion) {
        if (expansion != null) expansion.unregister();
    }
}
