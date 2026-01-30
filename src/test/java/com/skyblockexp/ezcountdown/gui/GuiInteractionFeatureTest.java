package com.skyblockexp.ezcountdown.gui;

import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GuiInteractionFeatureTest extends MockBukkitTestBase {

    @Test
    public void guiManagerIsAvailable_and_mainGuiCanBeOpened() {
        assertNotNull(plugin, "Plugin should be loaded");
        assertNotNull(registry, "Registry should be available after startup");

        assertNotNull(registry.gui(), "GuiManager should be registered in registry");
        // create a countdown and register it with the manager so the main GUI has an item
        var cd = new com.skyblockexp.ezcountdown.api.model.Countdown("gui-test", com.skyblockexp.ezcountdown.api.model.CountdownType.MANUAL, java.util.EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 5, null, "{formatted}", null, null, java.util.List.of(), java.time.ZoneId.systemDefault());
        cd.setDurationSeconds(10);
        registry.countdowns().createCountdown(cd);

        // add a player and open the main GUI
        var player = addPlayer("gui-player");
        registry.gui().openMain(player);

        // ensure the player's open inventory is the main GUI
        assertNotNull(player.getOpenInventory(), "Player should have an open inventory");
        assertEquals(com.skyblockexp.ezcountdown.gui.MainGui.getTitle(), player.getOpenInventory().getTitle());

        // Simulate a left-click on slot 0 to open the editor
        org.bukkit.inventory.InventoryView view = player.getOpenInventory();
        org.bukkit.event.inventory.InventoryClickEvent click = new org.bukkit.event.inventory.InventoryClickEvent(view, org.bukkit.event.inventory.InventoryType.SlotType.CONTAINER, 0, org.bukkit.event.inventory.ClickType.LEFT, org.bukkit.event.inventory.InventoryAction.PICKUP_ALL);
        plugin.getServer().getPluginManager().callEvent(click);

        // After handling, the player's open inventory title should start with the editor prefix
        assertNotNull(player.getOpenInventory(), "Player should still have an open inventory after click");
        assertTrue(player.getOpenInventory().getTitle().startsWith(com.skyblockexp.ezcountdown.gui.EditorMenu.getPrefix()));
    }
}
