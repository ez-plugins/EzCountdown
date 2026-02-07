package com.skyblockexp.ezcountdown.command.subcommand;

import com.skyblockexp.ezcountdown.bootstrap.Registry;
import org.bukkit.command.CommandSender;

public final class ReloadSubcommand implements Subcommand {
    private final Registry registry;
    private final Runnable reloadAction;

    public ReloadSubcommand(Registry registry, Runnable reloadAction) {
        this.registry = registry;
        this.reloadAction = reloadAction;
    }

    @Override
    public String name() { return "reload"; }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (reloadAction != null) {
            reloadAction.run();
            sender.sendMessage(registry.messages().message("commands.reload.success"));
        } else {
            sender.sendMessage(registry.messages().message("commands.reload.unsupported"));
        }
    }

    @Override
    public java.util.List<String> tabComplete(CommandSender sender, String[] args) {
        return java.util.List.of();
    }
}
