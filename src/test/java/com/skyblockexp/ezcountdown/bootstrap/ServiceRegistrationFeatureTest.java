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

        // TODO: Assert that EzCountdownApi is registered with the Bukkit ServicesManager
        // TODO: Disable plugin and assert unregister behavior
    }
}
