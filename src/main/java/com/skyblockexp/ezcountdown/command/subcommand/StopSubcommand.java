package com.skyblockexp.ezcountdown.command.subcommand;

import com.skyblockexp.ezcountdown.bootstrap.Registry;
import com.skyblockexp.ezcountdown.manager.CountdownManager;
import com.skyblockexp.ezcountdown.manager.MessageManager;
import org.bukkit.command.CommandSender;

import java.util.Map;
import java.util.Locale;

public final class StopSubcommand implements Subcommand {
    private final Registry registry;

    public StopSubcommand(Registry registry) {
        this.registry = registry;
    }

    @Override
    public String name() { return "stop"; }

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessageManager messageManager = registry.messages();
        CountdownManager manager = registry.countdowns();

        if (!hasPermission(sender, registry.permissions().stop())) {
            sender.sendMessage(messageManager.message("commands.stop.no-permission"));
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(messageManager.message("commands.stop.usage"));
            return;
        }
        if (manager.stopCountdown(args[1])) {
            sender.sendMessage(messageManager.message("commands.stop.success", Map.of("name", args[1])));
        } else {
            sender.sendMessage(messageManager.message("commands.stop.missing", Map.of("name", args[1])));
        }
    }

    private boolean hasPermission(CommandSender sender, String permission) {
        return permission == null || permission.isBlank() || sender.hasPermission(permission);
    }

    @Override
    public java.util.List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return registry.countdowns().getCountdowns().stream()
                    .map(com.skyblockexp.ezcountdown.api.model.Countdown::getName)
                    .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(args[1].toLowerCase(Locale.ROOT)))
                    .sorted()
                    .toList();
        }
        return java.util.List.of();
    }
}
