package com.skyblockexp.ezcountdown.bootstrap;

import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceRegistrationFeatureTest extends MockBukkitTestBase {

    @Test
    public void pluginRegistersServices_onEnable_and_unregisters_onDisable() {
        assertNotNull(plugin, "Plugin should be loaded");
        assertNotNull(registry, "Registry should be available after plugin startup");

        // Basic assertion: gui manager and countdown manager should be present
        assertNotNull(registry.countdowns(), "CountdownManager should be initialized");
        assertNotNull(registry.gui(), "GuiManager should be initialized");

        // Ensure setting an API instance is stored and that shutdown clears it without throwing
        com.skyblockexp.ezcountdown.api.EzCountdownApi api = new com.skyblockexp.ezcountdown.api.EzCountdownApiImpl(registry);
        registry.setApi(api);
        assertNotNull(registry.api(), "Registry should store API instance after setApi");

        // Shutdown should attempt to unregister and null the api reference
        registry.shutdown();
        assertNull(registry.api(), "Registry should clear API reference after shutdown");
    }
}
