package com.skyblockexp.ezcountdown.listener.actions;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.gui.CommandsEditor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Optional;

public class OpenCommandsEditorAction implements GuiAction {
    private final CommandsEditor commandsEditor;

    public OpenCommandsEditorAction(CommandsEditor commandsEditor) {
        this.commandsEditor = commandsEditor;
    }

    @Override
    public ActionResult handle(InventoryClickEvent event, Player player, String cdName, Optional<Countdown> countdown) {
        if (countdown.isEmpty()) return ActionResult.none();
        commandsEditor.openCommandsEditor(player, countdown.get());
        return ActionResult.handled();
    }
}
