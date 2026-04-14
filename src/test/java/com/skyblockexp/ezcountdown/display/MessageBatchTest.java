package com.skyblockexp.ezcountdown.display;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.util.Collections;
import java.util.EnumSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link MessageBatch}: deduplication and flush behaviour.
 * Uses pure Mockito mocks — no MockBukkit requirement.
 */
public class MessageBatchTest {

    private Player player;
    private Countdown countdown;

    @BeforeEach
    public void setup() {
        player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());

        countdown = new Countdown("test", CountdownType.MANUAL,
                EnumSet.of(DisplayType.CHAT), 1, null, "{formatted}", "s", "e",
                Collections.emptyList(), ZoneId.systemDefault());
    }

    @Test
    public void addSamePairTwice_flushSendsOneMessage() {
        MessageBatch batch = new MessageBatch();
        batch.add(player, countdown, "hello");
        batch.add(player, countdown, "hello");

        assertEquals(1, batch.size());
        batch.flush();

        verify(player, times(1)).sendMessage("hello");
    }

    @Test
    public void addDifferentCountdowns_flushSendsBoth() {
        Countdown other = new Countdown("other", CountdownType.MANUAL,
                EnumSet.of(DisplayType.CHAT), 1, null, "{formatted}", "s", "e",
                Collections.emptyList(), ZoneId.systemDefault());

        MessageBatch batch = new MessageBatch();
        batch.add(player, countdown, "msg-a");
        batch.add(player, other, "msg-b");

        assertEquals(2, batch.size());
        batch.flush();

        verify(player, times(1)).sendMessage("msg-a");
        verify(player, times(1)).sendMessage("msg-b");
    }

    @Test
    public void addSameCountdownDifferentPlayers_flushSendsBoth() {
        Player second = mock(Player.class);
        when(second.getUniqueId()).thenReturn(UUID.randomUUID());

        MessageBatch batch = new MessageBatch();
        batch.add(player, countdown, "hi");
        batch.add(second, countdown, "hi");

        assertEquals(2, batch.size());
        batch.flush();

        verify(player, times(1)).sendMessage("hi");
        verify(second, times(1)).sendMessage("hi");
    }

    @Test
    public void firstWriteWins_subsequentAddIgnored() {
        MessageBatch batch = new MessageBatch();
        batch.add(player, countdown, "first");
        batch.add(player, countdown, "second");  // same player + countdown — ignored

        batch.flush();

        verify(player, times(1)).sendMessage("first");
        verify(player, never()).sendMessage("second");
    }

    @Test
    public void flushClearsBatch() {
        MessageBatch batch = new MessageBatch();
        batch.add(player, countdown, "msg");
        batch.flush();
        assertEquals(0, batch.size());

        // Second flush on empty batch should not re-send
        batch.flush();
        verify(player, times(1)).sendMessage("msg");
    }
}
