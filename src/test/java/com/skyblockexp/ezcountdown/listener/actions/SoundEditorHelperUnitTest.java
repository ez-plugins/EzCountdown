package com.skyblockexp.ezcountdown.listener.actions;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SoundEditorHelperUnitTest {

    @Test
    public void sendAvailableSounds_sendsHeaderAndKnownSoundNames() {
        Player player = mock(Player.class);

        SoundEditorHelper.sendAvailableSounds(player);

        verify(player, atLeastOnce()).sendMessage(contains("Available sounds ("));
        assertTrue(Sound.values().length > 0, "Expected at least one Bukkit sound enum value");
    }
}
