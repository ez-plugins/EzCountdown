package com.skyblockexp.ezcountdown.command.subcommand;

import org.bukkit.command.CommandSender;
import java.util.List;

public interface Subcommand {
    /** Primary name of the subcommand (e.g., "create") */
    String name();

    /** Execute the subcommand. Args include the subcommand token as args[0]. */
    void execute(CommandSender sender, String[] args);

    /** Tab completion suggestions for this subcommand. */
    default List<String> tabComplete(CommandSender sender, String[] args) { return java.util.List.of(); }
}
