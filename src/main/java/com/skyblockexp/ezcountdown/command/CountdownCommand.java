package com.skyblockexp.ezcountdown.command;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.display.DisplayType;
import com.skyblockexp.ezcountdown.manager.CountdownDefaults;
import com.skyblockexp.ezcountdown.manager.CountdownManager;
import com.skyblockexp.ezcountdown.manager.MessageManager;
import com.skyblockexp.ezcountdown.util.DurationParser;
import com.skyblockexp.ezcountdown.manager.LocationManager;
import com.skyblockexp.ezcountdown.gui.GuiManager;
import com.skyblockexp.ezcountdown.bootstrap.Registry;
import com.skyblockexp.ezcountdown.type.CountdownTypeHandler;
import org.bukkit.plugin.java.JavaPlugin;
// CountdownEditorGui removed; using GuiManager instead
import com.skyblockexp.ezcountdown.command.LocationPermissions;
import org.bukkit.entity.Player;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public final class CountdownCommand implements CommandExecutor, TabCompleter {

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final CountdownManager manager;
    private final CountdownDefaults defaults;
    private final CountdownPermissions permissions;
    private final MessageManager messageManager;
    private final Runnable reloadAction;
    private final LocationManager locationManager;
    private final LocationPermissions locationPermissions;
    // plugin not required; use Registry-provided components
    private final GuiManager gui;
    private final Registry registry;

    public CountdownCommand(Registry registry, Runnable reloadAction) {
        this.manager = registry.countdowns();
        this.defaults = registry.defaults();
        this.permissions = registry.permissions();
        this.messageManager = registry.messages();
        this.reloadAction = reloadAction;
        this.locationManager = registry.locations();
        this.locationPermissions = registry.locationPermissions();
        this.gui = registry.gui();
        this.registry = registry;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!hasPermission(sender, permissions.base())) {
            sender.sendMessage(messageManager.message("commands.no-permission"));
            return true;
        }
        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }
        String sub = args[0].toLowerCase(Locale.ROOT);
        switch (sub) {
            case "create" -> handleCreate(sender, args);
            case "start" -> handleStart(sender, args);
            case "stop" -> handleStop(sender, args);
            case "delete" -> handleDelete(sender, args);
            case "gui" -> handleGui(sender);
            case "list" -> handleList(sender);
            case "info" -> handleInfo(sender, args);
            case "reload" -> handleReload(sender, command);
            case "location" -> handleLocation(sender, args);
            default -> sendUsage(sender);
        }
        return true;
    }
    private void handleLocation(CommandSender sender, String[] args) {
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
    public java.util.List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
                return java.util.List.of("create", "start", "stop", "delete", "list", "info", "reload", "location", "gui").stream()
                    .filter(entry -> entry.startsWith(args[0].toLowerCase(Locale.ROOT)))
                    .toList();
        }
        if (args.length == 2 && "location".equalsIgnoreCase(args[0])) {
            return java.util.List.of("add", "delete").stream()
                    .filter(entry -> entry.startsWith(args[1].toLowerCase(Locale.ROOT)))
                    .toList();
        }
        if (args.length == 3 && "location".equalsIgnoreCase(args[0]) && "delete".equalsIgnoreCase(args[1])) {
            // Suggest existing location names for deletion
            return locationManager.getLocationNames().stream()
                    .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(args[2].toLowerCase(Locale.ROOT)))
                    .sorted()
                    .toList();
        }
        if (args.length == 2 && ("start".equalsIgnoreCase(args[0])
                || "stop".equalsIgnoreCase(args[0])
                || "delete".equalsIgnoreCase(args[0])
                || "info".equalsIgnoreCase(args[0]))) {
            return manager.getCountdowns().stream()
                    .map(Countdown::getName)
                    .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(args[1].toLowerCase(Locale.ROOT)))
                    .sorted()
                    .toList();
        }
        if (args.length == 3 && "create".equalsIgnoreCase(args[0])) {
            return java.util.List.of("manual", "recurring", "duration", "2026-01-01");
        }
        return java.util.List.of();
    }

    private void handleCreate(CommandSender sender, String[] args) {
        if (!hasPermission(sender, permissions.create())) {
            sender.sendMessage(messageManager.message("commands.create.no-permission"));
            return;
        }
        if (args.length < 3) {
            sender.sendMessage(messageManager.message("commands.create.usage"));
            return;
        }
        String name = args[1];
        String typeToken = args[2];
        Countdown countdown;
        CountdownType type;
        if ("recurring".equalsIgnoreCase(typeToken)) {
            type = CountdownType.RECURRING;
        } else if ("manual".equalsIgnoreCase(typeToken)) {
            type = CountdownType.MANUAL;
        } else if ("duration".equalsIgnoreCase(typeToken)) {
            type = CountdownType.DURATION;
        } else {
            // treat as fixed date token (date string)
            type = CountdownType.FIXED_DATE;
        }

        countdown = buildCountdown(name, type);
        CountdownTypeHandler handler = registry.getHandler(type);
        try {
            String[] handlerArgs;
            if (type == CountdownType.FIXED_DATE) {
                handlerArgs = java.util.Arrays.copyOfRange(args, 2, args.length); // [date, optional time]
            } else {
                handlerArgs = java.util.Arrays.copyOfRange(args, 3, args.length); // args after type token
            }
            if (handler != null) {
                handler.configureFromCreateArgs(countdown, handlerArgs, defaults);
            } else {
                // fallback to legacy parsing for safety
                if (type == CountdownType.FIXED_DATE) {
                    String date = typeToken;
                    String time = args.length > 3 ? args[3] : "00:00";
                    try {
                        LocalDateTime parsed = LocalDateTime.parse(date + " " + time, DATE_TIME_FORMAT);
                        ZoneId zoneId = defaults.zoneId();
                        countdown.setTargetInstant(parsed.atZone(zoneId).toInstant());
                        countdown.setRunning(defaults.startOnCreate());
                    } catch (DateTimeParseException ex) {
                        sender.sendMessage(messageManager.message("commands.create.invalid-date"));
                        return;
                    }
                } else if (type == CountdownType.DURATION || type == CountdownType.MANUAL) {
                    if (args.length < 4) {
                        sender.sendMessage(messageManager.message(type == CountdownType.DURATION ? "commands.create.duration-usage" : "commands.create.manual-usage"));
                        return;
                    }
                    try {
                        countdown.setDurationSeconds(DurationParser.parseToSeconds(args[3]));
                    } catch (IllegalArgumentException ex) {
                        sender.sendMessage(messageManager.message("commands.create.invalid-duration", Map.of("reason", ex.getMessage())));
                        return;
                    }
                    if (type == CountdownType.DURATION) countdown.setRunning(defaults.startOnCreate());
                    else countdown.setRunning(false);
                    if (countdown.isRunning() && countdown.getTargetInstant() == null) countdown.setTargetInstant(Instant.now().plusSeconds(countdown.getDurationSeconds()));
                } else if (type == CountdownType.RECURRING) {
                    if (args.length < 6) {
                        sender.sendMessage(messageManager.message("commands.create.recurring-usage"));
                        return;
                    }
                    int month;
                    int day;
                    LocalTime time;
                    try {
                        month = Integer.parseInt(args[3]);
                        day = Integer.parseInt(args[4]);
                        time = LocalTime.parse(args[5]);
                    } catch (NumberFormatException | DateTimeParseException ex) {
                        sender.sendMessage(messageManager.message("commands.create.invalid-recurring"));
                        return;
                    }
                    countdown.setRecurringMonth(month);
                    countdown.setRecurringDay(day);
                    countdown.setRecurringTime(time);
                    countdown.setTargetInstant(countdown.resolveNextRecurringTarget(Instant.now()));
                    countdown.setRunning(true);
                }
            }
        } catch (IllegalArgumentException ex) {
            sender.sendMessage(messageManager.message("commands.create.invalid-args", Map.of("reason", ex.getMessage())));
            return;
        }
        if (!manager.createCountdown(countdown)) {
            sender.sendMessage(messageManager.message("commands.create.exists", Map.of("name", name)));
            return;
        }
        manager.save();
        sender.sendMessage(messageManager.message("commands.create.success", Map.of("name", name)));
    }

    private void handleStart(CommandSender sender, String[] args) {
        if (!hasPermission(sender, permissions.start())) {
            sender.sendMessage(messageManager.message("commands.start.no-permission"));
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(messageManager.message("commands.start.usage"));
            return;
        }
        if (manager.startCountdown(args[1])) {
            sender.sendMessage(messageManager.message("commands.start.success", Map.of("name", args[1])));
        } else {
            sender.sendMessage(messageManager.message("commands.start.missing", Map.of("name", args[1])));
        }
    }

    private void handleStop(CommandSender sender, String[] args) {
        if (!hasPermission(sender, permissions.stop())) {
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

    private void handleDelete(CommandSender sender, String[] args) {
        if (!hasPermission(sender, permissions.delete())) {
            sender.sendMessage(messageManager.message("commands.delete.no-permission"));
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(messageManager.message("commands.delete.usage"));
            return;
        }
        if (manager.deleteCountdown(args[1])) {
            // Persist deletion to storage so it does not reappear after a restart
            manager.save();
            sender.sendMessage(messageManager.message("commands.delete.success", Map.of("name", args[1])));
        } else {
            sender.sendMessage(messageManager.message("commands.delete.missing", Map.of("name", args[1])));
        }
    }

    private void handleList(CommandSender sender) {
        if (!hasPermission(sender, permissions.list())) {
            sender.sendMessage(messageManager.message("commands.list.no-permission"));
            return;
        }
        if (manager.getCountdowns().isEmpty()) {
            sender.sendMessage(messageManager.message("commands.list.empty"));
            return;
        }
        String entryFormat = messageManager.raw("commands.list.entry");
        String separator = messageManager.raw("commands.list.separator");
        String runningLabel = messageManager.raw("commands.list.status.running");
        String stoppedLabel = messageManager.raw("commands.list.status.stopped");
        StringJoiner joiner = new StringJoiner(separator);
        for (Countdown countdown : manager.getCountdowns()) {
            String entry = entryFormat
                    .replace("{name}", countdown.getName())
                    .replace("{status}", countdown.isRunning() ? runningLabel : stoppedLabel);
            joiner.add(entry);
        }
        sender.sendMessage(messageManager.message("commands.list.format", Map.of("list", joiner.toString())));
    }

    private void handleInfo(CommandSender sender, String[] args) {
        if (!hasPermission(sender, permissions.info())) {
            sender.sendMessage(messageManager.message("commands.info.no-permission"));
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(messageManager.message("commands.info.usage"));
            return;
        }
        Optional<Countdown> countdownOptional = manager.getCountdown(args[1]);
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

    private void handleReload(CommandSender sender, Command command) {
        if (!hasPermission(sender, permissions.reload())) {
            sender.sendMessage(messageManager.message("commands.reload.no-permission"));
            return;
        }
        if (reloadAction != null) {
            reloadAction.run();
            sender.sendMessage(messageManager.message("commands.reload.success"));
        } else {
            sender.sendMessage(messageManager.message("commands.reload.unsupported"));
        }
    }

    private void handleGui(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(messageManager.message("commands.gui.only-players"));
            return;
        }
        if (!hasPermission(sender, permissions.list())) {
            sender.sendMessage(messageManager.message("commands.gui.no-permission"));
            return;
        }
        gui.openMain(player);
    }

    private Countdown buildCountdown(String name, CountdownType type) {
        EnumSet<DisplayType> displayTypes = EnumSet.copyOf(defaults.displayTypes());
        Countdown countdown = new Countdown(name, type, displayTypes, defaults.updateIntervalSeconds(),
                defaults.visibilityPermission(), defaults.formatMessage(), defaults.startMessage(),
                defaults.endMessage(), java.util.List.of(), defaults.zoneId());
        countdown.setRunning(defaults.startOnCreate());
        return countdown;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(messageManager.message("commands.usage.header"));
        sender.sendMessage(messageManager.message("commands.usage.create"));
        sender.sendMessage(messageManager.message("commands.usage.start"));
        sender.sendMessage(messageManager.message("commands.usage.stop"));
        sender.sendMessage(messageManager.message("commands.usage.delete"));
        sender.sendMessage(messageManager.message("commands.usage.list"));
        sender.sendMessage(messageManager.message("commands.usage.info"));
        sender.sendMessage(messageManager.message("commands.usage.reload"));
    }

    private boolean hasPermission(CommandSender sender, String permission) {
        return permission == null || permission.isBlank() || sender.hasPermission(permission);
    }

}
