package com.skyblockexp.ezcountdown.listener.actions;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.listener.ChatInputListener;
import com.skyblockexp.ezcountdown.manager.CountdownManager;
import com.skyblockexp.ezcountdown.manager.MessageManager;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Locale;
import java.util.Optional;

public final class EditStartSoundAction implements GuiAction {
    private final CountdownManager manager;
    private final MessageManager messageManager;
    private final ChatInputListener chatInputListener;
    private final com.skyblockexp.ezcountdown.bootstrap.Registry registry;

    public EditStartSoundAction(CountdownManager manager, MessageManager messageManager, ChatInputListener chatInputListener, com.skyblockexp.ezcountdown.bootstrap.Registry registry) {
        this.manager = manager;
        this.messageManager = messageManager;
        this.chatInputListener = chatInputListener;
        this.registry = registry;
    }

    @Override
    public ActionResult handle(InventoryClickEvent event, Player player, String cdName, Optional<Countdown> countdownOpt) {
        if (countdownOpt.isEmpty()) return ActionResult.none();
        Countdown cd = countdownOpt.get();
        player.sendMessage(org.bukkit.ChatColor.GRAY + "Enter start sound (Bukkit Sound enum) or 'none' to disable.");
        SoundEditorHelper.sendAvailableSounds(player);
        chatInputListener.request(player, input -> {
            String value = input == null ? "" : input.trim();
            if (value.equalsIgnoreCase("none") || value.isBlank()) {
                cd.setStartSound(null);
                manager.save();
                player.sendMessage(messageManager.message("gui.edit.saved", java.util.Map.of("name", cdName)));
                registry.scheduler().runTask(() -> registry.gui().editorMenu().openEditor(player, cd));
                return;
            }
            String normalized = value.toUpperCase(Locale.ROOT);
            boolean exists = java.util.Arrays.stream(Sound.values()).anyMatch(s -> s.name().equals(normalized));
            if (exists) {
                cd.setStartSound(value.toUpperCase(Locale.ROOT));
                manager.save();
                player.sendMessage(messageManager.message("gui.edit.saved", java.util.Map.of("name", cdName)));
                registry.scheduler().runTask(() -> registry.gui().editorMenu().openEditor(player, cd));
            } else {
                player.sendMessage(org.bukkit.ChatColor.RED + "Invalid sound name: " + value);
                SoundEditorHelper.sendAvailableSounds(player);
            }
        });
        return ActionResult.handled();
    }
}
