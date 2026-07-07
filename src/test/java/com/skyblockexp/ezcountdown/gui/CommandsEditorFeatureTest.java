package com.skyblockexp.ezcountdown.gui;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.test.MockBukkitTestBase;
import com.skyblockexp.ezcountdown.util.MaterialCompat;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CommandsEditorFeatureTest extends MockBukkitTestBase {

    @Test
    void openCommandsEditorPopulatesCommandItemsAndAddButton() {
        Countdown countdown = new Countdown(
                "cmd-edit",
                CountdownType.MANUAL,
                EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class),
                1,
                null,
                "{formatted}",
                "start",
                "end",
                List.of("say first", "say second"),
                ZoneId.systemDefault()
        );

        CommandsEditor editor = new CommandsEditor(manager, registry.gui().chatInputListener(), registry.messages(), registry);
        var player = addPlayer("cmd-editor-player");

        editor.openCommandsEditor(player, countdown);

        Inventory top = player.getOpenInventory().getTopInventory();
        assertNotNull(top);
        assertEquals(9, top.getSize(), "Two command entries should still use one inventory row");
        assertEquals(CommandsEditor.getPrefix() + "cmd-edit", player.getOpenInventory().getTitle());

        ItemStack first = top.getItem(0);
        assertNotNull(first);
        assertEquals(Material.PAPER, first.getType());
        assertNotNull(first.getItemMeta());
        assertEquals(ChatColor.WHITE + "say first", first.getItemMeta().getDisplayName());

        ItemStack second = top.getItem(1);
        assertNotNull(second);
        assertEquals(Material.PAPER, second.getType());
        assertNotNull(second.getItemMeta());
        assertEquals(ChatColor.WHITE + "say second", second.getItemMeta().getDisplayName());

        ItemStack addButton = top.getItem(8);
        assertNotNull(addButton);
        assertEquals(MaterialCompat.resolve("GREEN_WOOL", "LIME_WOOL", "WOOL", "PAPER"), addButton.getType());
        assertNotNull(addButton.getItemMeta());
        assertEquals(ChatColor.GREEN + "Add Command", addButton.getItemMeta().getDisplayName());
    }

    @Test
    void openCommandsEditorExpandsInventoryForManyCommands() {
        List<String> commands = java.util.stream.IntStream.range(0, 14)
                .mapToObj(i -> "say c" + i)
                .toList();

        Countdown countdown = new Countdown(
                "cmd-many",
                CountdownType.MANUAL,
                EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class),
                1,
                null,
                "{formatted}",
                "start",
                "end",
                commands,
                ZoneId.systemDefault()
        );

        CommandsEditor editor = new CommandsEditor(manager, registry.gui().chatInputListener(), registry.messages(), registry);
        var player = addPlayer("cmd-many-player");

        editor.openCommandsEditor(player, countdown);

        Inventory top = player.getOpenInventory().getTopInventory();
        assertNotNull(top);
        assertEquals(18, top.getSize(), "14 commands + add button should require two rows");

        ItemStack lastCommand = top.getItem(13);
        assertNotNull(lastCommand);
        assertEquals(Material.PAPER, lastCommand.getType());

        ItemStack addButton = top.getItem(17);
        assertNotNull(addButton);
        assertEquals(ChatColor.GREEN + "Add Command", addButton.getItemMeta().getDisplayName());
    }
}
