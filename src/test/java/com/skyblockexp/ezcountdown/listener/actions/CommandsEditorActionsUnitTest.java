package com.skyblockexp.ezcountdown.listener.actions;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.command.CountdownPermissions;
import com.skyblockexp.ezcountdown.gui.CommandsEditor;
import com.skyblockexp.ezcountdown.listener.ChatInputListener;
import com.skyblockexp.ezcountdown.manager.CountdownManager;
import com.skyblockexp.ezcountdown.manager.MessageManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
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
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CommandsEditorActionsUnitTest {

    @Test
    public void coversCoreBranches() {
        CountdownManager manager = mock(CountdownManager.class);
        MessageManager messages = mock(MessageManager.class);
        ChatInputListener chat = mock(ChatInputListener.class);
        CommandsEditor editor = mock(CommandsEditor.class);
        com.skyblockexp.ezcountdown.bootstrap.Registry registry = mock(com.skyblockexp.ezcountdown.bootstrap.Registry.class);
        com.skyblockexp.ezcountdown.compat.scheduler.SchedulerAdapter scheduler = mock(com.skyblockexp.ezcountdown.compat.scheduler.SchedulerAdapter.class);

        when(registry.scheduler()).thenReturn(scheduler);
        when(registry.permissions()).thenReturn(new CountdownPermissions("base", "create.perm", "start", "stop", "delete", "list", "info", "reload"));
        when(messages.message(eq("commands.create.no-permission"))).thenReturn("no-perm");
        when(messages.message(eq("gui.commands.moved"), anyMap())).thenReturn("moved");
        when(messages.message(eq("gui.commands.removed"), anyMap())).thenReturn("removed");
        when(messages.message(eq("gui.commands.added"), anyMap())).thenReturn("added");
        when(messages.message(eq("gui.commands.edited"), anyMap())).thenReturn("edited");
        when(manager.updateCountdown(eq("cd"), any(Countdown.class))).thenReturn(true);
        doAnswer(call -> {
            Runnable r = call.getArgument(0);
            r.run();
            return new com.skyblockexp.ezcountdown.compat.scheduler.TaskHandle() {
                @Override public void cancel() {}
                @Override public boolean isCancelled() { return false; }
            };
        }).when(scheduler).runTask(any(Runnable.class));

        CommandsEditorActions action = new CommandsEditorActions(manager, messages, chat, editor, registry);
        Player player = mock(Player.class);
        Countdown cd = new Countdown("cd", CountdownType.MANUAL, EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "fmt", "s", "e", List.of("one", "two", "three"), ZoneId.systemDefault());

        ActionResult none = action.handle(mock(InventoryClickEvent.class), player, "cd", Optional.empty());
        assertFalse(none.isHandled());

        Inventory inv = mock(Inventory.class);
        when(inv.getSize()).thenReturn(9);

        InventoryClickEvent addClickNoPerm = mock(InventoryClickEvent.class);
        when(addClickNoPerm.getRawSlot()).thenReturn(8);
        when(addClickNoPerm.getInventory()).thenReturn(inv);
        when(player.hasPermission("create.perm")).thenReturn(false);
        ActionResult addNoPerm = action.handle(addClickNoPerm, player, "cd", Optional.of(cd));
        assertTrue(addNoPerm.isHandled());

        AtomicReference<Consumer<String>> captured = new AtomicReference<>();
        doAnswer(call2 -> { captured.set(call2.getArgument(1)); return null; }).when(chat).request(any(Player.class), any());
        when(player.hasPermission("create.perm")).thenReturn(true);
        InventoryClickEvent addClick = mock(InventoryClickEvent.class);
        when(addClick.getRawSlot()).thenReturn(8);
        when(addClick.getInventory()).thenReturn(inv);
        action.handle(addClick, player, "cd", Optional.of(cd));
        captured.get().accept("say added");

        ItemStack clicked = mock(ItemStack.class);
        ItemMeta meta = mock(ItemMeta.class);
        when(clicked.getType()).thenReturn(org.bukkit.Material.PAPER);
        when(clicked.getItemMeta()).thenReturn(meta);
        when(meta.getDisplayName()).thenReturn(ChatColor.WHITE + "one");

        InventoryClickEvent left = mock(InventoryClickEvent.class);
        when(left.getRawSlot()).thenReturn(0);
        when(left.getInventory()).thenReturn(inv);
        when(left.getCurrentItem()).thenReturn(clicked);
        when(left.getClick()).thenReturn(ClickType.LEFT);
        action.handle(left, player, "cd", Optional.of(cd));
        captured.get().accept("say edited");

        InventoryClickEvent shiftLeft = mock(InventoryClickEvent.class);
        when(shiftLeft.getRawSlot()).thenReturn(1);
        when(shiftLeft.getInventory()).thenReturn(inv);
        when(shiftLeft.getCurrentItem()).thenReturn(clicked);
        when(shiftLeft.getClick()).thenReturn(ClickType.SHIFT_LEFT);
        ActionResult movedLeft = action.handle(shiftLeft, player, "cd", Optional.of(cd));
        assertTrue(movedLeft.isMutated());

        InventoryClickEvent shiftRight = mock(InventoryClickEvent.class);
        when(shiftRight.getRawSlot()).thenReturn(0);
        when(shiftRight.getInventory()).thenReturn(inv);
        when(shiftRight.getCurrentItem()).thenReturn(clicked);
        when(shiftRight.getClick()).thenReturn(ClickType.SHIFT_RIGHT);
        ActionResult movedRight = action.handle(shiftRight, player, "cd", Optional.of(cd));
        assertTrue(movedRight.isMutated());

        InventoryClickEvent right = mock(InventoryClickEvent.class);
        when(right.getRawSlot()).thenReturn(0);
        when(right.getInventory()).thenReturn(inv);
        when(right.getCurrentItem()).thenReturn(clicked);
        when(right.getClick()).thenReturn(ClickType.RIGHT);
        ActionResult removed = action.handle(right, player, "cd", Optional.of(cd));
        assertTrue(removed.isMutated());

        InventoryClickEvent noneClick = mock(InventoryClickEvent.class);
        when(noneClick.getRawSlot()).thenReturn(0);
        when(noneClick.getInventory()).thenReturn(inv);
        when(noneClick.getCurrentItem()).thenReturn(clicked);
        when(noneClick.getClick()).thenReturn(ClickType.MIDDLE);
        ActionResult noAction = action.handle(noneClick, player, "cd", Optional.of(cd));
        assertFalse(noAction.isHandled());

        verify(manager, org.mockito.Mockito.atLeastOnce()).save();
        verify(editor, org.mockito.Mockito.atLeastOnce()).openCommandsEditor(eq(player), any(Countdown.class));
    }
}
