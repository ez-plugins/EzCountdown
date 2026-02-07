package com.skyblockexp.ezcountdown.listener.actions;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.gui.EditorMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Optional;

public class OpenEditorAction implements GuiAction {
    private final EditorMenu editorMenu;

    public OpenEditorAction(EditorMenu editorMenu) {
        this.editorMenu = editorMenu;
    }

    @Override
    public ActionResult handle(InventoryClickEvent event, Player player, String target, Optional<Countdown> countdown) {
        if (countdown.isPresent()) {
            editorMenu.openEditor(player, countdown.get());
            return ActionResult.handled();
        }
        return ActionResult.none();
    }
}
