package com.skyblockexp.ezcountdown.command.subcommand;

import com.skyblockexp.ezcountdown.bootstrap.Registry;
import com.skyblockexp.ezcountdown.manager.CountdownManager;
import com.skyblockexp.ezcountdown.manager.MessageManager;
import org.bukkit.command.CommandSender;

import java.util.Map;
import java.util.Locale;

public final class StartSubcommand implements Subcommand {
    private final Registry registry;

    public StartSubcommand(Registry registry) {
        this.registry = registry;
    }

    @Override
    public String name() { return "start"; }

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessageManager messageManager = registry.messages();
        CountdownManager manager = registry.countdowns();

        if (!hasPermission(sender, registry.permissions().start())) {
            sender.sendMessage(messageManager.message("commands.start.no-permission"));
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(messageManager.message("commands.start.usage"));
            return;
        }
        var opt = manager.getCountdown(args[1]);
        if (opt.isEmpty()) {
            sender.sendMessage(messageManager.message("commands.start.missing", Map.of("name", args[1])));
            return;
        }
        if (opt.get().isRunning()) {
            sender.sendMessage(messageManager.message("commands.start.already", Map.of("name", args[1])));
            return;
        }
        if (manager.startCountdown(args[1])) {
            sender.sendMessage(messageManager.message("commands.start.success", Map.of("name", args[1])));
        } else {
            sender.sendMessage(messageManager.message("commands.start.missing", Map.of("name", args[1])));
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
