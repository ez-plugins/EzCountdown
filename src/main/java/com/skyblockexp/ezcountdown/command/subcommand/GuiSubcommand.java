package com.skyblockexp.ezcountdown.command.subcommand;

import com.skyblockexp.ezcountdown.bootstrap.Registry;
import com.skyblockexp.ezcountdown.manager.MessageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class GuiSubcommand implements Subcommand {
    private final Registry registry;

    public GuiSubcommand(Registry registry) {
        this.registry = registry;
    }

    @Override
    public String name() { return "gui"; }

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessageManager messageManager = registry.messages();
        if (!(sender instanceof Player player)) {
            sender.sendMessage(messageManager.message("commands.gui.only-players"));
            return;
        }
        if (!hasPermission(sender, registry.permissions().list())) {
            sender.sendMessage(messageManager.message("commands.gui.no-permission"));
            return;
        }
        registry.gui().openMain(player);
    }

    private boolean hasPermission(CommandSender sender, String permission) {
        return permission == null || permission.isBlank() || sender.hasPermission(permission);
    }

    @Override
    public java.util.List<String> tabComplete(CommandSender sender, String[] args) {
        return java.util.List.of();
    }
}
