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

class CommandsEditorUnitTest {

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
    void openCommandsEditorBuildsInventoryAndAddButton() {
        Countdown countdown = new Countdown(
                "unit-cmd",
                CountdownType.MANUAL,
                EnumSet.noneOf(DisplayType.class),
                1,
                null,
                "{formatted}",
                "start",
                "end",
                List.of("say hello"),
                ZoneId.systemDefault()
        );

        CommandsEditor editor = new CommandsEditor(null, null, null, null);
        var player = server.addPlayer("editor-unit-player");

        editor.openCommandsEditor(player, countdown);

        Inventory top = player.getOpenInventory().getTopInventory();
        assertNotNull(top);
        assertEquals(9, top.getSize());
        assertEquals(CommandsEditor.getPrefix() + "unit-cmd", player.getOpenInventory().getTitle());
        assertEquals("say hello", org.bukkit.ChatColor.stripColor(top.getItem(0).getItemMeta().getDisplayName()));

        var add = top.getItem(8);
        assertNotNull(add);
        assertEquals(MaterialCompat.resolve("GREEN_WOOL", "LIME_WOOL", "WOOL", "PAPER"), add.getType());
        assertEquals("Add Command", org.bukkit.ChatColor.stripColor(add.getItemMeta().getDisplayName()));
    }
}
