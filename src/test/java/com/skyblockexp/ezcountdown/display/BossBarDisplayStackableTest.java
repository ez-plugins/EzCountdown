package com.skyblockexp.ezcountdown.display;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.display.bossbar.BossBarDisplay;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class BossBarDisplayStackableTest {

    @BeforeEach
    public void setup() throws Exception {
        org.bukkit.Server bukkitServer = mock(org.bukkit.Server.class);
        when(bukkitServer.getLogger()).thenReturn(java.util.logging.Logger.getLogger("test"));
        when(bukkitServer.getPluginManager()).thenReturn(mock(org.bukkit.plugin.PluginManager.class));
        when(bukkitServer.getConsoleSender()).thenReturn(mock(org.bukkit.command.ConsoleCommandSender.class));

        java.lang.reflect.Field serverField = Bukkit.class.getDeclaredField("server");
        serverField.setAccessible(true);
        serverField.set(null, bukkitServer);
    }

    @Test
    public void displayMultiple_skipsBossBarWhenBossBarNotConfiguredAsDisplayType() throws Exception {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        when(player.hasPermission(anyString())).thenReturn(true);

        Set<Player> players = new HashSet<>();
        players.add(player);
        @SuppressWarnings("unchecked")
        java.util.Collection<Player> coll = (java.util.Collection<Player>) (java.util.Collection<?>) players;
        when(Bukkit.getOnlinePlayers()).thenAnswer(invocation -> coll);

        // Countdown configured with CHAT only — BOSS_BAR not enabled
        Countdown c = new Countdown("no-boss", CountdownType.MANUAL,
                EnumSet.of(DisplayType.CHAT), 1, null,
                "{formatted}", "s", "e", Collections.emptyList(), ZoneId.systemDefault());

        Map<Countdown, String> messages = new HashMap<>();
        messages.put(c, "msg");
        Map<Countdown, Long> remaining = new HashMap<>();
        remaining.put(c, 5L);

        BossBarDisplay display = new BossBarDisplay();
        display.displayMultiple(List.of(c), messages, remaining);

        // No BossBar should have been created or stored
        Field bossBarsField = BossBarDisplay.class.getDeclaredField("bossBars");
        bossBarsField.setAccessible(true);
        Map<?, ?> bossBars = (Map<?, ?>) bossBarsField.get(display);
        assertTrue(bossBars.isEmpty(),
                "No BossBar should be created when BOSS_BAR is not a configured display type");
    }
}
