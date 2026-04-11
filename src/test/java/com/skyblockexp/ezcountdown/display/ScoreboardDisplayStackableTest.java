package com.skyblockexp.ezcountdown.display;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.display.scoreboard.ScoreboardDisplay;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.util.*;

import static org.mockito.Mockito.*;

public class ScoreboardDisplayStackableTest {

    @BeforeEach
    public void setup() throws Exception {
        // Mock Bukkit.server to avoid NPEs
        org.bukkit.Server bukkitServer = mock(org.bukkit.Server.class);
        when(bukkitServer.getLogger()).thenReturn(java.util.logging.Logger.getLogger("test"));
        when(bukkitServer.getPluginManager()).thenReturn(mock(org.bukkit.plugin.PluginManager.class));
        when(bukkitServer.getConsoleSender()).thenReturn(mock(org.bukkit.command.ConsoleCommandSender.class));

        java.lang.reflect.Field serverField = org.bukkit.Bukkit.class.getDeclaredField("server");
        serverField.setAccessible(true);
        serverField.set(null, bukkitServer);
    }

    @Test
    public void displayMultiple_updatesObjectivePerPlayer() {
        // Mocks
        ScoreboardManager manager = mock(ScoreboardManager.class);
        Scoreboard mainScore = mock(Scoreboard.class);
        Scoreboard newScore = mock(Scoreboard.class);
        Objective objective = mock(Objective.class);

        when(Bukkit.getScoreboardManager()).thenReturn(manager);
        when(manager.getMainScoreboard()).thenReturn(mainScore);
        when(manager.getNewScoreboard()).thenReturn(newScore);

        Player player = mock(Player.class);
        UUID uid = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(uid);
        when(player.getScoreboard()).thenReturn(mainScore);
        when(player.hasPermission(anyString())).thenReturn(true);

        Set<Player> players = new HashSet<>();
        players.add(player);
        @SuppressWarnings("unchecked")
        java.util.Collection<Player> coll = (java.util.Collection<Player>) (java.util.Collection<?>) players;
        when(Bukkit.getOnlinePlayers()).thenAnswer(invocation -> coll);

        when(newScore.getObjective(anyString())).thenReturn(null);
        when(newScore.registerNewObjective(anyString(), anyString(), anyString())).thenReturn(objective);
        when(newScore.getEntries()).thenReturn(Collections.emptySet());
            org.bukkit.scoreboard.Score mockScore = mock(org.bukkit.scoreboard.Score.class);
            when(objective.getScore(anyString())).thenReturn(mockScore);

        // Create two countdowns
        Countdown c1 = new Countdown("one", com.skyblockexp.ezcountdown.api.model.CountdownType.MANUAL, EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "{formatted}", "s", "e", Collections.emptyList(), ZoneId.systemDefault());
        Countdown c2 = new Countdown("two", com.skyblockexp.ezcountdown.api.model.CountdownType.MANUAL, EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "{formatted}", "s", "e", Collections.emptyList(), ZoneId.systemDefault());

        List<Countdown> list = List.of(c1, c2);
        Map<Countdown, String> messages = new HashMap<>();
        messages.put(c1, "m1");
        messages.put(c2, "m2");
        Map<Countdown, Long> remaining = new HashMap<>();
        remaining.put(c1, 5L);
        remaining.put(c2, 10L);

        ScoreboardDisplay sd = new ScoreboardDisplay();
        sd.displayMultiple(list, messages, remaining);

        // verify that objective was created and scores were updated
        verify(newScore, times(1)).registerNewObjective(anyString(), eq("dummy"), anyString());
        verify(objective, times(1)).setDisplaySlot(org.bukkit.scoreboard.DisplaySlot.SIDEBAR);
        verify(objective, times(1)).getScore("m1");
        verify(objective, times(1)).getScore("m2");
    }
}
