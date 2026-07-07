package com.skyblockexp.ezcountdown.listener.actions;

import com.skyblockexp.ezcountdown.gui.CommandsEditor;
import com.skyblockexp.ezcountdown.gui.DisplayEditor;
import com.skyblockexp.ezcountdown.gui.EditorMenu;
import org.bukkit.event.inventory.ClickType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class GuiActionRegistryUnitTest {

    @Test
    public void registryReturnsExpectedMainGuiActions() {
        GuiActionRegistry registry = new GuiActionRegistry(
                mock(com.skyblockexp.ezcountdown.manager.CountdownManager.class),
                mock(com.skyblockexp.ezcountdown.manager.MessageManager.class),
                mock(com.skyblockexp.ezcountdown.listener.ChatInputListener.class),
                mock(com.skyblockexp.ezcountdown.bootstrap.Registry.class),
                mock(EditorMenu.class),
                mock(DisplayEditor.class),
                mock(CommandsEditor.class)
        );

        assertTrue(registry.forMainGuiClick(ClickType.LEFT).isPresent());
        assertTrue(registry.forMainGuiClick(ClickType.RIGHT).isPresent());
        assertTrue(registry.forMainGuiClick(ClickType.SHIFT_RIGHT).isPresent());
        assertFalse(registry.forMainGuiClick(ClickType.MIDDLE).isPresent());
    }

    @Test
    public void registryReturnsExpectedEditorSlotMappings() {
        GuiActionRegistry registry = new GuiActionRegistry(
                mock(com.skyblockexp.ezcountdown.manager.CountdownManager.class),
                mock(com.skyblockexp.ezcountdown.manager.MessageManager.class),
                mock(com.skyblockexp.ezcountdown.listener.ChatInputListener.class),
                mock(com.skyblockexp.ezcountdown.bootstrap.Registry.class),
                mock(EditorMenu.class),
                mock(DisplayEditor.class),
                mock(CommandsEditor.class)
        );

        for (int i = 0; i <= 10; i++) {
            assertTrue(registry.forEditorSlot(i).isPresent());
        }
        assertFalse(registry.forEditorSlot(11).isPresent());
        assertFalse(registry.forEditorSlot(-1).isPresent());

        assertTrue(registry.forDisplaySlot(0).isPresent());
        assertTrue(registry.forCommands().isPresent());
    }
}
