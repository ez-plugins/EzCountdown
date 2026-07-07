package com.skyblockexp.ezcountdown.gui;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.display.DisplayType;
import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;
import com.skyblockexp.ezcountdown.util.MaterialCompat;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DisplayEditorFeatureTest extends MockBukkitTestBase {

    @Test
    void openDisplayEditorUsesEnabledAndDisabledMaterials() {
        EnumSet<DisplayType> active = EnumSet.of(DisplayType.CHAT, DisplayType.BOSS_BAR);
        Countdown countdown = new Countdown(
                "display-edit",
                CountdownType.MANUAL,
                active,
                1,
                null,
                "{formatted}",
                "start",
                "end",
                List.of(),
                ZoneId.systemDefault()
        );

        DisplayEditor editor = new DisplayEditor(manager, registry.messages());
        var player = addPlayer("display-editor-player");

        editor.openDisplayEditor(player, countdown);

        Inventory top = player.getOpenInventory().getTopInventory();
        assertNotNull(top);
        assertEquals(9, top.getSize());
        assertEquals(DisplayEditor.getPrefix() + "display-edit", player.getOpenInventory().getTitle());

        DisplayType[] values = DisplayType.values();
        for (int i = 0; i < values.length; i++) {
            DisplayType type = values[i];
            ItemStack item = top.getItem(i);
            assertNotNull(item, "Each display type should have an editor slot");
            assertNotNull(item.getItemMeta());
            assertEquals(ChatColor.AQUA + type.name(), item.getItemMeta().getDisplayName());

            if (active.contains(type)) {
                assertEquals(MaterialCompat.resolve("LIME_CONCRETE", "LIME_WOOL", "WOOL"), item.getType());
            } else {
                assertEquals(MaterialCompat.resolve("GRAY_CONCRETE", "GRAY_WOOL", "WOOL"), item.getType());
            }
        }
    }
}
