package com.skyblockexp.ezcountdown.listener.actions;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.gui.DisplayEditor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Optional;

public class OpenDisplayEditorAction implements GuiAction {
    private final DisplayEditor displayEditor;

    public OpenDisplayEditorAction(DisplayEditor displayEditor) {
        this.displayEditor = displayEditor;
    }

    @Override
    public ActionResult handle(InventoryClickEvent event, Player player, String cdName, Optional<Countdown> countdown) {
        if (countdown.isEmpty()) return ActionResult.none();
        displayEditor.openDisplayEditor(player, countdown.get());
        return ActionResult.handled();
    }
}
