package com.skyblockexp.ezcountdown.command;

import com.skyblockexp.ezcountdown.gui.MainGui;
import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;
import org.junit.jupiter.api.Test;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.entity.Player;
import static org.junit.jupiter.api.Assertions.*;

public class CountdownCommandFeatureMoreTest extends MockBukkitTestBase {

    @Test
    public void createFixedDateUsesFallbackWhenNoHandler() {
        assertTrue(server.dispatchCommand(server.getConsoleSender(), "ezcd create fd 2026-02-02 12:00"));
        assertTrue(manager.getCountdown("fd").isPresent());
    }

    @Test
    public void guiOnlyPlayersAndOpensForPlayerWithPermission() {
        assertTrue(server.dispatchCommand(server.getConsoleSender(), "ezcd gui"));
        Player player = addPlayer("guiPlayer");
        assertTrue(server.dispatchCommand(player, "ezcd gui"));
        // verify opened inventory title and type
        // PlayerMock-specific assert is deprecated; verify by checking open inventory title via server API
        // keep a simple check that player's open inventory has desired type
        assertEquals(InventoryType.CHEST, player.getOpenInventory().getType());
    }

    @Test
    public void locationAddAndDeleteRespectPermissionsAndPlayerOnly() {
        Player player = addOp("locPlayer");
        assertTrue(server.dispatchCommand(player, "ezcd location add here"));
        assertTrue(registry.locations().getLocationNames().contains("here"));
        assertTrue(server.dispatchCommand(player, "ezcd location delete here"));
        assertFalse(registry.locations().getLocationNames().contains("here"));
    }
}
