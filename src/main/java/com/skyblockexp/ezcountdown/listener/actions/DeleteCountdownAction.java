package com.skyblockexp.ezcountdown.listener.actions;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.manager.CountdownManager;
import com.skyblockexp.ezcountdown.manager.MessageManager;
import com.skyblockexp.ezcountdown.bootstrap.Registry;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Optional;

public class DeleteCountdownAction implements GuiAction {
    private final CountdownManager manager;
    private final MessageManager messageManager;
    private final Registry registry;

    public DeleteCountdownAction(CountdownManager manager, MessageManager messageManager, Registry registry) {
        this.manager = manager;
        this.messageManager = messageManager;
        this.registry = registry;
    }

    @Override
    public ActionResult handle(InventoryClickEvent event, Player player, String target, Optional<Countdown> countdown) {
        if (!player.hasPermission(registry.permissions().delete())) {
            player.sendMessage(messageManager.message("commands.delete.no-permission"));
            return ActionResult.handled();
        }
        boolean ok = manager.deleteCountdown(target);
        if (ok) {
            player.sendMessage(messageManager.message("commands.delete.success", java.util.Map.of("name", target)));
            return new ActionResult(true, true, true, Optional.empty());
        } else {
            player.sendMessage(messageManager.message("commands.delete.missing", java.util.Map.of("name", target)));
            return ActionResult.handledAndClose();
        }
    }
}
