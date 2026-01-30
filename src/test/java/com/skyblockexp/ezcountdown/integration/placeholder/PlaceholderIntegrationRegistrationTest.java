package com.skyblockexp.ezcountdown.integration.placeholder;

import com.skyblockexp.ezcountdown.bootstrap.Registry;
import com.skyblockexp.ezcountdown.integration.PlaceholderIntegration;
import com.skyblockexp.ezcountdown.manager.CountdownManager;
import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import org.bukkit.plugin.Plugin;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class PlaceholderIntegrationRegistrationTest {

    @Test
    public void registerIfPresent_registersExpansionWhenPlaceholderPluginExists() {
        // Arrange: mock a Registry with plugin/server/pluginManager that reports PlaceholderAPI present
        Registry registry = mock(Registry.class);
        com.skyblockexp.ezcountdown.EzCountdownPlugin plugin = mock(com.skyblockexp.ezcountdown.EzCountdownPlugin.class);
        Server server = mock(Server.class);
        PluginManager pm = mock(PluginManager.class);
        when(plugin.getServer()).thenReturn(server);
        when(server.getPluginManager()).thenReturn(pm);
        when(pm.getPlugin("PlaceholderAPI")).thenReturn(mock(Plugin.class));

        // provide a real-ish CountdownManager via mock that returns a countdown for placeholder lookup
        CountdownManager manager = mock(CountdownManager.class);
        when(registry.countdowns()).thenReturn(manager);
        when(registry.plugin()).thenReturn(plugin);

        // create and register a countdown in the mocked manager
        Countdown cd = new Countdown("ph-test", CountdownType.DURATION, java.util.EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "{formatted}", null, null, java.util.List.of(), ZoneId.systemDefault());
        cd.setRunning(true);
        cd.setTargetInstant(Instant.now().plusSeconds(90));
        when(manager.getCountdown("ph-test")).thenReturn(Optional.of(cd));

        // Act: attempt to register expansion
        var expansion = PlaceholderIntegration.registerIfPresent(registry);

        // Assert: expansion object created and can resolve placeholders
        assertNotNull(expansion, "Expansion should be created when PlaceholderAPI plugin is present");
        String formatted = expansion.onPlaceholderRequest(null, "ph-test_formatted");
        assertNotNull(formatted);
        assertFalse(formatted.isBlank());

        // cleanup: attempt to unregister but ignore any failures (PlaceholderAPI may not be fully initialized in unit tests)
        try { expansion.unregister(); } catch (Throwable ignored) {}
    }
}
