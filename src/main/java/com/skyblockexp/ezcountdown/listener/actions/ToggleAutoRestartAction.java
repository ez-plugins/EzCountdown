package com.skyblockexp.ezcountdown.listener.actions;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.manager.CountdownManager;
import com.skyblockexp.ezcountdown.manager.MessageManager;
import com.skyblockexp.ezcountdown.util.CountdownCloner;
import com.skyblockexp.ezcountdown.gui.EditorMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Optional;

public class ToggleAutoRestartAction implements GuiAction {
    private final CountdownManager manager;
    private final MessageManager messageManager;
    private final EditorMenu editorMenu;

    public ToggleAutoRestartAction(CountdownManager manager, MessageManager messageManager, EditorMenu editorMenu) {
        this.manager = manager;
        this.messageManager = messageManager;
        this.editorMenu = editorMenu;
    }

    @Override
    public ActionResult handle(InventoryClickEvent event, Player player, String cdName, Optional<Countdown> countdownOpt) {
        if (countdownOpt.isEmpty()) return ActionResult.none();
        Countdown cd = countdownOpt.get();
        boolean newVal = !cd.isAutoRestart();
        com.skyblockexp.ezcountdown.api.model.Countdown newCd = new com.skyblockexp.ezcountdown.api.model.Countdown(cd.getName(), cd.getType(), cd.getDisplayTypes(), cd.getUpdateIntervalSeconds(), cd.getVisibilityPermission(), cd.getFormatMessage(), cd.getStartMessage(), cd.getEndMessage(), cd.getEndCommands(), cd.getZoneId(), newVal, cd.getStartCountdown(), cd.getRestartDelaySeconds());
        CountdownCloner.copyRuntimeFields(cd, newCd);
        if (manager.updateCountdown(cdName, newCd)) { player.sendMessage(messageManager.message("gui.edit.saved", java.util.Map.of("name", cdName))); editorMenu.openEditor(player, newCd); return new ActionResult(true, true, false, Optional.empty()); }
        return ActionResult.handled();
    }
}
