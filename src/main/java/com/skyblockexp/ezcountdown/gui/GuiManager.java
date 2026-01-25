package com.skyblockexp.ezcountdown.gui;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.manager.CountdownManager;
import com.skyblockexp.ezcountdown.manager.MessageManager;
import com.skyblockexp.ezcountdown.command.CountdownPermissions;
import com.skyblockexp.ezcountdown.listener.AnvilClickListener;
import org.bukkit.entity.Player;

public final class GuiManager {
    private final CountdownManager manager;
    private final MessageManager messageManager;
    private final CountdownPermissions permissions;

    private final MainGui mainGui;
    private final EditorMenu editorMenu;
    private final DisplayEditor displayEditor;
    private final CommandsEditor commandsEditor;
    private final AnvilClickListener anvilHandler;

    public GuiManager(CountdownManager manager, MessageManager messageManager, CountdownPermissions permissions, AnvilClickListener anvilHandler) {
        this.manager = manager;
        this.messageManager = messageManager;
        this.permissions = permissions;

        this.anvilHandler = anvilHandler;
        this.mainGui = new MainGui(manager);
        this.editorMenu = new EditorMenu(manager, anvilHandler, messageManager, permissions);
        this.displayEditor = new DisplayEditor(manager, messageManager);
        this.commandsEditor = new CommandsEditor(manager, anvilHandler, messageManager, permissions);
        // Listener registration is handled by bootstrap
    }

    public MainGui mainGui() { return mainGui; }
    public EditorMenu editorMenu() { return editorMenu; }
    public DisplayEditor displayEditor() { return displayEditor; }
    public CommandsEditor commandsEditor() { return commandsEditor; }
    public AnvilClickListener anvilHandler() { return anvilHandler; }

    public void openMain(Player player) {
        mainGui.openMain(player);
    }
}
