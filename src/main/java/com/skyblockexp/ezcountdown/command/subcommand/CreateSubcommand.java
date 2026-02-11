package com.skyblockexp.ezcountdown.command.subcommand;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.bootstrap.Registry;
import com.skyblockexp.ezcountdown.display.DisplayType;
import com.skyblockexp.ezcountdown.manager.CountdownManager;
import com.skyblockexp.ezcountdown.manager.MessageManager;
import com.skyblockexp.ezcountdown.type.CountdownTypeHandler;
import com.skyblockexp.ezcountdown.util.DurationParser;
import org.bukkit.command.CommandSender;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Map;

public final class CreateSubcommand implements Subcommand {
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final Registry registry;

    public CreateSubcommand(Registry registry) {
        this.registry = registry;
    }

    @Override
    public String name() { return "create"; }

    @Override
    public void execute(CommandSender sender, String[] args) {
        MessageManager messageManager = registry.messages();
        CountdownManager manager = registry.countdowns();

        if (!hasPermission(sender, registry.permissions().create())) {
            sender.sendMessage(messageManager.message("commands.create.no-permission"));
            return;
        }
        if (args.length < 3) {
            sender.sendMessage(messageManager.message("commands.create.usage"));
            return;
        }
        String name = args[1];
        String typeToken = args[2];
        CountdownType type;
        if ("recurring".equalsIgnoreCase(typeToken)) {
            type = CountdownType.RECURRING;
        } else if ("manual".equalsIgnoreCase(typeToken)) {
            type = CountdownType.MANUAL;
        } else if ("duration".equalsIgnoreCase(typeToken)) {
            type = CountdownType.DURATION;
        } else {
            type = CountdownType.FIXED_DATE;
        }

        // build countdown with defaults using the builder so we can set new alignment options
        EnumSet<DisplayType> displayTypes = EnumSet.copyOf(registry.defaults().displayTypes());
        com.skyblockexp.ezcountdown.api.model.CountdownBuilder builder = com.skyblockexp.ezcountdown.api.model.CountdownBuilder.builder(name)
            .type(type)
            .displayTypes(displayTypes)
            .updateIntervalSeconds(registry.defaults().updateIntervalSeconds())
            .visibilityPermission(registry.defaults().visibilityPermission())
            .formatMessage(registry.defaults().formatMessage())
            .startMessage(registry.defaults().startMessage())
            .endMessage(registry.defaults().endMessage())
            .endCommands(java.util.List.of())
            .zoneId(registry.defaults().zoneId())
            .autoRestart(false)
            .restartDelaySeconds(0);

        CountdownTypeHandler handler = registry.getHandler(type);
        Countdown countdown = null;
        try {
            String[] handlerArgs;
            if (type == CountdownType.FIXED_DATE) {
                handlerArgs = java.util.Arrays.copyOfRange(args, 2, args.length);
            } else {
                handlerArgs = java.util.Arrays.copyOfRange(args, 3, args.length);
            }
            if (handler != null) {
                // build a Countdown instance for handler to configure
                countdown = builder.build();
                countdown.setRunning(registry.defaults().startOnCreate());
                handler.configureFromCreateArgs(countdown, handlerArgs, registry.defaults());
            } else {
                if (type == CountdownType.FIXED_DATE) {
                    String date = typeToken;
                    String time = args.length > 3 ? args[3] : "00:00";
                    try {
                        LocalDateTime parsed = LocalDateTime.parse(date + " " + time, DATE_TIME_FORMAT);
                        ZoneId zoneId = registry.defaults().zoneId();
                        countdown = builder.build();
                        countdown.setTargetInstant(parsed.atZone(zoneId).toInstant());
                        countdown.setRunning(registry.defaults().startOnCreate());
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
                        countdown = builder.build();
                        countdown.setDurationSeconds(DurationParser.parseToSeconds(args[3]));
                    } catch (IllegalArgumentException ex) {
                        sender.sendMessage(messageManager.message("commands.create.invalid-duration", Map.of("reason", ex.getMessage())));
                        return;
                    }
                    if (type == CountdownType.DURATION) countdown.setRunning(registry.defaults().startOnCreate());
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
                    // apply recurring fields to builder first so builder-based options are included
                    builder.recurringDate(month, day, time);
                    // parse any additional flags (optional)
                    for (int i = 6; i < args.length; i++) {
                        String tok = args[i];
                        if ("--align-to-clock".equalsIgnoreCase(tok)) {
                            builder.alignToClock(true);
                        } else if ("--align-interval".equalsIgnoreCase(tok) && i + 1 < args.length) {
                            builder.alignInterval(args[++i]);
                        } else if ("--timezone".equalsIgnoreCase(tok) && i + 1 < args.length) {
                            try {
                                builder.zoneId(java.time.ZoneId.of(args[++i]));
                            } catch (Exception ex) {
                                sender.sendMessage(messageManager.message("commands.create.invalid-timezone"));
                                return;
                            }
                        } else if ("--missed-run-policy".equalsIgnoreCase(tok) && i + 1 < args.length) {
                            try {
                                builder.missedRunPolicy(com.skyblockexp.ezcountdown.api.model.MissedRunPolicy.valueOf(args[++i].toUpperCase(java.util.Locale.ROOT)));
                            } catch (IllegalArgumentException ex) {
                                sender.sendMessage(messageManager.message("commands.create.invalid-missed-run-policy"));
                                return;
                            }
                        }
                    }
                    countdown = builder.build();
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

    private boolean hasPermission(CommandSender sender, String permission) {
        return permission == null || permission.isBlank() || sender.hasPermission(permission);
    }

    @Override
    public java.util.List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 3) {
            return java.util.List.of("manual", "recurring", "duration", "2026-01-01").stream()
                    .filter(entry -> entry.startsWith(args[2].toLowerCase(Locale.ROOT)))
                    .toList();
        }
        return java.util.List.of();
    }
}
