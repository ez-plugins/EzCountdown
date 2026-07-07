package com.skyblockexp.ezcountdown.listener.actions;

import com.skyblockexp.ezcountdown.command.CountdownPermissions;
import com.skyblockexp.ezcountdown.manager.CountdownManager;
import com.skyblockexp.ezcountdown.manager.MessageManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DeleteCountdownActionUnitTest {

    @Test
    public void noPermissionReturnsHandledWithoutMutation() {
        CountdownManager manager = mock(CountdownManager.class);
        MessageManager messages = mock(MessageManager.class);
        com.skyblockexp.ezcountdown.bootstrap.Registry registry = mock(com.skyblockexp.ezcountdown.bootstrap.Registry.class);
        when(registry.permissions()).thenReturn(new CountdownPermissions("base", "create", "start", "stop", "delete.perm", "list", "info", "reload"));
        when(messages.message(eq("commands.delete.no-permission"))).thenReturn("no-perm");

        DeleteCountdownAction action = new DeleteCountdownAction(manager, messages, registry);
        Player player = mock(Player.class);
        when(player.hasPermission("delete.perm")).thenReturn(false);

        ActionResult result = action.handle(mock(InventoryClickEvent.class), player, "abc", Optional.empty());

        assertTrue(result.isHandled());
        assertFalse(result.isMutated());
        verify(manager, never()).deleteCountdown("abc");
    }

    @Test
    public void successfulDeleteReturnsMutatedAndClose() {
        CountdownManager manager = mock(CountdownManager.class);
        MessageManager messages = mock(MessageManager.class);
        com.skyblockexp.ezcountdown.bootstrap.Registry registry = mock(com.skyblockexp.ezcountdown.bootstrap.Registry.class);
        when(registry.permissions()).thenReturn(new CountdownPermissions("base", "create", "start", "stop", "delete.perm", "list", "info", "reload"));
        when(manager.deleteCountdown("abc")).thenReturn(true);
        when(messages.message(eq("commands.delete.success"), anyMap())).thenReturn("deleted");

        DeleteCountdownAction action = new DeleteCountdownAction(manager, messages, registry);
        Player player = mock(Player.class);
        when(player.hasPermission("delete.perm")).thenReturn(true);

        ActionResult result = action.handle(mock(InventoryClickEvent.class), player, "abc", Optional.empty());

        assertTrue(result.isHandled());
        assertTrue(result.isMutated());
        assertTrue(result.isCloseInventory());
    }

    @Test
    public void missingDeleteReturnsHandledCloseNotMutated() {
        CountdownManager manager = mock(CountdownManager.class);
        MessageManager messages = mock(MessageManager.class);
        com.skyblockexp.ezcountdown.bootstrap.Registry registry = mock(com.skyblockexp.ezcountdown.bootstrap.Registry.class);
        when(registry.permissions()).thenReturn(new CountdownPermissions("base", "create", "start", "stop", "delete.perm", "list", "info", "reload"));
        when(manager.deleteCountdown("abc")).thenReturn(false);
        when(messages.message(eq("commands.delete.missing"), anyMap())).thenReturn("missing");

        DeleteCountdownAction action = new DeleteCountdownAction(manager, messages, registry);
        Player player = mock(Player.class);
        when(player.hasPermission("delete.perm")).thenReturn(true);

        ActionResult result = action.handle(mock(InventoryClickEvent.class), player, "abc", Optional.empty());

        assertTrue(result.isHandled());
        assertFalse(result.isMutated());
        assertTrue(result.isCloseInventory());
    }
}
