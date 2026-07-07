package com.skyblockexp.ezcountdown.gui;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.display.DisplayType;
import com.skyblockexp.ezcountdown.util.MaterialCompat;
import org.bukkit.inventory.Inventory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.time.ZoneId;
import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DisplayEditorUnitTest {

    private ServerMock server;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void openDisplayEditorMarksActiveTypesWithEnabledMaterial() {
        Countdown countdown = new Countdown(
                "unit-display",
                CountdownType.MANUAL,
                EnumSet.of(DisplayType.ACTION_BAR, DisplayType.CHAT),
                1,
                null,
                "{formatted}",
                "start",
                "end",
                List.of(),
                ZoneId.systemDefault()
        );

        DisplayEditor editor = new DisplayEditor(null, null);
        var player = server.addPlayer("display-unit-player");

        editor.openDisplayEditor(player, countdown);

        Inventory top = player.getOpenInventory().getTopInventory();
        assertNotNull(top);
        assertEquals(9, top.getSize());
        assertEquals(DisplayEditor.getPrefix() + "unit-display", player.getOpenInventory().getTitle());

        DisplayType[] values = DisplayType.values();
        for (int i = 0; i < values.length; i++) {
            var item = top.getItem(i);
            assertNotNull(item);
            assertEquals(values[i].name(), org.bukkit.ChatColor.stripColor(item.getItemMeta().getDisplayName()));
            if (countdown.getDisplayTypes().contains(values[i])) {
                assertEquals(MaterialCompat.resolve("LIME_CONCRETE", "LIME_WOOL", "WOOL"), item.getType());
            } else {
                assertEquals(MaterialCompat.resolve("GRAY_CONCRETE", "GRAY_WOOL", "WOOL"), item.getType());
            }
        }
    }
}
