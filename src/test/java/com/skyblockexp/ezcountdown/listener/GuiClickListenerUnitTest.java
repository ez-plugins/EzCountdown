package com.skyblockexp.ezcountdown.listener;

import com.skyblockexp.ezcountdown.gui.CommandsEditor;
import com.skyblockexp.ezcountdown.gui.DisplayEditor;
import com.skyblockexp.ezcountdown.gui.EditorMenu;
import com.skyblockexp.ezcountdown.gui.MainGui;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GuiClickListenerUnitTest {

    @Test
    public void ignoresEventsWithMissingView() {
        GuiClickListener listener = new GuiClickListener(
                mock(MainGui.class),
                mock(EditorMenu.class),
                mock(DisplayEditor.class),
                mock(CommandsEditor.class),
                mock(ChatInputListener.class),
                mock(com.skyblockexp.ezcountdown.manager.CountdownManager.class),
                mock(com.skyblockexp.ezcountdown.manager.MessageManager.class),
                mock(com.skyblockexp.ezcountdown.bootstrap.Registry.class)
        );

        InventoryClickEvent event = mock(InventoryClickEvent.class);
        when(event.getView()).thenReturn(null);

        listener.onInventoryClick(event);

        verify(event, never()).setCancelled(true);
    }

    @Test
    public void mainGuiNullClickedItemIsIgnoredAfterCancel() {
        com.skyblockexp.ezcountdown.manager.CountdownManager manager = mock(com.skyblockexp.ezcountdown.manager.CountdownManager.class);
        com.skyblockexp.ezcountdown.manager.MessageManager messages = mock(com.skyblockexp.ezcountdown.manager.MessageManager.class);

        GuiClickListener listener = new GuiClickListener(
                mock(MainGui.class),
                mock(EditorMenu.class),
                mock(DisplayEditor.class),
                mock(CommandsEditor.class),
                mock(ChatInputListener.class),
                manager,
                messages,
                mock(com.skyblockexp.ezcountdown.bootstrap.Registry.class)
        );

        Player player = mock(Player.class);
        InventoryClickEvent event = mock(InventoryClickEvent.class);
        org.bukkit.inventory.InventoryView view = mock(org.bukkit.inventory.InventoryView.class);
        when(event.getView()).thenReturn(view);
        when(view.getTitle()).thenReturn(MainGui.getTitle());
        when(event.getWhoClicked()).thenReturn(player);
        when(event.getCurrentItem()).thenReturn(null);

        listener.onInventoryClick(event);

        verify(event).setCancelled(true);
        verify(player, never()).closeInventory();
    }

    @Test
    public void nonPlayerClickIsIgnored() {
        GuiClickListener listener = new GuiClickListener(
                mock(MainGui.class),
                mock(EditorMenu.class),
                mock(DisplayEditor.class),
                mock(CommandsEditor.class),
                mock(ChatInputListener.class),
                mock(com.skyblockexp.ezcountdown.manager.CountdownManager.class),
                mock(com.skyblockexp.ezcountdown.manager.MessageManager.class),
                mock(com.skyblockexp.ezcountdown.bootstrap.Registry.class)
        );

        InventoryClickEvent event = mock(InventoryClickEvent.class);
        org.bukkit.inventory.InventoryView view = mock(org.bukkit.inventory.InventoryView.class);
        when(event.getView()).thenReturn(view);
        when(view.getTitle()).thenReturn(MainGui.getTitle());
        when(event.getWhoClicked()).thenReturn(mock(HumanEntity.class));

        listener.onInventoryClick(event);

        verify(event, never()).setCancelled(true);
    }
}
