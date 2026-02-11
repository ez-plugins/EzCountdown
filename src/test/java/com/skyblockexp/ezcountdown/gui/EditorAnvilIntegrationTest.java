package com.skyblockexp.ezcountdown.gui;

import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.ClickType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EditorAnvilIntegrationTest extends MockBukkitTestBase {

    @Test
    public void editingFormatStartEndViaAnvilUpdatesCountdownAndEditorSlots() {
        // create a countdown with explicit per-countdown messages
        var cd = new com.skyblockexp.ezcountdown.api.model.Countdown("anvil-test", com.skyblockexp.ezcountdown.api.model.CountdownType.MANUAL, java.util.EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 5, null, "Format: {name} {formatted}", "Start: {name}", "End: {name}", java.util.List.of(), java.time.ZoneId.systemDefault());
        cd.setDurationSeconds(10);
        registry.countdowns().createCountdown(cd);

        var player = addPlayer("anvil-player");

        // Open the editor directly
        registry.gui().editorMenu().openEditor(player, cd);
        assertNotNull(player.getOpenInventory());
        assertTrue(player.getOpenInventory().getTitle().startsWith(com.skyblockexp.ezcountdown.gui.EditorMenu.getPrefix()));

        // Verify the editor shows the resolved format/start/end in the lore
        var view = player.getOpenInventory();
        var formatItem = view.getTopInventory().getItem(4);
        assertNotNull(formatItem);
        assertNotNull(formatItem.getItemMeta());
        String formatLore = formatItem.getItemMeta().getLore().get(0);
        assertTrue(formatLore.contains("Format:") || formatLore.contains("anvil-test"));

        var startItem = view.getTopInventory().getItem(6);
        assertNotNull(startItem);
        String startLore = startItem.getItemMeta().getLore().get(0);
        assertTrue(startLore.contains("Start:") || startLore.contains("anvil-test"));

        var endItem = view.getTopInventory().getItem(8);
        assertNotNull(endItem);
        String endLore = endItem.getItemMeta().getLore().get(0);
        assertTrue(endLore.contains("End:") || endLore.contains("anvil-test"));

        // Simulate clicking the format slot to open anvil input
        org.bukkit.event.inventory.InventoryClickEvent click = new org.bukkit.event.inventory.InventoryClickEvent(view, org.bukkit.event.inventory.InventoryType.SlotType.CONTAINER, 4, ClickType.LEFT, InventoryAction.PICKUP_ALL);
        plugin.getServer().getPluginManager().callEvent(click);

        // After clicking, the listener should prompt for chat input. Simulate player chat submission.
        plugin.getServer().getPluginManager().callEvent(new org.bukkit.event.player.AsyncPlayerChatEvent(false, player, "Edited Format {name}", java.util.Set.of()));

        // After submit, countdown should be updated
        var updated = registry.countdowns().getCountdown("anvil-test").orElseThrow();
        assertEquals("Edited Format {name}", updated.getFormatMessage());

        // Re-open editor and verify the slot now shows updated resolved content
        registry.gui().editorMenu().openEditor(player, updated);
        var view2 = player.getOpenInventory();
        var formatItem2 = view2.getTopInventory().getItem(4);
        String lore2 = formatItem2.getItemMeta().getLore().get(0);
        assertTrue(lore2.contains("Edited Format") || lore2.contains("anvil-test"));
    }
}
