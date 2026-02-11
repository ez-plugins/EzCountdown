package com.skyblockexp.ezcountdown.listener.actions;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.manager.CountdownManager;
import com.skyblockexp.ezcountdown.manager.MessageManager;
import com.skyblockexp.ezcountdown.listener.ChatInputListener;
import com.skyblockexp.ezcountdown.util.CountdownCloner;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.Optional;

public class EditFormatMessageAction implements GuiAction {
    private final CountdownManager manager;
    private final MessageManager messageManager;
    private final ChatInputListener chatInputListener;
    private final com.skyblockexp.ezcountdown.bootstrap.Registry registry;

    public EditFormatMessageAction(CountdownManager manager, MessageManager messageManager, ChatInputListener chatInputListener, com.skyblockexp.ezcountdown.bootstrap.Registry registry) {
        this.manager = manager;
        this.messageManager = messageManager;
        this.chatInputListener = chatInputListener;
        this.registry = registry;
    }

    @Override
    public ActionResult handle(InventoryClickEvent event, Player player, String cdName, Optional<Countdown> countdownOpt) {
        if (countdownOpt.isEmpty()) return ActionResult.none();
        Countdown cd = countdownOpt.get();
        chatInputListener.request(player, new FormatInputConsumer(player, cd, cdName, manager, messageManager, registry));
        return ActionResult.handled();
    }

    private static final class FormatInputConsumer implements java.util.function.Consumer<String> {
        private final Player player;
        private final Countdown cd;
        private final String cdName;
        private final CountdownManager manager;
        private final MessageManager messageManager;
        private final com.skyblockexp.ezcountdown.bootstrap.Registry registry;

        FormatInputConsumer(Player player, Countdown cd, String cdName, CountdownManager manager, MessageManager messageManager, com.skyblockexp.ezcountdown.bootstrap.Registry registry) {
            this.player = player;
            this.cd = cd;
            this.cdName = cdName;
            this.manager = manager;
            this.messageManager = messageManager;
            this.registry = registry;
        }

        @Override
        public void accept(String input) {
            String stripped = org.bukkit.ChatColor.stripColor(input == null ? "" : input);
            com.skyblockexp.ezcountdown.api.model.Countdown newCd = new com.skyblockexp.ezcountdown.api.model.Countdown(cd.getName(), cd.getType(), cd.getDisplayTypes(), cd.getUpdateIntervalSeconds(), cd.getVisibilityPermission(), stripped, cd.getStartMessage(), cd.getEndMessage(), cd.getEndCommands(), cd.getZoneId(), false, null, 0);
            CountdownCloner.copyRuntimeFields(cd, newCd);
            if (manager.updateCountdown(cdName, newCd)) { manager.save(); player.sendMessage(messageManager.message("gui.edit.saved", java.util.Map.of("name", cdName))); org.bukkit.Bukkit.getScheduler().runTask(registry.plugin(), () -> registry.gui().editorMenu().openEditor(player, newCd)); }
        }
    }
}
