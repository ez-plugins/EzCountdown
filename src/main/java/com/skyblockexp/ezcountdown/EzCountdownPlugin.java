package com.skyblockexp.ezcountdown;

import com.skyblockexp.ezcountdown.api.EzCountdownApi;
import com.skyblockexp.ezcountdown.bootstrap.PluginBootstrap;
import com.skyblockexp.ezcountdown.bootstrap.Registry;
import com.skyblockexp.ezcountdown.util.ServerVersionUtil;
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

        int mcMinor = ServerVersionUtil.minorVersion();
        int javaMinor = parseJavaMajor();
        getLogger().info("Server: MC " + ServerVersionUtil.versionDisplay() + " | Java " + javaMinor);

        if (mcMinor < 18) {
            getLogger().severe("EzCountdown requires Paper/Spigot 1.18 or newer. Disabling.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (javaMinor < 17) {
            getLogger().severe("EzCountdown requires Java 17 or newer. Disabling.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

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

    /** Returns the Java major version (e.g. 17, 21). */
    private static int parseJavaMajor() {
        try {
            String v = System.getProperty("java.version", "17");
            // "17.0.9", "21.0.1", "1.8.0_362"
            if (v.startsWith("1.")) {
                return Integer.parseInt(v.split("\\.")[1]);
            }
            return Integer.parseInt(v.split("[.\\-]")[0]);
        } catch (Throwable ignored) {
            return 17;
        }
    }
}


