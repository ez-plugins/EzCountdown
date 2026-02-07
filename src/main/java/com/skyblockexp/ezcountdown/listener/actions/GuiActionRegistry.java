package com.skyblockexp.ezcountdown.listener.actions;

import com.skyblockexp.ezcountdown.gui.CommandsEditor;
import com.skyblockexp.ezcountdown.gui.DisplayEditor;
import com.skyblockexp.ezcountdown.gui.EditorMenu;
import com.skyblockexp.ezcountdown.manager.CountdownManager;
import com.skyblockexp.ezcountdown.manager.MessageManager;
import com.skyblockexp.ezcountdown.listener.AnvilClickListener;
import com.skyblockexp.ezcountdown.bootstrap.Registry;
import org.bukkit.event.inventory.ClickType;

import java.util.Optional;

public class GuiActionRegistry {
    private final OpenEditorAction openEditorAction;
    private final PreviewCountdownAction previewAction;
    private final DeleteCountdownAction deleteAction;
    private final ToggleRunningAction toggleRunningAction;
    private final OpenDisplayEditorAction openDisplayEditorAction;
    private final EditDurationOrTargetAction editDurationOrTargetAction;
    private final OpenCommandsEditorAction openCommandsEditorAction;
    private final EditFormatMessageAction editFormatMessageAction;
    private final ToggleAutoRestartAction toggleAutoRestartAction;
    private final EditStartMessageAction editStartMessageAction;
    private final EditStartCountdownTargetAction editStartCountdownTargetAction;
    private final EditEndMessageAction editEndMessageAction;
    private final ToggleDisplayTypeAction toggleDisplayTypeAction;
    private final CommandsEditorActions commandsEditorActions;

    public GuiActionRegistry(CountdownManager manager, MessageManager messageManager, AnvilClickListener anvilHandler, Registry registry, EditorMenu editorMenu, DisplayEditor displayEditor, CommandsEditor commandsEditor) {
        this.openEditorAction = new OpenEditorAction(editorMenu);
        this.previewAction = new PreviewCountdownAction(messageManager);
        this.deleteAction = new DeleteCountdownAction(manager, messageManager, registry);
        this.toggleRunningAction = new ToggleRunningAction(manager, messageManager);
        this.openDisplayEditorAction = new OpenDisplayEditorAction(displayEditor);
        this.editDurationOrTargetAction = new EditDurationOrTargetAction(manager, messageManager, anvilHandler);
        this.openCommandsEditorAction = new OpenCommandsEditorAction(commandsEditor);
        this.editFormatMessageAction = new EditFormatMessageAction(manager, messageManager, anvilHandler);
        this.toggleAutoRestartAction = new ToggleAutoRestartAction(manager, messageManager, editorMenu);
        this.editStartMessageAction = new EditStartMessageAction(manager, messageManager, anvilHandler);
        this.editStartCountdownTargetAction = new EditStartCountdownTargetAction(manager, messageManager, anvilHandler);
        this.editEndMessageAction = new EditEndMessageAction(manager, messageManager, anvilHandler);
        this.toggleDisplayTypeAction = new ToggleDisplayTypeAction(manager, messageManager, displayEditor);
        this.commandsEditorActions = new CommandsEditorActions(manager, messageManager, anvilHandler, commandsEditor, registry);
    }

    public Optional<GuiAction> forMainGuiClick(ClickType clickType) {
        return switch (clickType) {
            case LEFT -> Optional.of(openEditorAction);
            case RIGHT -> Optional.of(previewAction);
            case SHIFT_RIGHT -> Optional.of(deleteAction);
            default -> Optional.empty();
        };
    }

    public Optional<GuiAction> forEditorSlot(int slot) {
        return switch (slot) {
            case 0 -> Optional.of(toggleRunningAction);
            case 1 -> Optional.of(openDisplayEditorAction);
            case 2 -> Optional.of(editDurationOrTargetAction);
            case 3 -> Optional.of(openCommandsEditorAction);
            case 4 -> Optional.of(editFormatMessageAction);
            case 5 -> Optional.of(toggleAutoRestartAction);
            case 6 -> Optional.of(editStartMessageAction);
            case 7 -> Optional.of(editStartCountdownTargetAction);
            case 8 -> Optional.of(editEndMessageAction);
            default -> Optional.empty();
        };
    }

    public Optional<GuiAction> forDisplaySlot(int slot) {
        return Optional.of(toggleDisplayTypeAction);
    }

    public Optional<GuiAction> forCommands() {
        return Optional.of(commandsEditorActions);
    }
}
