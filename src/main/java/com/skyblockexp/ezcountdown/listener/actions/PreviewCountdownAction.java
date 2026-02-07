package com.skyblockexp.ezcountdown.listener.actions;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.manager.MessageManager;
import com.skyblockexp.ezcountdown.util.TimeFormat;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.time.Instant;
import java.util.Optional;

public class PreviewCountdownAction implements GuiAction {
    private final MessageManager messageManager;

    public PreviewCountdownAction(MessageManager messageManager) {
        this.messageManager = messageManager;
    }

    @Override
    public ActionResult handle(InventoryClickEvent event, Player player, String target, Optional<Countdown> countdown) {
        if (countdown.isEmpty()) return ActionResult.none();
        Countdown cd = countdown.get();
        long remaining;
        if (cd.getTargetInstant() != null) {
            remaining = Math.max(0L, cd.getTargetInstant().getEpochSecond() - Instant.now().getEpochSecond());
        } else {
            remaining = cd.getDurationSeconds();
        }
        var parts = TimeFormat.toParts(remaining);
        String formatted = TimeFormat.format(parts);
        String message = cd.getFormatMessage()
                .replace("{name}", cd.getName())
                .replace("{days}", String.valueOf(parts.days()))
                .replace("{hours}", String.valueOf(parts.hours()))
                .replace("{minutes}", String.valueOf(parts.minutes()))
                .replace("{seconds}", String.valueOf(parts.seconds()))
                .replace("{formatted}", formatted);
        player.sendMessage(messageManager.formatWithPrefix(message, java.util.Map.of()));
        return ActionResult.handled();
    }
}
