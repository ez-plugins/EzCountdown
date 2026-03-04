package com.skyblockexp.ezcountdown;

import com.skyblockexp.ezcountdown.api.EzCountdownApi;
import com.skyblockexp.ezcountdown.bootstrap.PluginBootstrap;
import com.skyblockexp.ezcountdown.bootstrap.Registry;
import org.bukkit.plugin.java.JavaPlugin;

public class EzCountdownPlugin extends JavaPlugin {
    private Registry registry;
    private static EzCountdownPlugin INSTANCE;

    public static EzCountdownApi getApi() {
        if (INSTANCE == null) return null;
        return INSTANCE.registry == null ? null : INSTANCE.registry.api();
    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        registry = PluginBootstrap.start(this);
        getLogger().info("EzCountdown enabled.");
    }

    @Override
    public void onDisable() {
        if (registry != null) {
            registry.shutdown();
            registry = null;
        }
        INSTANCE = null;
        getLogger().info("EzCountdown disabled.");
    }
}

