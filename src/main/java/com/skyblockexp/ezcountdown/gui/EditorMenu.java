package com.skyblockexp.ezcountdown.gui;

import com.skyblockexp.ezcountdown.api.model.Countdown;
import com.skyblockexp.ezcountdown.command.CountdownPermissions;
import com.skyblockexp.ezcountdown.manager.CountdownManager;
import com.skyblockexp.ezcountdown.manager.MessageManager;
import com.skyblockexp.ezcountdown.util.TimeFormat;
import com.skyblockexp.ezcountdown.util.DurationParser;
import com.skyblockexp.ezcountdown.listener.ChatInputListener;
import com.skyblockexp.ezcountdown.api.model.CountdownType;
import com.skyblockexp.ezcountdown.display.DisplayType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import com.skyblockexp.ezcountdown.util.MaterialCompat;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.Instant;
import java.util.List;

public final class EditorMenu {
    private static final String PREFIX = ChatColor.DARK_AQUA + "Edit: ";

    private final CountdownManager manager;
    private final ChatInputListener chatInputListener;
    private final MessageManager messageManager;
    private final com.skyblockexp.ezcountdown.bootstrap.Registry registry;

    public EditorMenu(CountdownManager manager, ChatInputListener chatInputListener, MessageManager messageManager, com.skyblockexp.ezcountdown.bootstrap.Registry registry) {
        this.manager = manager;
        this.chatInputListener = chatInputListener;
        this.messageManager = messageManager;
        this.registry = registry;
    }

    public void openEditor(Player player, Countdown countdown) {
        String title = PREFIX + countdown.getName();
        Inventory inv = Bukkit.createInventory(null, 9, title);
        ItemStack run = new ItemStack(countdown.isRunning() ? MaterialCompat.resolve("LIME_CONCRETE", "LIME_WOOL", "WOOL") : MaterialCompat.resolve("RED_CONCRETE", "RED_WOOL", "WOOL"));
        ItemMeta rm = run.getItemMeta();
        rm.setDisplayName((countdown.isRunning() ? ChatColor.GREEN : ChatColor.RED) + "Toggle Running");
        rm.setLore(List.of(ChatColor.GRAY + "Currently: " + (countdown.isRunning() ? "Running" : "Stopped")));
        run.setItemMeta(rm);
        inv.setItem(0, run);

        ItemStack displays = new ItemStack(Material.ARMOR_STAND);
        ItemMeta dm2 = displays.getItemMeta();
        dm2.setDisplayName(ChatColor.AQUA + "Edit Display Types");
        dm2.setLore(List.of(ChatColor.GRAY + countdown.getDisplayTypes().toString()));
        displays.setItemMeta(dm2);
        inv.setItem(1, displays);

        ItemStack duration = new ItemStack(Material.CLOCK);
        ItemMeta dm = duration.getItemMeta();
        dm.setDisplayName(ChatColor.GOLD + "Edit Duration/Target");
        dm.setLore(List.of(ChatColor.GRAY + "Type: " + countdown.getType().name()));
        duration.setItemMeta(dm);
        inv.setItem(2, duration);

        ItemStack cmds = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta cm = cmds.getItemMeta();
        cm.setDisplayName(ChatColor.YELLOW + "Edit End Commands");
        cm.setLore(List.of(ChatColor.GRAY + "Commands: " + countdown.getEndCommands().size()));
        cmds.setItemMeta(cm);
        inv.setItem(3, cmds);

        ItemStack format = new ItemStack(Material.PAPER);
        ItemMeta fm = format.getItemMeta();
        fm.setDisplayName(ChatColor.YELLOW + "Edit Format Message");
        String resolvedFormat = messageManager.formatWithPrefix(countdown.getFormatMessage(), java.util.Map.of("name", countdown.getName()));
        fm.setLore(List.of(resolvedFormat == null ? "" : resolvedFormat));
        format.setItemMeta(fm);
        inv.setItem(4, format);

        ItemStack start = new ItemStack(MaterialCompat.resolve("FIREWORK_ROCKET", "FIREWORK"));
        ItemMeta sm = start.getItemMeta();
        sm.setDisplayName(ChatColor.AQUA + "Edit Start Message");
        String resolvedStart = messageManager.formatWithPrefix(countdown.getStartMessage(), java.util.Map.of("name", countdown.getName()));
        sm.setLore(List.of(resolvedStart == null ? "" : resolvedStart));
        start.setItemMeta(sm);
        inv.setItem(6, start);

        ItemStack auto = new ItemStack(Material.LEVER);
        ItemMeta am = auto.getItemMeta();
        boolean enabled = countdown.isAutoRestart();
        am.setDisplayName((enabled ? ChatColor.GREEN : ChatColor.RED) + "Auto Restart");
        am.setLore(List.of(ChatColor.GRAY + "Enabled: " + (enabled ? "true" : "false"), ChatColor.GRAY + "Toggle to restart on end"));
        auto.setItemMeta(am);
        inv.setItem(5, auto);

        ItemStack startTarget = new ItemStack(Material.NAME_TAG);
        ItemMeta stm = startTarget.getItemMeta();
        stm.setDisplayName(ChatColor.AQUA + "Start Countdown On End");
        String target = countdown.getStartCountdown() == null ? "(none)" : countdown.getStartCountdown();
        stm.setLore(List.of(ChatColor.GRAY + "Target: " + target, ChatColor.GRAY + "Click to set or clear"));
        startTarget.setItemMeta(stm);
        inv.setItem(7, startTarget);

        ItemStack end = new ItemStack(MaterialCompat.resolve("BELL", "PAPER"));
        ItemMeta em = end.getItemMeta();
        em.setDisplayName(ChatColor.RED + "Edit End Message");
        String resolvedEnd = messageManager.formatWithPrefix(countdown.getEndMessage(), java.util.Map.of("name", countdown.getName()));
        em.setLore(List.of(resolvedEnd == null ? "" : resolvedEnd));
        end.setItemMeta(em);
        inv.setItem(8, end);

        player.openInventory(inv);
    }

    public static String getPrefix() { return PREFIX; }
}
