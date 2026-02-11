package com.skyblockexp.ezcountdown.gui;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.manager.CountdownManager;
import com.skyblockexp.ezcountdown.manager.MessageManager;
import com.skyblockexp.ezcountdown.command.CountdownPermissions;
import com.skyblockexp.ezcountdown.listener.ChatInputListener;
import org.bukkit.entity.Player;

public final class GuiManager {
    private final CountdownManager manager;
    private final MessageManager messageManager;
    private final com.skyblockexp.ezcountdown.bootstrap.Registry registry;

    private final MainGui mainGui;
    private final EditorMenu editorMenu;
    private final DisplayEditor displayEditor;
    private final CommandsEditor commandsEditor;
    private final ChatInputListener chatInputListener;

    public GuiManager(CountdownManager manager, MessageManager messageManager, com.skyblockexp.ezcountdown.bootstrap.Registry registry, ChatInputListener chatInputListener) {
        this.manager = manager;
        this.messageManager = messageManager;
        this.registry = registry;
        this.chatInputListener = chatInputListener;
        this.mainGui = new MainGui(manager);
        this.editorMenu = new EditorMenu(manager, chatInputListener, messageManager, registry);
        this.displayEditor = new DisplayEditor(manager, messageManager);
        this.commandsEditor = new CommandsEditor(manager, chatInputListener, messageManager, registry);
        // Listener registration is handled by bootstrap
    }

    public MainGui mainGui() { return mainGui; }
    public EditorMenu editorMenu() { return editorMenu; }
    public DisplayEditor displayEditor() { return displayEditor; }
    public CommandsEditor commandsEditor() { return commandsEditor; }
    public ChatInputListener chatInputListener() { return chatInputListener; }

    public void openMain(Player player) {
        mainGui.openMain(player);
    }

    public void closeAllOpenInventories() {
        for (org.bukkit.entity.Player p : org.bukkit.Bukkit.getOnlinePlayers()) {
            try { p.closeInventory(); } catch (Exception ignored) {}
        }
    }
}
