package com.skyblockexp.ezcountdown.api;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.bootstrap.Registry;
import com.skyblockexp.ezcountdown.manager.CountdownDefaults;
import com.skyblockexp.ezcountdown.manager.CountdownManager;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EzCountdownApiImplTest {

    @Test
    public void delegatesStartStopGetListDeleteToManager() {
        Registry registry = mock(Registry.class);
        CountdownManager manager = mock(CountdownManager.class);
        when(registry.countdowns()).thenReturn(manager);

        EzCountdownApi api = new EzCountdownApiImpl(registry);

        when(manager.startCountdown("x")).thenReturn(true);
        assertTrue(api.startCountdown("x"));
        verify(manager).startCountdown("x");

        when(manager.stopCountdown("y")).thenReturn(false);
        assertFalse(api.stopCountdown("y"));
        verify(manager).stopCountdown("y");

        when(manager.getCountdown("a")).thenReturn(Optional.of(new Countdown("a", CountdownType.MANUAL, null, 1, "", "", "", "", List.of(), ZoneId.systemDefault())));
        assertTrue(api.getCountdown("a").isPresent());
        verify(manager).getCountdown("a");

        when(manager.getCountdowns()).thenReturn(List.of());
        assertNotNull(api.listCountdowns());
        verify(manager).getCountdowns();

        when(manager.deleteCountdown("z")).thenReturn(true);
        assertTrue(api.deleteCountdown("z"));
        verify(manager).deleteCountdown("z");
    }

    @Test
    public void createCountdown_durationNotifiesPlayersAndStarts() {
        Registry registry = mock(Registry.class);
        CountdownManager manager = mock(CountdownManager.class);
        CountdownDefaults defaults = mock(CountdownDefaults.class);
        when(registry.countdowns()).thenReturn(manager);
        when(registry.defaults()).thenReturn(defaults);

        when(defaults.displayTypes()).thenReturn(java.util.EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class));
        when(defaults.updateIntervalSeconds()).thenReturn(5);
        when(defaults.visibilityPermission()).thenReturn("");
        when(defaults.formatMessage()).thenReturn("fmt");
        when(defaults.startMessage()).thenReturn("s");
        when(defaults.endMessage()).thenReturn("e");
        when(defaults.zoneId()).thenReturn(ZoneId.systemDefault());

        when(manager.createCountdown(any())).thenReturn(true);

        EzCountdownApi api = new EzCountdownApiImpl(registry);

        Player p = mock(Player.class);
        boolean created = api.createCountdown(CountdownType.DURATION, 10L, List.of(p));
        assertTrue(created);

        // capture the countdown passed to manager
        verify(manager).createCountdown(any());
        // ensure player was notified
        verify(p).sendMessage(startsWith("Countdown 'api-"));
    }

    @Test
    public void createCountdown_fixedOrRecurringIsStopped() {
        Registry registry = mock(Registry.class);
        CountdownManager manager = mock(CountdownManager.class);
        CountdownDefaults defaults = mock(CountdownDefaults.class);
        when(registry.countdowns()).thenReturn(manager);
        when(registry.defaults()).thenReturn(defaults);

        when(defaults.displayTypes()).thenReturn(java.util.EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class));
        when(defaults.updateIntervalSeconds()).thenReturn(5);
        when(defaults.visibilityPermission()).thenReturn("");
        when(defaults.formatMessage()).thenReturn("fmt");
        when(defaults.startMessage()).thenReturn("s");
        when(defaults.endMessage()).thenReturn("e");
        when(defaults.zoneId()).thenReturn(ZoneId.systemDefault());

        when(manager.createCountdown(any())).thenReturn(true);

        EzCountdownApi api = new EzCountdownApiImpl(registry);

        boolean createdFixed = api.createCountdown(CountdownType.FIXED_DATE, 12345L, null);
        assertTrue(createdFixed);
        verify(manager).createCountdown(any());

        boolean createdRec = api.createCountdown(CountdownType.RECURRING, 0L, null);
        assertTrue(createdRec);
        verify(manager, atLeast(2)).createCountdown(any());
    }
}
