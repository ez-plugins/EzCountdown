package com.skyblockexp.ezcountdown.listener;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.util.TimeFormat;
import com.skyblockexp.ezcountdown.util.DurationParser;
import com.skyblockexp.ezcountdown.type.CountdownTypeHandler;
import com.skyblockexp.ezcountdown.command.CountdownPermissions;
import com.skyblockexp.ezcountdown.manager.CountdownManager;
import com.skyblockexp.ezcountdown.manager.MessageManager;
import com.skyblockexp.ezcountdown.display.DisplayType;
import com.skyblockexp.ezcountdown.listener.AnvilClickListener;
import com.skyblockexp.ezcountdown.gui.CommandsEditor;
import com.skyblockexp.ezcountdown.gui.DisplayEditor;
import com.skyblockexp.ezcountdown.gui.EditorMenu;
import com.skyblockexp.ezcountdown.gui.MainGui;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public final class GuiClickListener implements Listener {
    private final MainGui mainGui;
    private final EditorMenu editorMenu;
    private final DisplayEditor displayEditor;
    private final CommandsEditor commandsEditor;
    private final AnvilClickListener anvilHandler;
    private final CountdownManager manager;
    private final MessageManager messageManager;
    private final CountdownPermissions permissions;

    public GuiClickListener(MainGui mainGui, EditorMenu editorMenu, DisplayEditor displayEditor, CommandsEditor commandsEditor, AnvilClickListener anvilHandler, CountdownManager manager, MessageManager messageManager, CountdownPermissions permissions) {
        this.mainGui = mainGui;
        this.editorMenu = editorMenu;
        this.displayEditor = displayEditor;
        this.commandsEditor = commandsEditor;
        this.anvilHandler = anvilHandler;
        this.manager = manager;
        this.messageManager = messageManager;
        this.permissions = permissions;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView() == null || event.getView().getTitle() == null) return;
        String title = event.getView().getTitle();
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (MainGui.getTitle().equals(title)) {
            event.setCancelled(true);
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || clicked.getType() == Material.AIR) return;
            if (clicked.getItemMeta() == null || clicked.getItemMeta().getDisplayName() == null) return;
            String name = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
            Countdown countdown = manager.getCountdown(name).orElse(null);
            if (countdown == null) {
                player.sendMessage(messageManager.message("commands.info.missing", java.util.Map.of("name", name)));
                player.closeInventory();
                return;
            }
            ClickType clickType = event.getClick();
            if (clickType == ClickType.LEFT) {
                editorMenu.openEditor(player, countdown);
                return;
            }
            if (clickType == ClickType.RIGHT) {
                long remaining = 0L;
                if (countdown.getTargetInstant() != null) {
                    remaining = Math.max(0L, countdown.getTargetInstant().getEpochSecond() - Instant.now().getEpochSecond());
                } else {
                    remaining = countdown.getDurationSeconds();
                }
                var parts = com.skyblockexp.ezcountdown.util.TimeFormat.toParts(remaining);
                String formatted = com.skyblockexp.ezcountdown.util.TimeFormat.format(parts);
                String message = countdown.getFormatMessage()
                        .replace("{name}", countdown.getName())
                        .replace("{days}", String.valueOf(parts.days()))
                        .replace("{hours}", String.valueOf(parts.hours()))
                        .replace("{minutes}", String.valueOf(parts.minutes()))
                        .replace("{seconds}", String.valueOf(parts.seconds()))
                        .replace("{formatted}", formatted);
                player.sendMessage(messageManager.formatWithPrefix(message, java.util.Map.of()));
                return;
            }
            if (clickType == ClickType.SHIFT_RIGHT) {
                if (!player.hasPermission(permissions.delete())) {
                    player.sendMessage(messageManager.message("commands.delete.no-permission"));
                    return;
                }
                boolean ok = manager.deleteCountdown(name);
                if (ok) {
                    manager.save();
                    player.sendMessage(messageManager.message("commands.delete.success", java.util.Map.of("name", name)));
                } else {
                    player.sendMessage(messageManager.message("commands.delete.missing", java.util.Map.of("name", name)));
                }
                player.closeInventory();
            }
            return;
        }

        // Delegate editor and other titles to respective handlers by prefix
        if (title.startsWith(EditorMenu.getPrefix())) {
            event.setCancelled(true);
            String cdName = title.substring(EditorMenu.getPrefix().length());
            Countdown cd = manager.getCountdown(cdName).orElse(null);
            if (cd == null) return;
            int slot = event.getRawSlot();
            switch (slot) {
                case 0 -> {
                    if (cd.isRunning()) { manager.stopCountdown(cdName); player.sendMessage(messageManager.message("commands.stop.success", java.util.Map.of("name", cdName))); }
                    else { manager.startCountdown(cdName); player.sendMessage(messageManager.message("commands.start.success", java.util.Map.of("name", cdName))); }
                    manager.save(); player.closeInventory();
                }
                case 1 -> displayEditor.openDisplayEditor(player, cd);
                case 2 -> {
                    if (!player.hasPermission(permissions.create())) { player.sendMessage(messageManager.message("commands.create.no-permission")); return; }
                    anvilHandler.request(player, input -> {
                        CountdownTypeHandler handler = manager.getHandler(cd.getType());
                        try {
                            if (handler != null) {
                                boolean applied = handler.tryApplyEditorInput(input, cd, Instant.now());
                                if (!applied) throw new IllegalArgumentException("Input not applicable");
                            } else {
                                // legacy fallback: try duration then ISO instant
                                try {
                                    long seconds = DurationParser.parseToSeconds(input);
                                    cd.setDurationSeconds(seconds);
                                    if (cd.getType() == CountdownType.DURATION || cd.getType() == CountdownType.MANUAL) {
                                        if (cd.isRunning()) cd.setTargetInstant(Instant.now().plusSeconds(seconds));
                                    }
                                } catch (IllegalArgumentException ex) {
                                    Instant inst = Instant.parse(input);
                                    cd.setTargetInstant(inst);
                                }
                            }
                            manager.save(); player.sendMessage(messageManager.message("gui.edit.saved", java.util.Map.of("name", cdName)));
                        } catch (IllegalArgumentException ex) {
                            player.sendMessage(messageManager.message("gui.edit.invalid-duration", java.util.Map.of("reason", ex.getMessage())));
                        } catch (Exception ex) {
                            player.sendMessage(messageManager.message("gui.edit.invalid-duration", java.util.Map.of("reason", ex.getMessage())));
                        }
                    });
                }
                case 3 -> commandsEditor.openCommandsEditor(player, cd);
                case 4 -> anvilHandler.request(player, input -> {
                    com.skyblockexp.ezcountdown.api.model.Countdown newCd = new com.skyblockexp.ezcountdown.api.model.Countdown(cd.getName(), cd.getType(), cd.getDisplayTypes(), cd.getUpdateIntervalSeconds(), cd.getVisibilityPermission(), input, cd.getStartMessage(), cd.getEndMessage(), cd.getEndCommands(), cd.getZoneId());
                    newCd.setDurationSeconds(cd.getDurationSeconds()); newCd.setTargetInstant(cd.getTargetInstant()); newCd.setRecurringMonth(cd.getRecurringMonth()); newCd.setRecurringDay(cd.getRecurringDay()); newCd.setRecurringTime(cd.getRecurringTime()); newCd.setRunning(cd.isRunning());
                    if (manager.updateCountdown(cdName, newCd)) { manager.save(); player.sendMessage(messageManager.message("gui.edit.saved", java.util.Map.of("name", cdName))); }
                });
                case 6 -> anvilHandler.request(player, input -> {
                    com.skyblockexp.ezcountdown.api.model.Countdown newCd = new com.skyblockexp.ezcountdown.api.model.Countdown(cd.getName(), cd.getType(), cd.getDisplayTypes(), cd.getUpdateIntervalSeconds(), cd.getVisibilityPermission(), cd.getFormatMessage(), input, cd.getEndMessage(), cd.getEndCommands(), cd.getZoneId());
                    newCd.setDurationSeconds(cd.getDurationSeconds()); newCd.setTargetInstant(cd.getTargetInstant()); newCd.setRecurringMonth(cd.getRecurringMonth()); newCd.setRecurringDay(cd.getRecurringDay()); newCd.setRecurringTime(cd.getRecurringTime()); newCd.setRunning(cd.isRunning());
                    if (manager.updateCountdown(cdName, newCd)) { manager.save(); player.sendMessage(messageManager.message("gui.edit.saved", java.util.Map.of("name", cdName))); }
                });
                case 8 -> anvilHandler.request(player, input -> {
                    com.skyblockexp.ezcountdown.api.model.Countdown newCd = new com.skyblockexp.ezcountdown.api.model.Countdown(cd.getName(), cd.getType(), cd.getDisplayTypes(), cd.getUpdateIntervalSeconds(), cd.getVisibilityPermission(), cd.getFormatMessage(), cd.getStartMessage(), input, cd.getEndCommands(), cd.getZoneId());
                    newCd.setDurationSeconds(cd.getDurationSeconds()); newCd.setTargetInstant(cd.getTargetInstant()); newCd.setRecurringMonth(cd.getRecurringMonth()); newCd.setRecurringDay(cd.getRecurringDay()); newCd.setRecurringTime(cd.getRecurringTime()); newCd.setRunning(cd.isRunning());
                    if (manager.updateCountdown(cdName, newCd)) { manager.save(); player.sendMessage(messageManager.message("gui.edit.saved", java.util.Map.of("name", cdName))); }
                });
                default -> {}
            }
            return;
        }

        if (title.startsWith(DisplayEditor.getPrefix())) {
            event.setCancelled(true);
            String cdName = title.substring(DisplayEditor.getPrefix().length());
            Countdown countdown = manager.getCountdown(cdName).orElse(null); if (countdown == null) return;
            int slot = event.getRawSlot(); DisplayType[] values = DisplayType.values(); if (slot >=0 && slot < values.length) {
                DisplayType toggled = values[slot]; EnumSet<DisplayType> set = countdown.getDisplayTypes(); EnumSet<DisplayType> newSet = EnumSet.copyOf(set);
                if (newSet.contains(toggled)) newSet.remove(toggled); else newSet.add(toggled);
                com.skyblockexp.ezcountdown.api.model.Countdown newCd = new com.skyblockexp.ezcountdown.api.model.Countdown(countdown.getName(), countdown.getType(), newSet, countdown.getUpdateIntervalSeconds(), countdown.getVisibilityPermission(), countdown.getFormatMessage(), countdown.getStartMessage(), countdown.getEndMessage(), countdown.getEndCommands(), countdown.getZoneId());
                newCd.setDurationSeconds(countdown.getDurationSeconds()); newCd.setTargetInstant(countdown.getTargetInstant()); newCd.setRecurringMonth(countdown.getRecurringMonth()); newCd.setRecurringDay(countdown.getRecurringDay()); newCd.setRecurringTime(countdown.getRecurringTime()); newCd.setRunning(countdown.isRunning());
                if (manager.updateCountdown(cdName, newCd)) { manager.save(); player.sendMessage(messageManager.message("gui.display.toggled", java.util.Map.of("name", cdName))); displayEditor.openDisplayEditor(player, newCd); }
            }
            return;
        }

        if (title.startsWith(CommandsEditor.getPrefix())) {
            event.setCancelled(true);
            String cdName = title.substring(CommandsEditor.getPrefix().length()); Countdown countdown = manager.getCountdown(cdName).orElse(null); if (countdown == null) return;
            int slot = event.getRawSlot(); int size = event.getInventory().getSize();
            if (slot == size - 1) {
                if (!player.hasPermission(permissions.create())) { player.sendMessage(messageManager.message("commands.create.no-permission")); return; }
                anvilHandler.request(player, input -> {
                    List<String> newCommands = new ArrayList<>(countdown.getEndCommands()); newCommands.add(input);
                    com.skyblockexp.ezcountdown.api.model.Countdown newCd = new com.skyblockexp.ezcountdown.api.model.Countdown(countdown.getName(), countdown.getType(), countdown.getDisplayTypes(), countdown.getUpdateIntervalSeconds(), countdown.getVisibilityPermission(), countdown.getFormatMessage(), countdown.getStartMessage(), countdown.getEndMessage(), newCommands, countdown.getZoneId());
                    newCd.setDurationSeconds(countdown.getDurationSeconds()); newCd.setTargetInstant(countdown.getTargetInstant()); newCd.setRecurringMonth(countdown.getRecurringMonth()); newCd.setRecurringDay(countdown.getRecurringDay()); newCd.setRecurringTime(countdown.getRecurringTime()); newCd.setRunning(countdown.isRunning());
                    if (manager.updateCountdown(cdName, newCd)) { manager.save(); player.sendMessage(messageManager.message("gui.commands.added", java.util.Map.of("name", cdName))); commandsEditor.openCommandsEditor(player, newCd); }
                });
                return;
            }
            ItemStack clicked = event.getCurrentItem(); if (clicked == null || clicked.getType() == Material.AIR) return; if (clicked.getItemMeta() == null || clicked.getItemMeta().getDisplayName() == null) return;
            String cmdText = ChatColor.stripColor(clicked.getItemMeta().getDisplayName()); List<String> currentCommands = new ArrayList<>(countdown.getEndCommands()); int cmdIndex = slot; int lastCmdIndex = Math.max(0, currentCommands.size() - 1);
            ClickType click = event.getClick();
            if (click == ClickType.LEFT) {
                int index = cmdIndex; anvilHandler.request(player, input -> { List<String> newCommands = new ArrayList<>(countdown.getEndCommands()); if (index >=0 && index < newCommands.size()) { newCommands.set(index, input); com.skyblockexp.ezcountdown.api.model.Countdown newCd = new com.skyblockexp.ezcountdown.api.model.Countdown(countdown.getName(), countdown.getType(), countdown.getDisplayTypes(), countdown.getUpdateIntervalSeconds(), countdown.getVisibilityPermission(), countdown.getFormatMessage(), countdown.getStartMessage(), countdown.getEndMessage(), newCommands, countdown.getZoneId()); newCd.setDurationSeconds(countdown.getDurationSeconds()); newCd.setTargetInstant(countdown.getTargetInstant()); newCd.setRecurringMonth(countdown.getRecurringMonth()); newCd.setRecurringDay(countdown.getRecurringDay()); newCd.setRecurringTime(countdown.getRecurringTime()); newCd.setRunning(countdown.isRunning()); if (manager.updateCountdown(cdName, newCd)) { manager.save(); player.sendMessage(messageManager.message("gui.commands.edited", java.util.Map.of("name", cdName))); commandsEditor.openCommandsEditor(player, newCd); } } });
                return;
            }
            if (click == ClickType.SHIFT_LEFT) {
                if (cmdIndex > 0 && cmdIndex <= lastCmdIndex) {
                    List<String> newCommands = new ArrayList<>(currentCommands); String v = newCommands.get(cmdIndex); newCommands.remove(cmdIndex); newCommands.add(cmdIndex - 1, v); com.skyblockexp.ezcountdown.api.model.Countdown newCd = new com.skyblockexp.ezcountdown.api.model.Countdown(countdown.getName(), countdown.getType(), countdown.getDisplayTypes(), countdown.getUpdateIntervalSeconds(), countdown.getVisibilityPermission(), countdown.getFormatMessage(), countdown.getStartMessage(), countdown.getEndMessage(), newCommands, countdown.getZoneId()); newCd.setDurationSeconds(countdown.getDurationSeconds()); newCd.setTargetInstant(countdown.getTargetInstant()); newCd.setRecurringMonth(countdown.getRecurringMonth()); newCd.setRecurringDay(countdown.getRecurringDay()); newCd.setRecurringTime(countdown.getRecurringTime()); newCd.setRunning(countdown.isRunning()); if (manager.updateCountdown(cdName, newCd)) { manager.save(); player.sendMessage(messageManager.message("gui.commands.moved", java.util.Map.of("name", cdName))); commandsEditor.openCommandsEditor(player, newCd); }
                }
                return;
            }
            if (click == ClickType.SHIFT_RIGHT) {
                if (cmdIndex >=0 && cmdIndex < lastCmdIndex) {
                    List<String> newCommands = new ArrayList<>(currentCommands); String v = newCommands.get(cmdIndex); newCommands.remove(cmdIndex); newCommands.add(cmdIndex + 1, v); com.skyblockexp.ezcountdown.api.model.Countdown newCd = new com.skyblockexp.ezcountdown.api.model.Countdown(countdown.getName(), countdown.getType(), countdown.getDisplayTypes(), countdown.getUpdateIntervalSeconds(), countdown.getVisibilityPermission(), countdown.getFormatMessage(), countdown.getStartMessage(), countdown.getEndMessage(), newCommands, countdown.getZoneId()); newCd.setDurationSeconds(countdown.getDurationSeconds()); newCd.setTargetInstant(countdown.getTargetInstant()); newCd.setRecurringMonth(countdown.getRecurringMonth()); newCd.setRecurringDay(countdown.getRecurringDay()); newCd.setRecurringTime(countdown.getRecurringTime()); newCd.setRunning(countdown.isRunning()); if (manager.updateCountdown(cdName, newCd)) { manager.save(); player.sendMessage(messageManager.message("gui.commands.moved", java.util.Map.of("name", cdName))); commandsEditor.openCommandsEditor(player, newCd); }
                }
                return;
            }
            if (click == ClickType.RIGHT) {
                List<String> newCommands = new ArrayList<>(currentCommands); if (cmdIndex >=0 && cmdIndex <= lastCmdIndex) { newCommands.remove(cmdIndex); com.skyblockexp.ezcountdown.api.model.Countdown newCd = new com.skyblockexp.ezcountdown.api.model.Countdown(countdown.getName(), countdown.getType(), countdown.getDisplayTypes(), countdown.getUpdateIntervalSeconds(), countdown.getVisibilityPermission(), countdown.getFormatMessage(), countdown.getStartMessage(), countdown.getEndMessage(), newCommands, countdown.getZoneId()); newCd.setDurationSeconds(countdown.getDurationSeconds()); newCd.setTargetInstant(countdown.getTargetInstant()); newCd.setRecurringMonth(countdown.getRecurringMonth()); newCd.setRecurringDay(countdown.getRecurringDay()); newCd.setRecurringTime(countdown.getRecurringTime()); newCd.setRunning(countdown.isRunning()); if (manager.updateCountdown(cdName, newCd)) { manager.save(); player.sendMessage(messageManager.message("gui.commands.removed", java.util.Map.of("name", cdName))); commandsEditor.openCommandsEditor(player, newCd); } }
                return;
            }
            return;
        }
    }
}
