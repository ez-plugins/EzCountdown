package com.skyblockexp.ezcountdown;

import com.skyblockexp.ezcountdown.bootstrap.PluginBootstrap;
import com.skyblockexp.ezcountdown.bootstrap.Registry;
import org.bukkit.plugin.java.JavaPlugin;

public final class EzCountdownPlugin extends JavaPlugin {
    private Registry registry;

    @Override
    public void onEnable() {
        registry = PluginBootstrap.start(this);
        getLogger().info("EzCountdown enabled.");
    }

    @Override
    public void onDisable() {
        if (registry != null) {
            registry.shutdown();
            registry = null;
        }
        getLogger().info("EzCountdown disabled.");
    }
}

