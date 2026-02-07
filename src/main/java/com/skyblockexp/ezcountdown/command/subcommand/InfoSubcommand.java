package com.skyblockexp.ezcountdown.command.subcommand;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.bootstrap.Registry;
import com.skyblockexp.ezcountdown.manager.MessageManager;
import org.bukkit.command.CommandSender;

import java.util.Map;
import java.util.Optional;
import java.util.Locale;

public final class InfoSubcommand implements Subcommand {
    private final Registry registry;

    public InfoSubcommand(Registry registry) {
        this.registry = registry;
    }

    @Override
    public String name() { return "info"; }

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessageManager messageManager = registry.messages();
        if (!hasPermission(sender, registry.permissions().info())) {
            sender.sendMessage(messageManager.message("commands.info.no-permission"));
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(messageManager.message("commands.info.usage"));
            return;
        }
        Optional<Countdown> countdownOptional = registry.countdowns().getCountdown(args[1]);
        if (countdownOptional.isEmpty()) {
            sender.sendMessage(messageManager.message("commands.info.missing", Map.of("name", args[1])));
            return;
        }
        Countdown countdown = countdownOptional.get();
        sender.sendMessage(messageManager.message("commands.info.header", Map.of("name", countdown.getName())));
        sender.sendMessage(messageManager.message("commands.info.type", Map.of("type", countdown.getType().name())));
        sender.sendMessage(messageManager.message("commands.info.running", Map.of("running", String.valueOf(countdown.isRunning()))));
        sender.sendMessage(messageManager.message("commands.info.display", Map.of("display", countdown.getDisplayTypes().toString())));
        if (countdown.getTargetInstant() != null) {
            sender.sendMessage(messageManager.message("commands.info.target",
                    Map.of("target", countdown.getTargetInstant().atZone(countdown.getZoneId()).toString())));
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
