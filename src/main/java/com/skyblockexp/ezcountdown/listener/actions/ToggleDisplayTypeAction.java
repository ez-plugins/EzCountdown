package com.skyblockexp.ezcountdown.listener.actions;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.display.DisplayType;
import com.skyblockexp.ezcountdown.manager.CountdownManager;
import com.skyblockexp.ezcountdown.manager.MessageManager;
import com.skyblockexp.ezcountdown.gui.DisplayEditor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.EnumSet;
import java.util.Optional;

public class ToggleDisplayTypeAction implements GuiAction {
    private final CountdownManager manager;
    private final MessageManager messageManager;
    private final DisplayEditor displayEditor;

    public ToggleDisplayTypeAction(CountdownManager manager, MessageManager messageManager, DisplayEditor displayEditor) {
        this.manager = manager;
        this.messageManager = messageManager;
        this.displayEditor = displayEditor;
    }

    @Override
    public ActionResult handle(InventoryClickEvent event, Player player, String cdName, Optional<Countdown> countdownOpt) {
        if (countdownOpt.isEmpty()) return ActionResult.none();
        Countdown countdown = countdownOpt.get();
        int slot = event.getRawSlot(); DisplayType[] values = DisplayType.values(); if (slot >=0 && slot < values.length) {
            DisplayType toggled = values[slot]; EnumSet<DisplayType> set = countdown.getDisplayTypes(); EnumSet<DisplayType> newSet = EnumSet.copyOf(set);
            if (newSet.contains(toggled)) newSet.remove(toggled); else newSet.add(toggled);
            com.skyblockexp.ezcountdown.api.model.Countdown newCd = new com.skyblockexp.ezcountdown.api.model.Countdown(countdown.getName(), countdown.getType(), newSet, countdown.getUpdateIntervalSeconds(), countdown.getVisibilityPermission(), countdown.getFormatMessage(), countdown.getStartMessage(), countdown.getEndMessage(), countdown.getEndCommands(), countdown.getZoneId(), false, null, 0);
            newCd.setDurationSeconds(countdown.getDurationSeconds()); newCd.setTargetInstant(countdown.getTargetInstant()); newCd.setRecurringMonth(countdown.getRecurringMonth()); newCd.setRecurringDay(countdown.getRecurringDay()); newCd.setRecurringTime(countdown.getRecurringTime()); newCd.setRunning(countdown.isRunning());
            if (manager.updateCountdown(cdName, newCd)) { manager.save(); player.sendMessage(messageManager.message("gui.display.toggled", java.util.Map.of("name", cdName))); displayEditor.openDisplayEditor(player, newCd); }
            return ActionResult.handledAndMutated();
        }
        return ActionResult.none();
    }
}
