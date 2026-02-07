package com.skyblockexp.ezcountdown.listener.actions;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Optional;

public interface GuiAction {
    ActionResult handle(InventoryClickEvent event, Player player, String target, Optional<Countdown> countdown);
}
