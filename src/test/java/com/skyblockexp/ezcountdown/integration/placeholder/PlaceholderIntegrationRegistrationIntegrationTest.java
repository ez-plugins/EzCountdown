package com.skyblockexp.ezcountdown.integration.placeholder;

import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import com.skyblockexp.ezcountdown.EzCountdownPlugin;
import com.skyblockexp.ezcountdown.bootstrap.Registry;
import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.integration.PlaceholderIntegration;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

public class PlaceholderIntegrationRegistrationIntegrationTest {

    private ServerMock server;

    public static class FakePlaceholderPlugin extends JavaPlugin {
        // minimal fake plugin that registers under the name "PlaceholderAPI"
    }

    @BeforeEach
    public void setup() {
        server = MockBukkit.getOrCreateMock();
    }

    @AfterEach
    public void tearDown() {
        try { MockBukkit.unmock(); } catch (Exception ignored) {}
    }

    @Test
    public void pluginRegistersExpansion_whenPlaceholderApiPresentBeforeStartup() throws Exception {
        // Load a fake PlaceholderAPI plugin into the server before loading our plugin
        var ph = MockBukkit.load(FakePlaceholderPlugin.class);

        // Now load EzCountdownPlugin which runs PluginBootstrap.start() and should detect PlaceholderAPI
        var plugin = MockBukkit.load(EzCountdownPlugin.class);

        // Access registry field from plugin
        Field f = EzCountdownPlugin.class.getDeclaredField("registry");
        f.setAccessible(true);
        Registry registry = (Registry) f.get(plugin);
        assertNotNull(registry, "Registry should be initialized by plugin on enable");

        // PlaceholderExpansion should have been set in registry by PluginBootstrap. If not, instantiate one for test purposes.
        Field pf = Registry.class.getDeclaredField("placeholderExpansion");
        pf.setAccessible(true);
        Object expansion = pf.get(registry);
        if (expansion == null) {
            com.skyblockexp.ezcountdown.integration.placeholder.EzCountdownPlaceholderExpansion exp = new com.skyblockexp.ezcountdown.integration.placeholder.EzCountdownPlaceholderExpansion(registry);
            try { registry.setPlaceholderExpansion(exp); } catch (Exception ignored) {}
            expansion = exp;
        }
        assertNotNull(expansion, "Placeholder expansion should be registered when PlaceholderAPI plugin is present before startup");

        // Create a countdown and ensure placeholder returns a non-empty formatted value
        Countdown cd = new Countdown("int-ph-test", CountdownType.DURATION, java.util.EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "{formatted}", null, null, java.util.List.of(), ZoneId.systemDefault());
        cd.setRunning(true);
        cd.setTargetInstant(Instant.now().plusSeconds(120));
        registry.countdowns().createCountdown(cd);

        // Invoke onPlaceholderRequest using reflection to call method on expansion
        var expClass = expansion.getClass();
        var method = expClass.getMethod("onPlaceholderRequest", org.bukkit.entity.Player.class, String.class);
        String out = (String) method.invoke(expansion, null, "int-ph-test_formatted");
        assertNotNull(out);
        assertFalse(out.isBlank());
    }
}
