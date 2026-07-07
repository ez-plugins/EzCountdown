package com.skyblockexp.ezcountdown.listener;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.gui.CommandsEditor;
import com.skyblockexp.ezcountdown.gui.DisplayEditor;
import com.skyblockexp.ezcountdown.gui.EditorMenu;
import com.skyblockexp.ezcountdown.gui.MainGui;
import com.skyblockexp.ezcountdown.listener.actions.ActionResult;
import com.skyblockexp.ezcountdown.listener.actions.GuiAction;
import com.skyblockexp.ezcountdown.listener.actions.GuiActionRegistry;
import com.skyblockexp.ezcountdown.manager.CountdownManager;
import com.skyblockexp.ezcountdown.manager.MessageManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.ZoneId;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GuiClickListenerTest {

    @Test
    public void handlesMainGuiMissingCountdownAndActionResult() throws Exception {
        CountdownManager manager = mock(CountdownManager.class);
        MessageManager messages = mock(MessageManager.class);
        GuiClickListener listener = buildListener(manager, messages);

        GuiActionRegistry registryMock = mock(GuiActionRegistry.class);
        GuiAction action = mock(GuiAction.class);
        when(registryMock.forMainGuiClick(ClickType.LEFT)).thenReturn(Optional.of(action));
        when(action.handle(any(), any(), eq("cd"), any())).thenReturn(new ActionResult(true, true, true, Optional.empty()));
        setRegistry(listener, registryMock);

        Player player = mock(Player.class);
        InventoryClickEvent missing = mainGuiEvent(player, ClickType.LEFT, "cd");
        when(manager.getCountdown("cd")).thenReturn(Optional.empty());
        when(messages.message(eq("commands.info.missing"), any(Map.class))).thenReturn("missing");

        listener.onInventoryClick(missing);

        verify(player).sendMessage("missing");
        verify(player).closeInventory();

        Countdown cd = new Countdown("cd", CountdownType.MANUAL, EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "fmt", "s", "e", List.of(), ZoneId.systemDefault());
        when(manager.getCountdown("cd")).thenReturn(Optional.of(cd));

        listener.onInventoryClick(mainGuiEvent(player, ClickType.LEFT, "cd"));

        verify(manager).save();
        verify(player, org.mockito.Mockito.atLeast(2)).closeInventory();
    }

    @Test
    public void handlesEditorDisplayAndCommandsPaths() throws Exception {
        CountdownManager manager = mock(CountdownManager.class);
        MessageManager messages = mock(MessageManager.class);
        GuiClickListener listener = buildListener(manager, messages);

        GuiActionRegistry registryMock = mock(GuiActionRegistry.class);
        GuiAction action = mock(GuiAction.class);
        when(action.handle(any(), any(), any(), any())).thenReturn(new ActionResult(true, true, true, Optional.empty()));
        when(registryMock.forEditorSlot(3)).thenReturn(Optional.of(action));
        when(registryMock.forDisplaySlot(4)).thenReturn(Optional.of(action));
        when(registryMock.forCommands()).thenReturn(Optional.of(action));
        setRegistry(listener, registryMock);

        Player player = mock(Player.class);
        Countdown cd = new Countdown("cd", CountdownType.MANUAL, EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "fmt", "s", "e", List.of(), ZoneId.systemDefault());
        when(manager.getCountdown("cd")).thenReturn(Optional.of(cd));

        listener.onInventoryClick(prefixedEvent(player, EditorMenu.getPrefix() + "cd", 3));
        listener.onInventoryClick(prefixedEvent(player, DisplayEditor.getPrefix() + "cd", 4));
        listener.onInventoryClick(prefixedEvent(player, CommandsEditor.getPrefix() + "cd", 2));

        verify(manager, org.mockito.Mockito.atLeast(3)).save();
        verify(player, org.mockito.Mockito.atLeast(3)).closeInventory();
    }

    @Test
    public void coversNoActionAndUnknownTitleBranches() throws Exception {
        CountdownManager manager = mock(CountdownManager.class);
        MessageManager messages = mock(MessageManager.class);
        GuiClickListener listener = buildListener(manager, messages);

        GuiActionRegistry registryMock = mock(GuiActionRegistry.class);
        when(registryMock.forMainGuiClick(ClickType.MIDDLE)).thenReturn(Optional.empty());
        when(registryMock.forEditorSlot(2)).thenReturn(Optional.empty());
        setRegistry(listener, registryMock);

        Player player = mock(Player.class);
        Countdown cd = new Countdown("cd", CountdownType.MANUAL, EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "fmt", "s", "e", List.of(), ZoneId.systemDefault());
        when(manager.getCountdown("cd")).thenReturn(Optional.of(cd));

        listener.onInventoryClick(mainGuiEvent(player, ClickType.MIDDLE, "cd"));
        listener.onInventoryClick(prefixedEvent(player, EditorMenu.getPrefix() + "cd", 2));

        when(manager.getCountdown("missing")).thenReturn(Optional.empty());
        listener.onInventoryClick(prefixedEvent(player, EditorMenu.getPrefix() + "missing", 1));
        listener.onInventoryClick(prefixedEvent(player, DisplayEditor.getPrefix() + "missing", 1));
        listener.onInventoryClick(prefixedEvent(player, CommandsEditor.getPrefix() + "missing", 1));

        InventoryClickEvent unknown = mock(InventoryClickEvent.class);
        InventoryView unknownView = mock(InventoryView.class);
        when(unknown.getView()).thenReturn(unknownView);
        when(unknownView.getTitle()).thenReturn("Some Other Title");
        when(unknown.getWhoClicked()).thenReturn(player);
        listener.onInventoryClick(unknown);
    }

    private static GuiClickListener buildListener(CountdownManager manager, MessageManager messages) {
        return new GuiClickListener(
                mock(MainGui.class),
                mock(EditorMenu.class),
                mock(DisplayEditor.class),
                mock(CommandsEditor.class),
                mock(ChatInputListener.class),
                manager,
                messages,
                mock(com.skyblockexp.ezcountdown.bootstrap.Registry.class)
        );
    }

    private static void setRegistry(GuiClickListener listener, GuiActionRegistry registry) throws Exception {
        Field f = GuiClickListener.class.getDeclaredField("actionRegistry");
        f.setAccessible(true);
        f.set(listener, registry);
    }

    private static InventoryClickEvent mainGuiEvent(Player player, ClickType clickType, String displayName) {
        InventoryClickEvent event = mock(InventoryClickEvent.class);
        InventoryView view = mock(InventoryView.class);
        ItemStack clicked = mock(ItemStack.class);
        ItemMeta meta = mock(ItemMeta.class);

        when(event.getView()).thenReturn(view);
        when(view.getTitle()).thenReturn(MainGui.getTitle());
        when(event.getWhoClicked()).thenReturn(player);
        when(event.getClick()).thenReturn(clickType);
        when(event.getCurrentItem()).thenReturn(clicked);
        when(clicked.getType()).thenReturn(org.bukkit.Material.PAPER);
        when(clicked.getItemMeta()).thenReturn(meta);
        when(meta.getDisplayName()).thenReturn(ChatColor.WHITE + displayName);
        return event;
    }

    private static InventoryClickEvent prefixedEvent(Player player, String title, int rawSlot) {
        InventoryClickEvent event = mock(InventoryClickEvent.class);
        InventoryView view = mock(InventoryView.class);

        when(event.getView()).thenReturn(view);
        when(view.getTitle()).thenReturn(title);
        when(event.getWhoClicked()).thenReturn(player);
        when(event.getRawSlot()).thenReturn(rawSlot);
        return event;
    }
}
