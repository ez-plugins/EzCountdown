package com.skyblockexp.ezcountdown.listener.actions;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.gui.CommandsEditor;
import com.skyblockexp.ezcountdown.gui.DisplayEditor;
import com.skyblockexp.ezcountdown.gui.EditorMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class OpenEditorActionsUnitTest {

    @Test
    public void openEditorActionHandlesPresentAndEmpty() {
        EditorMenu menu = mock(EditorMenu.class);
        OpenEditorAction action = new OpenEditorAction(menu);
        Player player = mock(Player.class);
        Countdown cd = new Countdown("cd", CountdownType.MANUAL, EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "fmt", "s", "e", List.of(), ZoneId.systemDefault());

        ActionResult none = action.handle(mock(InventoryClickEvent.class), player, "cd", Optional.empty());
        assertFalse(none.isHandled());

        ActionResult ok = action.handle(mock(InventoryClickEvent.class), player, "cd", Optional.of(cd));
        assertTrue(ok.isHandled());
        verify(menu).openEditor(player, cd);
    }

    @Test
    public void openDisplayEditorActionHandlesPresentAndEmpty() {
        DisplayEditor editor = mock(DisplayEditor.class);
        OpenDisplayEditorAction action = new OpenDisplayEditorAction(editor);
        Player player = mock(Player.class);
        Countdown cd = new Countdown("cd", CountdownType.MANUAL, EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "fmt", "s", "e", List.of(), ZoneId.systemDefault());

        ActionResult none = action.handle(mock(InventoryClickEvent.class), player, "cd", Optional.empty());
        assertFalse(none.isHandled());

        ActionResult ok = action.handle(mock(InventoryClickEvent.class), player, "cd", Optional.of(cd));
        assertTrue(ok.isHandled());
        verify(editor).openDisplayEditor(player, cd);
    }

    @Test
    public void openCommandsEditorActionHandlesPresentAndEmpty() {
        CommandsEditor editor = mock(CommandsEditor.class);
        OpenCommandsEditorAction action = new OpenCommandsEditorAction(editor);
        Player player = mock(Player.class);
        Countdown cd = new Countdown("cd", CountdownType.MANUAL, EnumSet.noneOf(com.skyblockexp.ezcountdown.display.DisplayType.class), 1, null, "fmt", "s", "e", List.of(), ZoneId.systemDefault());

        ActionResult none = action.handle(mock(InventoryClickEvent.class), player, "cd", Optional.empty());
        assertFalse(none.isHandled());

        ActionResult ok = action.handle(mock(InventoryClickEvent.class), player, "cd", Optional.of(cd));
        assertTrue(ok.isHandled());
        verify(editor).openCommandsEditor(player, cd);
    }
}
