package com.skyblockexp.ezcountdown.command;

import com.skyblockexp.ezcountdown.bootstrap.Registry;
import com.skyblockexp.ezcountdown.command.subcommand.Subcommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Locale;

public final class CountdownCommand implements CommandExecutor, TabCompleter {

    private final Registry registry;
    private final java.util.Map<String, Subcommand> subcommands;

    public CountdownCommand(Registry registry, java.util.Map<String, Subcommand> subcommands, Runnable reloadAction) {
        this.registry = registry;
        this.subcommands = subcommands == null ? java.util.Map.of() : subcommands;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!hasPermission(sender, registry.permissions().base())) {
            sender.sendMessage(registry.messages().message("commands.no-permission"));
            return true;
        }
        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }
        String sub = args[0].toLowerCase(Locale.ROOT);
        if (!subcommands.isEmpty() && subcommands.containsKey(sub)) {
            try {
                subcommands.get(sub).execute(sender, args);
            } catch (Exception ex) {
                sender.sendMessage(registry.messages().message("commands.error", java.util.Map.of("reason", ex.getMessage())));
            }
            return true;
        }
        sendUsage(sender);
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length >= 1) {
            String sub = args[0].toLowerCase(Locale.ROOT);
            if (!subcommands.isEmpty() && subcommands.containsKey(sub)) {
                java.util.List<String> res = subcommands.get(sub).tabComplete(sender, args);
                return res == null ? java.util.List.of() : res;
            }
            if (args.length == 1) {
                return subcommands.keySet().stream()
                        .sorted()
                        .filter(entry -> entry.startsWith(args[0].toLowerCase(Locale.ROOT)))
                        .toList();
            }
        }
        return java.util.List.of();
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(registry.messages().message("commands.usage.header"));
        sender.sendMessage(registry.messages().message("commands.usage.create"));
        sender.sendMessage(registry.messages().message("commands.usage.start"));
        sender.sendMessage(registry.messages().message("commands.usage.stop"));
        sender.sendMessage(registry.messages().message("commands.usage.delete"));
        sender.sendMessage(registry.messages().message("commands.usage.list"));
        sender.sendMessage(registry.messages().message("commands.usage.info"));
        sender.sendMessage(registry.messages().message("commands.usage.reload"));
    }

    private boolean hasPermission(CommandSender sender, String permission) {
        return permission == null || permission.isBlank() || sender.hasPermission(permission);
    }

}
