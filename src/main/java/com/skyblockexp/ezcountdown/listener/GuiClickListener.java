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
import com.skyblockexp.ezcountdown.listener.ChatInputListener;
import com.skyblockexp.ezcountdown.gui.CommandsEditor;
import com.skyblockexp.ezcountdown.gui.DisplayEditor;
import com.skyblockexp.ezcountdown.gui.EditorMenu;
import com.skyblockexp.ezcountdown.gui.MainGui;
import com.skyblockexp.ezcountdown.bootstrap.Registry;
import com.skyblockexp.ezcountdown.listener.actions.GuiActionRegistry;
import com.skyblockexp.ezcountdown.listener.actions.GuiAction;
import com.skyblockexp.ezcountdown.listener.actions.ActionResult;
import java.util.Optional;
import java.util.Map;
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
    private final ChatInputListener chatInputListener;
    private final CountdownManager manager;
    private final MessageManager messageManager;
    private final Registry registry;
    private final GuiActionRegistry actionRegistry;

    public GuiClickListener(MainGui mainGui, EditorMenu editorMenu, DisplayEditor displayEditor, CommandsEditor commandsEditor, ChatInputListener chatInputListener, CountdownManager manager, MessageManager messageManager, com.skyblockexp.ezcountdown.bootstrap.Registry registry) {
        this.mainGui = mainGui;
        this.editorMenu = editorMenu;
        this.displayEditor = displayEditor;
        this.commandsEditor = commandsEditor;
        this.chatInputListener = chatInputListener;
        this.manager = manager;
        this.messageManager = messageManager;
        this.registry = registry;
        this.actionRegistry = new GuiActionRegistry(manager, messageManager, chatInputListener, registry, editorMenu, displayEditor, commandsEditor);
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
            Optional<GuiAction> opt = actionRegistry.forMainGuiClick(clickType);
            if (opt.isPresent()) {
                ActionResult res = opt.get().handle(event, player, name, Optional.ofNullable(countdown));
                if (res != null && res.isMutated()) {
                    manager.save();
                }
                if (res != null && res.isCloseInventory()) player.closeInventory();
                return;
            }
            return;
        }

        // Delegate editor and other titles to respective handlers by prefix
        if (title.startsWith(EditorMenu.getPrefix())) {
            event.setCancelled(true);
            String cdName = title.substring(EditorMenu.getPrefix().length());
            Optional<Countdown> cd = manager.getCountdown(cdName);
            if (cd.isEmpty()) return;
            int slot = event.getRawSlot();
            Optional<GuiAction> actionOpt = actionRegistry.forEditorSlot(slot);
            if (actionOpt.isPresent()) {
                ActionResult res = actionOpt.get().handle(event, player, cdName, cd);
                if (res != null && res.isMutated()) manager.save();
                if (res != null && res.isCloseInventory()) player.closeInventory();
                return;
            }
            return;
        }

        if (title.startsWith(DisplayEditor.getPrefix())) {
            event.setCancelled(true);
            String cdName = title.substring(DisplayEditor.getPrefix().length());
            Optional<Countdown> cd = manager.getCountdown(cdName);
            if (cd.isEmpty()) return;
            int slot = event.getRawSlot();
            Optional<GuiAction> actionOpt = actionRegistry.forDisplaySlot(slot);
            if (actionOpt.isPresent()) {
                ActionResult res = actionOpt.get().handle(event, player, cdName, cd);
                if (res != null && res.isMutated()) manager.save();
                if (res != null && res.isCloseInventory()) player.closeInventory();
            }
            return;
        }

        if (title.startsWith(CommandsEditor.getPrefix())) {
            event.setCancelled(true);
            String cdName = title.substring(CommandsEditor.getPrefix().length());
            Optional<Countdown> countdown = manager.getCountdown(cdName);
            if (countdown.isEmpty()) return;
            Optional<GuiAction> actionOpt = actionRegistry.forCommands();
            if (actionOpt.isPresent()) {
                ActionResult res = actionOpt.get().handle(event, player, cdName, countdown);
                if (res != null && res.isMutated()) manager.save();
                if (res != null && res.isCloseInventory()) player.closeInventory();
            }
            return;
        }
    }
}
