package com.skyblockexp.ezcountdown.listener.actions;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.manager.CountdownManager;
import com.skyblockexp.ezcountdown.manager.MessageManager;
import com.skyblockexp.ezcountdown.listener.AnvilClickListener;
import com.skyblockexp.ezcountdown.util.CountdownCloner;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Optional;

public class EditEndMessageAction implements GuiAction {
    private final CountdownManager manager;
    private final MessageManager messageManager;
    private final AnvilClickListener anvilHandler;

    public EditEndMessageAction(CountdownManager manager, MessageManager messageManager, AnvilClickListener anvilHandler) {
        this.manager = manager;
        this.messageManager = messageManager;
        this.anvilHandler = anvilHandler;
    }

    @Override
    public ActionResult handle(InventoryClickEvent event, Player player, String cdName, Optional<Countdown> countdownOpt) {
        if (countdownOpt.isEmpty()) return ActionResult.none();
        Countdown cd = countdownOpt.get();
        anvilHandler.request(player, new EndMessageConsumer(player, cd, cdName, manager, messageManager));
        return ActionResult.handled();
    }

    private static final class EndMessageConsumer implements java.util.function.Consumer<String> {
        private final Player player;
        private final Countdown cd;
        private final String cdName;
        private final CountdownManager manager;
        private final MessageManager messageManager;

        EndMessageConsumer(Player player, Countdown cd, String cdName, CountdownManager manager, MessageManager messageManager) {
            this.player = player;
            this.cd = cd;
            this.cdName = cdName;
            this.manager = manager;
            this.messageManager = messageManager;
        }

        @Override
        public void accept(String input) {
            com.skyblockexp.ezcountdown.api.model.Countdown newCd = new com.skyblockexp.ezcountdown.api.model.Countdown(cd.getName(), cd.getType(), cd.getDisplayTypes(), cd.getUpdateIntervalSeconds(), cd.getVisibilityPermission(), cd.getFormatMessage(), cd.getStartMessage(), input, cd.getEndCommands(), cd.getZoneId(), false, null, 0);
            CountdownCloner.copyRuntimeFields(cd, newCd);
            if (manager.updateCountdown(cdName, newCd)) { manager.save(); player.sendMessage(messageManager.message("gui.edit.saved", java.util.Map.of("name", cdName))); }
        }
    }
}
