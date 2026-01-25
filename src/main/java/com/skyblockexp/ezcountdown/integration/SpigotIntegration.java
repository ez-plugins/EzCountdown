package com.skyblockexp.ezcountdown.integration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import com.skyblockexp.ezcountdown.bootstrap.Registry;

/**
 * Spigot update integration — moved from update package into integration.
 */
public final class SpigotIntegration {
    private static final String UPDATE_ENDPOINT = "https://api.spigotmc.org/legacy/update.php?resource=";
    private static final int RESOURCE_ID = 131146;
    private static final String RESOURCE_LINK = "https://www.spigotmc.org/resources/1-7-1-21-ezcountdown-%E2%8F%B3-event-new-year-custom-countdown-timers.131146/";
    private final Registry registry;
    private final int resourceId;

    public SpigotIntegration(Registry registry, int resourceId) {
        this.registry = registry;
        this.resourceId = resourceId;
    }

    public SpigotIntegration(Registry registry) {
        this.registry = registry;
        this.resourceId = RESOURCE_ID;
    }

    public void checkForUpdates(UpdateNotify notify) {
        registry.plugin().getServer().getScheduler().runTaskAsynchronously(registry.plugin(), () -> {
            try {
                String latestVersion = fetchLatestVersion();
                if (latestVersion == null || latestVersion.isBlank()) {
                    registry.plugin().getLogger().warning("SpigotMC update check returned an empty response.");
                    return;
                }
                String currentVersion = registry.plugin().getDescription().getVersion();
                if (!latestVersion.equalsIgnoreCase(currentVersion)) {
                    notify.onUpdateAvailable(currentVersion, latestVersion, RESOURCE_LINK);
                }
            } catch (Exception ex) {
                registry.plugin().getLogger().warning("Failed to check for EzCountdown updates: " + ex.getMessage());
            }
        });
    }

    private String fetchLatestVersion() throws Exception {
        URL url = new URL(UPDATE_ENDPOINT + resourceId);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setRequestProperty("User-Agent", "EzCountdown Update Checker");
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            return reader.readLine();
        } finally {
            connection.disconnect();
        }
    }

    public interface UpdateNotify {
        void onUpdateAvailable(String currentVersion, String newVersion, String link);
    }
}
