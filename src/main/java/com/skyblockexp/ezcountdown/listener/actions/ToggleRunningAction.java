package com.skyblockexp.ezcountdown.listener.actions;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.manager.CountdownManager;
import com.skyblockexp.ezcountdown.manager.MessageManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Optional;

public class ToggleRunningAction implements GuiAction {
    private final CountdownManager manager;
    private final MessageManager messageManager;

    public ToggleRunningAction(CountdownManager manager, MessageManager messageManager) {
        this.manager = manager;
        this.messageManager = messageManager;
    }

    @Override
    public ActionResult handle(InventoryClickEvent event, Player player, String cdName, Optional<Countdown> countdown) {
        if (countdown.isEmpty()) return ActionResult.none();
        Countdown cd = countdown.get();
        if (cd.isRunning()) {
            manager.stopCountdown(cdName);
            player.sendMessage(messageManager.message("commands.stop.success", java.util.Map.of("name", cdName)));
        } else {
            manager.startCountdown(cdName);
            player.sendMessage(messageManager.message("commands.start.success", java.util.Map.of("name", cdName)));
        }
        // indicate state changed and instruct dispatcher to save and close
        return new ActionResult(true, true, true, Optional.empty());
    }
}
