package com.skyblockexp.ezcountdown.command.subcommand;

import com.skyblockexp.ezcountdown.bootstrap.Registry;
import com.skyblockexp.ezcountdown.manager.LocationManager;
import com.skyblockexp.ezcountdown.manager.MessageManager;
import com.skyblockexp.ezcountdown.command.LocationPermissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

public final class LocationSubcommand implements Subcommand {
    private final Registry registry;

    public LocationSubcommand(Registry registry) {
        this.registry = registry;
    }

    @Override
    public String name() { return "location"; }

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessageManager messageManager = registry.messages();
        LocationManager locationManager = registry.locations();
        LocationPermissions locationPermissions = registry.locationPermissions();

        if (args.length < 3) {
            sender.sendMessage("Usage: /ezcd location <add|delete> <name>");
            return;
        }
        String action = args[1].toLowerCase(Locale.ROOT);
        String name = args[2];
        switch (action) {
            case "add" -> {
                if (!hasPermission(sender, locationPermissions.add())) {
                    sender.sendMessage(messageManager.message("commands.location.no-permission"));
                    return;
                }
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("Only players can add locations.");
                    return;
                }
                boolean success = locationManager.addLocation(name, player.getLocation());
                if (success) {
                    sender.sendMessage("Location '" + name + "' added.");
                } else {
                    sender.sendMessage("Location '" + name + "' already exists.");
                }
            }
            case "delete" -> {
                if (!hasPermission(sender, locationPermissions.delete())) {
                    sender.sendMessage(messageManager.message("commands.location.no-permission"));
                    return;
                }
                boolean success = locationManager.deleteLocation(name);
                if (success) {
                    sender.sendMessage("Location '" + name + "' deleted.");
                } else {
                    sender.sendMessage("Location '" + name + "' not found.");
                }
            }
            default -> sender.sendMessage("Usage: /ezcd location <add|delete> <name>");
        }
    }

    @Override
    public java.util.List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return java.util.List.of("add", "delete").stream()
                    .filter(entry -> entry.startsWith(args[1].toLowerCase(Locale.ROOT)))
                    .toList();
        }
        if (args.length == 3 && "delete".equalsIgnoreCase(args[1])) {
            return registry.locations().getLocationNames().stream()
                    .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(args[2].toLowerCase(Locale.ROOT)))
                    .sorted()
                    .toList();
        }
        return java.util.List.of();
    }

    private boolean hasPermission(CommandSender sender, String permission) {
        return permission == null || permission.isBlank() || sender.hasPermission(permission);
    }
}
