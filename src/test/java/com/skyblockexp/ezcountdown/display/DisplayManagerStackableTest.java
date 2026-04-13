package com.skyblockexp.ezcountdown.display;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.display.DisplayType;
import com.skyblockexp.ezcountdown.manager.DisplayManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class DisplayManagerStackableTest {

    private DisplayManager displayManager;

    @BeforeEach
    public void setup() {
        com.skyblockexp.ezcountdown.config.ConfigService cs = mock(com.skyblockexp.ezcountdown.config.ConfigService.class);
        when(cs.loadDisplayOverrides()).thenReturn(Collections.emptyMap());
        // ensure Bukkit.server is set to a mock to avoid NPEs when constructing DisplayManager
        org.bukkit.Server bukkitServer = mock(org.bukkit.Server.class);
        org.bukkit.plugin.PluginManager pm = mock(org.bukkit.plugin.PluginManager.class);
        org.bukkit.command.ConsoleCommandSender console = mock(org.bukkit.command.ConsoleCommandSender.class);
        when(bukkitServer.getPluginManager()).thenReturn(pm);
        when(bukkitServer.getConsoleSender()).thenReturn(console);
        when(bukkitServer.getLogger()).thenReturn(java.util.logging.Logger.getLogger("test"));
        try {
            java.lang.reflect.Field serverField = org.bukkit.Bukkit.class.getDeclaredField("server");
            serverField.setAccessible(true);
            serverField.set(null, bukkitServer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        displayManager = new DisplayManager(cs);
    }

    @Test
    public void stackableHandlerReceivesBatchCall_and_nonStackableFallsBackPerCountdown() throws Exception {
        // Prepare mock handlers
        StackableDisplay stackable = mock(StackableDisplay.class);
        DisplayHandler nonStackable = mock(DisplayHandler.class);

        // Inject into DisplayManager.handlers via reflection
        java.lang.reflect.Field f = DisplayManager.class.getDeclaredField("handlers");
        f.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<DisplayType, DisplayHandler> handlers = (Map<DisplayType, DisplayHandler>) f.get(displayManager);
        handlers.clear();
        handlers.put(DisplayType.BOSS_BAR, stackable);
        handlers.put(DisplayType.CHAT, nonStackable);

        // Two countdowns that both request BOSS_BAR and CHAT
        Countdown c1 = new Countdown("one", com.skyblockexp.ezcountdown.api.model.CountdownType.MANUAL, EnumSet.of(DisplayType.BOSS_BAR, DisplayType.CHAT), 1, null, "{formatted}", "s", "e", Collections.emptyList(), ZoneId.systemDefault());
        Countdown c2 = new Countdown("two", com.skyblockexp.ezcountdown.api.model.CountdownType.MANUAL, EnumSet.of(DisplayType.BOSS_BAR, DisplayType.CHAT), 1, null, "{formatted}", "s", "e", Collections.emptyList(), ZoneId.systemDefault());

        List<Countdown> list = List.of(c1, c2);
        Map<Countdown, String> messages = new HashMap<>();
        messages.put(c1, "m1");
        messages.put(c2, "m2");
        Map<Countdown, Long> remaining = new HashMap<>();
        remaining.put(c1, 5L);
        remaining.put(c2, 10L);

        // Invoke batch display
        displayManager.displayAll(list, messages, remaining);

        // Stackable should be called once with both countdowns
        verify(stackable, times(1)).displayMultiple(eq(list), eq(messages), eq(remaining));

        // Non-stackable should be called per-countdown via displayBatched
        verify(nonStackable, times(2)).displayBatched(any(Countdown.class), anyString(), anyLong(), any(MessageBatch.class));
    }

    @Test
    public void nonStackableDisplayCalledWhenUsingSingleDisplay() throws Exception {
        DisplayHandler nonStackable = mock(DisplayHandler.class);
        java.lang.reflect.Field f = DisplayManager.class.getDeclaredField("handlers");
        f.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<DisplayType, DisplayHandler> handlers = (Map<DisplayType, DisplayHandler>) f.get(displayManager);
        handlers.clear();
        handlers.put(DisplayType.CHAT, nonStackable);

        Countdown c = new Countdown("solo", com.skyblockexp.ezcountdown.api.model.CountdownType.MANUAL, EnumSet.of(DisplayType.CHAT), 1, null, "{formatted}", "s", "e", Collections.emptyList(), ZoneId.systemDefault());

        displayManager.display(c, "hello", 3L);

        verify(nonStackable, times(1)).displayBatched(eq(c), eq("hello"), eq(3L), any(MessageBatch.class));
    }
}
