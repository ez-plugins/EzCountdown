package com.skyblockexp.ezcountdown.listener.actions;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.gui.CommandsEditor;
import com.skyblockexp.ezcountdown.manager.CountdownManager;
import com.skyblockexp.ezcountdown.manager.MessageManager;
import com.skyblockexp.ezcountdown.listener.ChatInputListener;
import com.skyblockexp.ezcountdown.bootstrap.Registry;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommandsEditorActions implements GuiAction {
    private final CountdownManager manager;
    private final MessageManager messageManager;
    private final ChatInputListener chatInputListener;
    private final CommandsEditor commandsEditor;
    private final Registry registry;

    public CommandsEditorActions(CountdownManager manager, MessageManager messageManager, ChatInputListener chatInputListener, CommandsEditor commandsEditor, Registry registry) {
        this.manager = manager;
        this.messageManager = messageManager;
        this.chatInputListener = chatInputListener;
        this.commandsEditor = commandsEditor;
        this.registry = registry;
    }

    @Override
    public ActionResult handle(InventoryClickEvent event, Player player, String cdName, Optional<Countdown> countdownOpt) {
        if (countdownOpt.isEmpty()) return ActionResult.none();
        Countdown countdown = countdownOpt.get();
        int slot = event.getRawSlot(); int size = event.getInventory().getSize();
        if (slot == size - 1) {
            if (!player.hasPermission(registry.permissions().create())) { player.sendMessage(messageManager.message("commands.create.no-permission")); return ActionResult.handled(); }
            chatInputListener.request(player, new AddCommandConsumer(player, countdown, cdName, manager, messageManager, commandsEditor, registry));
            return ActionResult.handled();
        }
        ItemStack clicked = event.getCurrentItem(); if (clicked == null || clicked.getType() == org.bukkit.Material.AIR) return ActionResult.none(); if (clicked.getItemMeta() == null || clicked.getItemMeta().getDisplayName() == null) return ActionResult.none();
        String cmdText = org.bukkit.ChatColor.stripColor(clicked.getItemMeta().getDisplayName()); List<String> currentCommands = new ArrayList<>(countdown.getEndCommands()); int cmdIndex = slot; int lastCmdIndex = Math.max(0, currentCommands.size() - 1);
        ClickType click = event.getClick();
        if (click == ClickType.LEFT) {
            int index = cmdIndex; chatInputListener.request(player, new EditCommandConsumer(player, countdown, cdName, manager, messageManager, commandsEditor, index, registry));
            return ActionResult.handled();
        }
        if (click == ClickType.SHIFT_LEFT) {
                if (cmdIndex > 0 && cmdIndex <= lastCmdIndex) {
                    List<String> newCommands = new ArrayList<>(currentCommands); String v = newCommands.get(cmdIndex); newCommands.remove(cmdIndex); newCommands.add(cmdIndex - 1, v); com.skyblockexp.ezcountdown.api.model.Countdown newCd = new com.skyblockexp.ezcountdown.api.model.Countdown(countdown.getName(), countdown.getType(), countdown.getDisplayTypes(), countdown.getUpdateIntervalSeconds(), countdown.getVisibilityPermission(), countdown.getFormatMessage(), countdown.getStartMessage(), countdown.getEndMessage(), newCommands, countdown.getZoneId(), false, null, 0); newCd.setDurationSeconds(countdown.getDurationSeconds()); newCd.setTargetInstant(countdown.getTargetInstant()); newCd.setRecurringMonth(countdown.getRecurringMonth()); newCd.setRecurringDay(countdown.getRecurringDay()); newCd.setRecurringTime(countdown.getRecurringTime()); newCd.setRunning(countdown.isRunning()); if (manager.updateCountdown(cdName, newCd)) { manager.save(); player.sendMessage(messageManager.message("gui.commands.moved", java.util.Map.of("name", cdName))); org.bukkit.Bukkit.getScheduler().runTask(registry.plugin(), () -> commandsEditor.openCommandsEditor(player, newCd)); }
            }
            return ActionResult.handledAndMutated();
        }
        if (click == ClickType.SHIFT_RIGHT) {
            if (cmdIndex >=0 && cmdIndex < lastCmdIndex) {
                 List<String> newCommands = new ArrayList<>(currentCommands); String v = newCommands.get(cmdIndex); newCommands.remove(cmdIndex); newCommands.add(cmdIndex + 1, v); com.skyblockexp.ezcountdown.api.model.Countdown newCd = new com.skyblockexp.ezcountdown.api.model.Countdown(countdown.getName(), countdown.getType(), countdown.getDisplayTypes(), countdown.getUpdateIntervalSeconds(), countdown.getVisibilityPermission(), countdown.getFormatMessage(), countdown.getStartMessage(), countdown.getEndMessage(), newCommands, countdown.getZoneId(), false, null, 0); newCd.setDurationSeconds(countdown.getDurationSeconds()); newCd.setTargetInstant(countdown.getTargetInstant()); newCd.setRecurringMonth(countdown.getRecurringMonth()); newCd.setRecurringDay(countdown.getRecurringDay()); newCd.setRecurringTime(countdown.getRecurringTime()); newCd.setRunning(countdown.isRunning()); if (manager.updateCountdown(cdName, newCd)) { manager.save(); player.sendMessage(messageManager.message("gui.commands.moved", java.util.Map.of("name", cdName))); org.bukkit.Bukkit.getScheduler().runTask(registry.plugin(), () -> commandsEditor.openCommandsEditor(player, newCd)); }
            }
            return ActionResult.handledAndMutated();
        }
        if (click == ClickType.RIGHT) {
            List<String> newCommands = new ArrayList<>(currentCommands); if (cmdIndex >=0 && cmdIndex <= lastCmdIndex) { newCommands.remove(cmdIndex); com.skyblockexp.ezcountdown.api.model.Countdown newCd = new com.skyblockexp.ezcountdown.api.model.Countdown(countdown.getName(), countdown.getType(), countdown.getDisplayTypes(), countdown.getUpdateIntervalSeconds(), countdown.getVisibilityPermission(), countdown.getFormatMessage(), countdown.getStartMessage(), countdown.getEndMessage(), newCommands, countdown.getZoneId(), false, null, 0); newCd.setDurationSeconds(countdown.getDurationSeconds()); newCd.setTargetInstant(countdown.getTargetInstant()); newCd.setRecurringMonth(countdown.getRecurringMonth()); newCd.setRecurringDay(countdown.getRecurringDay()); newCd.setRecurringTime(countdown.getRecurringTime()); newCd.setRunning(countdown.isRunning()); if (manager.updateCountdown(cdName, newCd)) { manager.save(); player.sendMessage(messageManager.message("gui.commands.removed", java.util.Map.of("name", cdName))); org.bukkit.Bukkit.getScheduler().runTask(registry.plugin(), () -> commandsEditor.openCommandsEditor(player, newCd)); } }
            return ActionResult.handledAndMutated();
        }
        return ActionResult.none();
    }

    private static final class AddCommandConsumer implements java.util.function.Consumer<String> {
        private final Player player;
        private final Countdown countdown;
        private final String cdName;
        private final CountdownManager manager;
        private final MessageManager messageManager;
        private final CommandsEditor commandsEditor;
        private final Registry registry;

        AddCommandConsumer(Player player, Countdown countdown, String cdName, CountdownManager manager, MessageManager messageManager, CommandsEditor commandsEditor, Registry registry) {
            this.player = player;
            this.countdown = countdown;
            this.cdName = cdName;
            this.manager = manager;
            this.messageManager = messageManager;
            this.commandsEditor = commandsEditor;
            this.registry = registry;
        }

        @Override
        public void accept(String input) {
            List<String> newCommands = new ArrayList<>(countdown.getEndCommands()); newCommands.add(input);
            com.skyblockexp.ezcountdown.api.model.Countdown newCd = new com.skyblockexp.ezcountdown.api.model.Countdown(countdown.getName(), countdown.getType(), countdown.getDisplayTypes(), countdown.getUpdateIntervalSeconds(), countdown.getVisibilityPermission(), countdown.getFormatMessage(), countdown.getStartMessage(), countdown.getEndMessage(), newCommands, countdown.getZoneId(), false, null, 0);
            newCd.setDurationSeconds(countdown.getDurationSeconds()); newCd.setTargetInstant(countdown.getTargetInstant()); newCd.setRecurringMonth(countdown.getRecurringMonth()); newCd.setRecurringDay(countdown.getRecurringDay()); newCd.setRecurringTime(countdown.getRecurringTime()); newCd.setRunning(countdown.isRunning());
            if (manager.updateCountdown(cdName, newCd)) { manager.save(); player.sendMessage(messageManager.message("gui.commands.added", java.util.Map.of("name", cdName))); org.bukkit.Bukkit.getScheduler().runTask(registry.plugin(), () -> commandsEditor.openCommandsEditor(player, newCd)); }
        }
    }

    private static final class EditCommandConsumer implements java.util.function.Consumer<String> {
        private final Player player;
        private final Countdown countdown;
        private final String cdName;
        private final CountdownManager manager;
        private final MessageManager messageManager;
        private final CommandsEditor commandsEditor;
        private final int index;
        private final Registry registry;

        EditCommandConsumer(Player player, Countdown countdown, String cdName, CountdownManager manager, MessageManager messageManager, CommandsEditor commandsEditor, int index, Registry registry) {
            this.player = player;
            this.countdown = countdown;
            this.cdName = cdName;
            this.manager = manager;
            this.messageManager = messageManager;
            this.commandsEditor = commandsEditor;
            this.index = index;
            this.registry = registry;
        }

        @Override
        public void accept(String input) {
            List<String> newCommands = new ArrayList<>(countdown.getEndCommands()); if (index >=0 && index < newCommands.size()) { newCommands.set(index, input); com.skyblockexp.ezcountdown.api.model.Countdown newCd = new com.skyblockexp.ezcountdown.api.model.Countdown(countdown.getName(), countdown.getType(), countdown.getDisplayTypes(), countdown.getUpdateIntervalSeconds(), countdown.getVisibilityPermission(), countdown.getFormatMessage(), countdown.getStartMessage(), countdown.getEndMessage(), newCommands, countdown.getZoneId(), false, null, 0); newCd.setDurationSeconds(countdown.getDurationSeconds()); newCd.setTargetInstant(countdown.getTargetInstant()); newCd.setRecurringMonth(countdown.getRecurringMonth()); newCd.setRecurringDay(countdown.getRecurringDay()); newCd.setRecurringTime(countdown.getRecurringTime()); newCd.setRunning(countdown.isRunning()); if (manager.updateCountdown(cdName, newCd)) { manager.save(); player.sendMessage(messageManager.message("gui.commands.edited", java.util.Map.of("name", cdName))); org.bukkit.Bukkit.getScheduler().runTask(registry.plugin(), () -> commandsEditor.openCommandsEditor(player, newCd)); } }
        }
    }
}
