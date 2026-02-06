package com.skyblockexp.ezcountdown.command.subcommand;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.bootstrap.Registry;
import com.skyblockexp.ezcountdown.manager.MessageManager;
import org.bukkit.command.CommandSender;

import java.util.StringJoiner;

public final class ListSubcommand implements Subcommand {
    private final Registry registry;

    public ListSubcommand(Registry registry) {
        this.registry = registry;
    }

    @Override
    public String name() { return "list"; }

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessageManager messageManager = registry.messages();
        if (!hasPermission(sender, registry.permissions().list())) {
            sender.sendMessage(messageManager.message("commands.list.no-permission"));
            return;
        }
        if (registry.countdowns().getCountdowns().isEmpty()) {
            sender.sendMessage(messageManager.message("commands.list.empty"));
            return;
        }
        String entryFormat = messageManager.raw("commands.list.entry");
        String separator = messageManager.raw("commands.list.separator");
        String runningLabel = messageManager.raw("commands.list.status.running");
        String stoppedLabel = messageManager.raw("commands.list.status.stopped");
        StringJoiner joiner = new StringJoiner(separator);
        for (Countdown countdown : registry.countdowns().getCountdowns()) {
            String entry = entryFormat
                    .replace("{name}", countdown.getName())
                    .replace("{status}", countdown.isRunning() ? runningLabel : stoppedLabel);
            joiner.add(entry);
        }
        sender.sendMessage(messageManager.message("commands.list.format", java.util.Map.of("list", joiner.toString())));
    }

    private boolean hasPermission(CommandSender sender, String permission) {
        return permission == null || permission.isBlank() || sender.hasPermission(permission);
    }

    @Override
    public java.util.List<String> tabComplete(CommandSender sender, String[] args) {
        return java.util.List.of();
    }
}
