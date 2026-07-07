package com.skyblockexp.ezcountdown.listener.actions;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.command.CountdownPermissions;
import com.skyblockexp.ezcountdown.gui.CommandsEditor;
import com.skyblockexp.ezcountdown.listener.ChatInputListener;
import com.skyblockexp.ezcountdown.manager.CountdownManager;
import com.skyblockexp.ezcountdown.manager.MessageManager;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CommandsEditorActionsTest {

    @Test
    public void handleCoversGuardBranches() {
        CountdownManager manager = mock(CountdownManager.class);
        MessageManager messages = mock(MessageManager.class);
        ChatInputListener chat = mock(ChatInputListener.class);
        CommandsEditor editor = mock(CommandsEditor.class);
        com.skyblockexp.ezcountdown.bootstrap.Registry registry = mock(com.skyblockexp.ezcountdown.bootstrap.Registry.class);

        when(registry.permissions()).thenReturn(new CountdownPermissions("base", "create.perm", "start", "stop", "delete", "list", "info", "reload"));
        CommandsEditorActions action = new CommandsEditorActions(manager, messages, chat, editor, registry);

        org.bukkit.entity.Player player = mock(org.bukkit.entity.Player.class);
        Countdown cd = new Countdown("cd", CountdownType.MANUAL, EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "fmt", "s", "e", List.of("one", "two"), ZoneId.systemDefault());
        Inventory inv = mock(Inventory.class);
        when(inv.getSize()).thenReturn(9);

        InventoryClickEvent clickedNull = mock(InventoryClickEvent.class);
        when(clickedNull.getRawSlot()).thenReturn(0);
        when(clickedNull.getInventory()).thenReturn(inv);
        when(clickedNull.getCurrentItem()).thenReturn(null);
        assertFalse(action.handle(clickedNull, player, "cd", Optional.of(cd)).isHandled());

        ItemStack air = mock(ItemStack.class);
        when(air.getType()).thenReturn(org.bukkit.Material.AIR);
        InventoryClickEvent clickedAir = mock(InventoryClickEvent.class);
        when(clickedAir.getRawSlot()).thenReturn(0);
        when(clickedAir.getInventory()).thenReturn(inv);
        when(clickedAir.getCurrentItem()).thenReturn(air);
        assertFalse(action.handle(clickedAir, player, "cd", Optional.of(cd)).isHandled());

        ItemStack noMeta = mock(ItemStack.class);
        when(noMeta.getType()).thenReturn(org.bukkit.Material.PAPER);
        when(noMeta.getItemMeta()).thenReturn(null);
        InventoryClickEvent clickedNoMeta = mock(InventoryClickEvent.class);
        when(clickedNoMeta.getRawSlot()).thenReturn(0);
        when(clickedNoMeta.getInventory()).thenReturn(inv);
        when(clickedNoMeta.getCurrentItem()).thenReturn(noMeta);
        assertFalse(action.handle(clickedNoMeta, player, "cd", Optional.of(cd)).isHandled());

        ItemStack noName = mock(ItemStack.class);
        ItemMeta meta = mock(ItemMeta.class);
        when(noName.getType()).thenReturn(org.bukkit.Material.PAPER);
        when(noName.getItemMeta()).thenReturn(meta);
        when(meta.getDisplayName()).thenReturn(null);
        InventoryClickEvent clickedNoName = mock(InventoryClickEvent.class);
        when(clickedNoName.getRawSlot()).thenReturn(0);
        when(clickedNoName.getInventory()).thenReturn(inv);
        when(clickedNoName.getCurrentItem()).thenReturn(noName);
        assertFalse(action.handle(clickedNoName, player, "cd", Optional.of(cd)).isHandled());

        ItemStack ok = mock(ItemStack.class);
        ItemMeta okMeta = mock(ItemMeta.class);
        when(ok.getType()).thenReturn(org.bukkit.Material.PAPER);
        when(ok.getItemMeta()).thenReturn(okMeta);
        when(okMeta.getDisplayName()).thenReturn(ChatColor.WHITE + "one");

        InventoryClickEvent shiftLeftBoundary = mock(InventoryClickEvent.class);
        when(shiftLeftBoundary.getRawSlot()).thenReturn(0);
        when(shiftLeftBoundary.getInventory()).thenReturn(inv);
        when(shiftLeftBoundary.getCurrentItem()).thenReturn(ok);
        when(shiftLeftBoundary.getClick()).thenReturn(ClickType.SHIFT_LEFT);
        ActionResult shiftLeft = action.handle(shiftLeftBoundary, player, "cd", Optional.of(cd));
        assertTrue(shiftLeft.isHandled());
        assertTrue(shiftLeft.isMutated());

        InventoryClickEvent shiftRightBoundary = mock(InventoryClickEvent.class);
        when(shiftRightBoundary.getRawSlot()).thenReturn(1);
        when(shiftRightBoundary.getInventory()).thenReturn(inv);
        when(shiftRightBoundary.getCurrentItem()).thenReturn(ok);
        when(shiftRightBoundary.getClick()).thenReturn(ClickType.SHIFT_RIGHT);
        ActionResult shiftRight = action.handle(shiftRightBoundary, player, "cd", Optional.of(cd));
        assertTrue(shiftRight.isHandled());
        assertTrue(shiftRight.isMutated());

        InventoryClickEvent rightOutOfRange = mock(InventoryClickEvent.class);
        when(rightOutOfRange.getRawSlot()).thenReturn(5);
        when(rightOutOfRange.getInventory()).thenReturn(inv);
        when(rightOutOfRange.getCurrentItem()).thenReturn(ok);
        when(rightOutOfRange.getClick()).thenReturn(ClickType.RIGHT);
        ActionResult right = action.handle(rightOutOfRange, player, "cd", Optional.of(cd));
        assertTrue(right.isHandled());
        assertTrue(right.isMutated());
    }
}
